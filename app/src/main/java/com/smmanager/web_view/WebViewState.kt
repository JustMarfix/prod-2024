package com.smmanager.web_view

sealed class WebViewState {

    data object Content : WebViewState()

    data class SomeError(val errorCode: Int) : WebViewState()

    data class HttpError(val errorCode: Int) : WebViewState()
}