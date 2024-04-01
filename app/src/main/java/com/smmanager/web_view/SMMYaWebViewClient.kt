package com.smmanager.web_view

import android.app.Activity
import android.content.Intent
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.smmanager.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SMMYaWebViewClient(
    private val onRefreshingUpdate: (Boolean) -> Unit,
) : WebViewClient() {

    private val _webViewState = MutableStateFlow<WebViewState>(WebViewState.Content)
    val webViewState = _webViewState.asStateFlow()
    private var isRefreshing = false

    fun setRefreshingToTrue() {
        isRefreshing = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (isRefreshing) {
            isRefreshing = false
            _webViewState.update {
                WebViewState.Content
            }
        }
        onRefreshingUpdate(isRefreshing)
    }

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
        isRefreshing = false
        if (error != null) {
            _webViewState.update {
                WebViewState.SomeError(error.errorCode)
            }
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        isRefreshing = false
        if (errorResponse != null && errorResponse.statusCode in 500..599) {
            _webViewState.update {
                WebViewState.HttpError(errorResponse.statusCode)
            }
        }
    }
}