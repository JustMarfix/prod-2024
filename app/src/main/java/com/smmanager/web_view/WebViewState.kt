package com.smmanager.web_view

sealed class WebViewState(open val isLoading: Boolean) {

    data class Content(override val isLoading: Boolean) : WebViewState(isLoading)

    data class SomeError(val errorCode: Int, override val isLoading: Boolean) :
        WebViewState(isLoading)

    data class HttpError(val errorCode: Int, override val isLoading: Boolean) :
        WebViewState(isLoading)
}