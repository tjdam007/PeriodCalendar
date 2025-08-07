package com.mjandroiddev.periodcalendar.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    private val context: Context,
    private val analyticsLogger: AnalyticsLogger
) : PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        // Donation product IDs - these must match what you create in Google Play Console
        const val SUPPORT_TIER_1 = "support_tier_1" // ₹29
        const val SUPPORT_TIER_2 = "support_tier_2" // ₹99  
        const val SUPPORT_TIER_3 = "support_tier_3" // ₹199
        
        val DONATION_PRODUCT_IDS = listOf(
            SUPPORT_TIER_1,
            SUPPORT_TIER_2,
            SUPPORT_TIER_3
        )
    }

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val _connectionState = MutableStateFlow(BillingConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BillingConnectionState> = _connectionState.asStateFlow()

    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails.asStateFlow()

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult.asStateFlow()

    init {
        startConnection()
    }

    private fun startConnection() {
        _connectionState.value = BillingConnectionState.CONNECTING
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _connectionState.value = BillingConnectionState.CONNECTED
            queryProductDetails()
        } else {
            _connectionState.value = BillingConnectionState.ERROR
            analyticsLogger.logError(
                RuntimeException("Billing setup failed: ${billingResult.debugMessage}"),
                "Billing response code: ${billingResult.responseCode}"
            )
        }
    }

    override fun onBillingServiceDisconnected() {
        _connectionState.value = BillingConnectionState.DISCONNECTED
        // Try to restart the connection on the next request
    }

    private fun queryProductDetails() {
        val productList = DONATION_PRODUCT_IDS.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = productDetailsList.productDetailsList
                
                // Track product details loaded for analytics
                analyticsLogger.logError(
                    RuntimeException("Product details loaded"),
                    "Found ${productDetailsList.productDetailsList.size} products: ${productDetailsList.productDetailsList.map { it.productId }}"
                )
            } else {
                analyticsLogger.logError(
                    RuntimeException("Failed to query product details: ${billingResult.debugMessage}"),
                    "Response code: ${billingResult.responseCode}"
                )
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Track donation attempt
        trackDonationAttempt(productDetails)

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseResult.value = PurchaseResult.Error("Failed to launch billing flow: ${billingResult.debugMessage}")
            
            analyticsLogger.logError(
                RuntimeException("Failed to launch billing flow"),
                "Product: ${productDetails.productId}, Response: ${billingResult.responseCode}"
            )
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseResult.value = PurchaseResult.Cancelled
                trackDonationResult("cancelled", null, null)
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                _purchaseResult.value = PurchaseResult.Error("You have already purchased this item")
                trackDonationResult("already_owned", null, null)
            }
            else -> {
                _purchaseResult.value = PurchaseResult.Error("Purchase failed: ${billingResult.debugMessage}")
                trackDonationResult("error", null, billingResult.debugMessage)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge the purchase (required for all one-time products)
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _purchaseResult.value = PurchaseResult.Success(purchase)
                        
                        // Track successful donation
                        val productId = purchase.products.firstOrNull()
                        val amount = getAmountForProductId(productId)
                        trackDonationResult("success", amount, productId)
                        
                    } else {
                        _purchaseResult.value = PurchaseResult.Error("Failed to acknowledge purchase")
                        analyticsLogger.logError(
                            RuntimeException("Failed to acknowledge purchase"),
                            "Purchase token: ${purchase.purchaseToken}"
                        )
                    }
                }
            } else {
                _purchaseResult.value = PurchaseResult.Success(purchase)
                
                // Track successful donation
                val productId = purchase.products.firstOrNull()
                val amount = getAmountForProductId(productId)
                trackDonationResult("success", amount, productId)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            _purchaseResult.value = PurchaseResult.Pending
            trackDonationResult("pending", null, null)
        }
    }

    private fun getAmountForProductId(productId: String?): String? {
        return when (productId) {
            SUPPORT_TIER_1 -> "29"
            SUPPORT_TIER_2 -> "99"
            SUPPORT_TIER_3 -> "199"
            else -> null
        }
    }

    private fun trackDonationAttempt(productDetails: ProductDetails) {
        try {
            analyticsLogger.logEvent("donation_attempt") {
                param("product_id", productDetails.productId)
                param("price", productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: "unknown")
                param("currency", productDetails.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: "unknown")
            }
        } catch (e: Exception) {
            analyticsLogger.logError(e, "Error tracking donation attempt")
        }
    }

    private fun trackDonationResult(result: String, amount: String?, productId: String?) {
        try {
            analyticsLogger.logEvent("donation_result") {
                param("result", result)
                amount?.let { param("amount", it) }
                productId?.let { param("product_id", it) }
            }
        } catch (e: Exception) {
            analyticsLogger.logError(e, "Error tracking donation result")
        }
    }

    fun clearPurchaseResult() {
        _purchaseResult.value = null
    }

    fun reconnectIfNeeded() {
        if (_connectionState.value != BillingConnectionState.CONNECTED) {
            startConnection()
        }
    }

    fun endConnection() {
        billingClient.endConnection()
        _connectionState.value = BillingConnectionState.DISCONNECTED
    }

    sealed class PurchaseResult {
        data class Success(val purchase: Purchase) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
        object Cancelled : PurchaseResult()
        object Pending : PurchaseResult()
    }

    enum class BillingConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }
}

// Extension function to add parameters to Firebase Analytics
private fun AnalyticsLogger.logEvent(eventName: String, params: (android.os.Bundle.() -> Unit)? = null) {
    try {
        val bundle = android.os.Bundle()
        params?.invoke(bundle)
        // This would use your existing analytics logging method
        // For now, we'll log it as a custom message
        this.logError(
            RuntimeException("Analytics Event: $eventName"),
            "Parameters: ${bundle.keySet().joinToString { "$it=${bundle.get(it)}" }}"
        )
    } catch (e: Exception) {
        // Fail silently
    }
}

private fun android.os.Bundle.param(key: String, value: String) {
    putString(key, value)
}