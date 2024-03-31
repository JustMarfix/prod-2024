package com.smmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smmanager.databinding.ActivityMainBinding
import com.smmanager.web_view.SMMYaWebViewClient

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        setupBackPressOverriding()
    }

    private fun setupWebView() {
        binding.webView.settings.apply {
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        binding.webView.webViewClient = SMMYaWebViewClient()
        binding.webView.loadUrl(BuildConfig.SMMYa_URL)
    }

    private fun setupBackPressOverriding() {
        onBackPressedDispatcher.addCallback {
            if (binding.webView.isFocused && binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        }
    }
}