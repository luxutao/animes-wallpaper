package me.m123.image.ui

import android.os.Bundle
import android.webkit.WebView
import me.m123.image.R

class LicenseActivity: BaseAAppCompatActivity() {

    private lateinit var WebLicense: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.WebLicense = this.findViewById(R.id.loading_license)
        this.WebLicense.loadUrl("https://pan.123m.me/public/LICENSE-2.0.txt")
        val webSettings = this.WebLicense.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.textZoom = 150

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_license
    }
}