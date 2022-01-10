package com.sanjayprajapat.in_app_purchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.sanjayprajapat.in_app_purchase.databinding.ActivityMainBinding

/**
 * @author: Sanjay Prajapat
 * time : 09 -1 -2022
 * */

class MainActivity : BaseActivity() {
    private var skuDetails :SkuDetails? = null
    private var binding:ActivityMainBinding? = null
    private var billingClient:BillingClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.apply {
            setContentView(this.root)
            executePendingBindings()
            lifecycleOwner = this@MainActivity
        }
        init()
    }

    override fun initArguments() {
        setUpBillingClient()
    }

    override fun initViews() {
    }

    override fun setupListener() {
        binding?.txtProductBuy?.setOnClickListener {
            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                billingClient?.launchBillingFlow(this, billingFlowParams)?.responseCode
            }?:showToast("No SKU mESSAge")
        }
    }

    override fun initObservers() {
    }

    override fun loadData() {
    }

    /**
     * get our callbacks related to any purchases initiated
     * */
    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.v("TAG_INAPP","billingResult responseCode : ${billingResult.responseCode}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
//                        handleNonConcumablePurchase(purchase)
                    handleConsumedPurchases(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }

    /**
     * initialize billing client
     * */
    private fun setUpBillingClient() {
         billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()
        startConnection()
    }

    /**
     * Start the connection on the billing-client instance
     * */
    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when(billingResult.responseCode){
                    BillingClient.BillingResponseCode.OK ->{
                        Log.v("TAG_INAPP","Setup Billing Done")
                        // The BillingClient is ready. You can query purchases here.
                        //launching the purchase flow or a query about our products
                        queryAvailableProducts()
                    }
                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE ->{
                        Log.v("TAG_INAPP","Item Unavailable")
                    }
                    BillingClient.BillingResponseCode.ERROR ->{
                        Log.v("TAG_INAPP","Item Error")
                    }
                }

            }
            override fun onBillingServiceDisconnected() {
                Log.v("TAG_INAPP","Billing client Disconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }
    private fun queryAvailableProducts(){
        val skuList= ArrayList<String>()
        skuList.add("1001")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                for (skuDetails in skuDetailsList) {
                    Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")
                    //This list should contain the products added above
                    updateUI(skuDetails)
                }
            }
        }
    }


    private fun updateUI(skuDetails: SkuDetails?) {
        skuDetails?.let {
            this.skuDetails = it
            binding?.txtProductName?.text = skuDetails.title
            binding?.txtProductDescription?.text = skuDetails.description
            showUIElements()
        }
    }

    private fun showUIElements() {
        binding?.txtProductName?.visibility = View.VISIBLE
        binding?.txtProductDescription?.visibility = View.VISIBLE
        binding?.txtProductBuy?.visibility = View.VISIBLE
    }
    private fun handleConsumedPurchases(purchase: Purchase) {
        Log.d("TAG_INAPP", "handleConsumablePurchasesAsync foreach it is $purchase")
        val params =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.consumeAsync(params) { billingResult, purchaseToken ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // Update the appropriate tables/databases to grant user the items
                    Log.d(
                        "TAG_INAPP",
                        " Update the appropriate tables/databases to grant user the items"
                    )
                }
                else -> {
                    Log.w("TAG_INAPP", billingResult.debugMessage)
                }
            }
        }
    }

    private fun handleNonConcumablePurchase(purchase: Purchase) {
        Log.v("TAG_INAPP","handlePurchase : ${purchase}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage

                    Log.v("TAG_INAPP","response code: $billingResponseCode")
                    Log.v("TAG_INAPP","debugMessage : $billingDebugMessage")

                }
            }
        }
    }

}