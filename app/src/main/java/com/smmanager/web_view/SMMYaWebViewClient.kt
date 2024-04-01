package com.smmanager.web_view

import android.app.Activity
import android.content.Intent
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.smmanager.BuildConfig

class SMMYaWebViewClient(
    private val onErrorAction: (Int) -> Unit = {}
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request != null && request.url.host == BuildConfig.SMMYa_URL_HOST) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.acceptThirdPartyCookies(view)
            return false
        }

        if (request != null) {
            Intent(Intent.ACTION_VIEW, request.url).apply {
                ContextCompat.startActivity(view!!.context as Activity, this, null)
            }
        }
        return true
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        if (error != null) {
            onErrorAction(error.errorCode)
        }
    }
}