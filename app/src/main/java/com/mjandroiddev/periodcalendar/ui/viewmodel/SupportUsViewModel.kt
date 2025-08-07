package com.mjandroiddev.periodcalendar.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.mjandroiddev.periodcalendar.billing.BillingManager
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportUsViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUsUiState())
    val uiState: StateFlow<SupportUsUiState> = _uiState.asStateFlow()

    init {
        observeBillingState()
        analyticsLogger.setCurrentScreen("support_us")
        
        // Track screen view
        trackSupportScreenView()
    }

    private fun observeBillingState() {
        viewModelScope.launch {
            // Observe connection state
            billingManager.connectionState.collect { connectionState ->
                _uiState.value = _uiState.value.copy(
                    isLoading = connectionState == BillingManager.BillingConnectionState.CONNECTING,
                    hasError = connectionState == BillingManager.BillingConnectionState.ERROR
                )
            }
        }

        viewModelScope.launch {
            // Observe product details
            billingManager.productDetails.collect { productDetails ->
                _uiState.value = _uiState.value.copy(
                    donationTiers = mapProductDetailsToTiers(productDetails),
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            // Observe purchase results
            billingManager.purchaseResult.collect { result ->
                when (result) {
                    is BillingManager.PurchaseResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            showThankYou = true,
                            isProcessingPurchase = false
                        )
                        trackDonationSuccess(result.purchase.products.firstOrNull())
                    }
                    is BillingManager.PurchaseResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message,
                            isProcessingPurchase = false
                        )
                        trackDonationError(result.message)
                    }
                    is BillingManager.PurchaseResult.Cancelled -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessingPurchase = false
                        )
                        trackDonationCancelled()
                    }
                    is BillingManager.PurchaseResult.Pending -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Purchase is pending. Please wait for confirmation.",
                            isProcessingPurchase = false
                        )
                    }
                    null -> {
                        // No result yet
                    }
                }
            }
        }
    }

    private fun mapProductDetailsToTiers(productDetails: List<ProductDetails>): List<DonationTier> {
        return productDetails.mapNotNull { product ->
            when (product.productId) {
                BillingManager.SUPPORT_TIER_1 -> DonationTier(
                    id = product.productId,
                    title = "Coffee Supporter",
                    description = "Buy me a coffee to keep the app running!",
                    amount = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₹29",
                    productDetails = product,
                    benefits = listOf("Support development", "Show your appreciation"),
                    icon = "☕"
                )
                BillingManager.SUPPORT_TIER_2 -> DonationTier(
                    id = product.productId,
                    title = "App Enthusiast",
                    description = "Help me add new features and improvements!",
                    amount = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₹99",
                    productDetails = product,
                    benefits = listOf("Support new features", "Priority bug fixes", "Your name in credits"),
                    icon = "⭐"
                )
                BillingManager.SUPPORT_TIER_3 -> DonationTier(
                    id = product.productId,
                    title = "Super Supporter",
                    description = "Become a champion of women's health tech!",
                    amount = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₹199",
                    productDetails = product,
                    benefits = listOf(
                        "Priority feature requests", 
                        "Early access to beta features",
                        "Direct developer contact",
                        "Special thanks in app"
                    ),
                    icon = "💎"
                )
                else -> null
            }
        }.sortedBy { 
            when (it.id) {
                BillingManager.SUPPORT_TIER_1 -> 1
                BillingManager.SUPPORT_TIER_2 -> 2
                BillingManager.SUPPORT_TIER_3 -> 3
                else -> 999
            }
        }
    }

    fun onDonationTierSelected(activity: Activity, tier: DonationTier) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessingPurchase = true)
            billingManager.launchBillingFlow(activity, tier.productDetails)
            
            // Track tier selection
            trackDonationTierSelected(tier)
        }
    }

    fun onRetryConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, hasError = false)
            billingManager.reconnectIfNeeded()
        }
    }

    fun dismissThankYou() {
        _uiState.value = _uiState.value.copy(showThankYou = false)
        billingManager.clearPurchaseResult()
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
        billingManager.clearPurchaseResult()
    }

    // Analytics tracking methods
    private fun trackSupportScreenView() {
        try {
            analyticsLogger.logError(
                RuntimeException("Screen View: Support Us"),
                "User viewed the support/donation screen"
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }

    private fun trackDonationTierSelected(tier: DonationTier) {
        try {
            analyticsLogger.logError(
                RuntimeException("Donation Tier Selected"),
                "Tier: ${tier.title}, Amount: ${tier.amount}, ID: ${tier.id}"
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }

    private fun trackDonationSuccess(productId: String?) {
        try {
            analyticsLogger.logError(
                RuntimeException("Donation Successful"),
                "Product ID: $productId"
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }

    private fun trackDonationError(error: String) {
        try {
            analyticsLogger.logError(
                RuntimeException("Donation Error"),
                "Error: $error"
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }

    private fun trackDonationCancelled() {
        try {
            analyticsLogger.logError(
                RuntimeException("Donation Cancelled"),
                "User cancelled the donation flow"
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Don't end connection here as it might be used by other screens
    }
}

data class SupportUsUiState(
    val donationTiers: List<DonationTier> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val isProcessingPurchase: Boolean = false,
    val showThankYou: Boolean = false,
    val errorMessage: String? = null
)

data class DonationTier(
    val id: String,
    val title: String,
    val description: String,
    val amount: String,
    val productDetails: ProductDetails,
    val benefits: List<String>,
    val icon: String
)