# 💰 Period Calendar Monetization Setup Guide

This guide walks you through setting up Google Play Billing for donation-based monetization in the Period Calendar app.

## 🎯 Overview

The app includes a complete monetization system with:
- **Rating & Support Dialog**: Automatically prompts users after 7 days of usage
- **3 Donation Tiers**: ₹29, ₹99, ₹199 with different benefits
- **Google Play Billing Integration**: Secure in-app purchases
- **Firebase Analytics**: Track donation conversion and user behavior

## 📋 Prerequisites

1. ✅ Google Play Developer Account
2. ✅ App published on Google Play Console  
3. ✅ Firebase project configured (already done)
4. ✅ Release signing configured

## 🛠 Google Play Console Setup

### Step 1: Create In-App Products

1. Go to **Google Play Console** → Your App → **Monetization** → **In-app products**
2. Click **Create Product** for each tier:

#### Tier 1: Coffee Supporter (₹29)
- **Product ID**: `support_tier_1`
- **Name**: `Coffee Supporter`
- **Description**: `Buy me a coffee to keep the app running! Support development and show your appreciation.`
- **Price**: ₹29.00 (or equivalent in your currency)

#### Tier 2: App Enthusiast (₹99) 
- **Product ID**: `support_tier_2`
- **Name**: `App Enthusiast`
- **Description**: `Help me add new features and improvements! Priority bug fixes and your name in credits.`
- **Price**: ₹99.00 (or equivalent in your currency)

#### Tier 3: Super Supporter (₹199)
- **Product ID**: `support_tier_3` 
- **Name**: `Super Supporter`
- **Description**: `Become a champion of women's health tech! Priority feature requests, early beta access, and direct developer contact.`
- **Price**: ₹199.00 (or equivalent in your currency)

### Step 2: Activate Products

1. After creating all products, click **Activate** for each one
2. Products must be activated to appear in your app

### Step 3: Test Purchases (Optional)

1. Add test accounts in **Play Console** → **Setup** → **License Testing**
2. Upload a signed APK/AAB to Internal Testing track
3. Test purchases before releasing to production

## 🔧 Technical Implementation

The monetization system is already implemented with these key components:

### BillingManager (`/app/src/main/java/.../billing/BillingManager.kt`)
- Handles Google Play Billing connection
- Manages product details and purchases  
- Includes comprehensive error handling and analytics

### SupportUsScreen (`/app/src/main/java/.../ui/screens/SupportUsScreen.kt`)
- Beautiful Material 3 donation interface
- Dynamic pricing from Play Store
- Success/error handling with user feedback

### RatingAndSupportDialog (`/app/src/main/java/.../ui/components/RatingAndSupportDialog.kt`)
- Smart timing: Shows after 7 days, respects user preferences
- Promotes both rating and donations
- Respects "Don't show again" choice

## 📊 Analytics Tracking

The app automatically tracks:
- `donation_attempt`: When user clicks a donation tier
- `donation_result`: Success/failure/cancellation
- `rating_dialog_interaction`: User interaction with rating dialog
- Firebase Crashlytics integration for debugging

## 🚀 Release Process

1. **Build Release APK/AAB**:
   ```bash
   ./gradlew bundleProductionRelease
   ```

2. **Upload to Play Console**:
   - Upload the signed AAB to Production track
   - Complete store listing if not done

3. **Verify In-App Products**:
   - Ensure all 3 products are created and activated
   - Test on internal testing track first

4. **Monitor Performance**:
   - Check Firebase Analytics for donation metrics
   - Monitor Crashlytics for any billing issues

## 🎨 Customization

### Modify Donation Amounts
Edit the product prices in Google Play Console. The app will automatically reflect the new prices.

### Change Dialog Timing
In `RatingAndSupportDialog.kt`, modify:
```kotlin
// Show after 7 days if not shown before, or after 30 days if shown before
val daysToWait = if (lastShown == 0L) 7 else 30
```

### Add More Tiers
1. Add new product IDs in `BillingManager.DONATION_PRODUCT_IDS`
2. Create corresponding products in Play Console
3. Update `SupportUsViewModel.mapProductDetailsToTiers()`

## 🔒 Security Features

- **ProGuard Protection**: Billing classes are protected from reverse engineering
- **Purchase Validation**: All purchases are acknowledged properly
- **Error Handling**: Graceful fallbacks for network/billing issues
- **Privacy Focused**: No personal data collection, only anonymous analytics

## 📈 Expected User Flow

1. **Day 1-6**: User uses app normally
2. **Day 7**: Rating & Support dialog appears
3. **User Options**:
   - **Rate 5 stars**: Opens Play Store
   - **Support Development**: Opens SupportUs screen
   - **Not Now**: Shows again after 30 days
   - **Don't Show Again**: Permanently dismissed

## 💡 Best Practices

1. **Never be pushy**: The dialog respects user choice
2. **Provide value first**: Only ask for support after user gets value
3. **Multiple options**: Rating is free, donations are optional
4. **Transparency**: Clear about what donations support
5. **Privacy commitment**: Reinforced throughout the app

## 🐛 Troubleshooting

### Common Issues:

**Products not loading**: 
- Verify product IDs match exactly
- Ensure products are activated in Play Console
- Check app signing certificate matches

**Test purchases not working**:
- Add test account to Play Console
- Install from Internal Testing track
- Clear app data before testing

**Billing connection fails**:
- Check internet connection
- Verify Google Play Services is updated
- Check Firebase Analytics for specific errors

## 📞 Support

The monetization system includes comprehensive error reporting through Firebase Crashlytics. Monitor the Firebase console for any billing-related issues and user feedback.

---

**🎉 Success!** Your app now has a complete, ethical monetization system that respects user privacy while enabling sustainable development funding.