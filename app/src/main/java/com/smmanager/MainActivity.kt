package com.smmanager

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smmanager.databinding.ActivityMainBinding
import com.smmanager.web_view.SMMYaWebViewClient

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        handleOrientationChanges()

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setupSplashScreenAnimation(splashScreen)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        setupBackPressOverriding()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun handleOrientationChanges() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_HINGE_ANGLE)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun setupSplashScreenAnimation(splashScreen: SplashScreen) {
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val scaleXAnimator = ObjectAnimator.ofFloat(
                splashScreenViewProvider.iconView, View.SCALE_X, 0.0f, 1f
            )

            val scaleYAnimator = ObjectAnimator.ofFloat(
                splashScreenViewProvider.iconView, View.SCALE_Y, 0.0f, 1f
            )

            val rotationAnimator = ObjectAnimator.ofFloat(
                splashScreenViewProvider.iconView, View.ROTATION, 0f, 360f
            )
            val animatorSet = AnimatorSet()
            animatorSet.apply {
                playTogether(scaleYAnimator, scaleXAnimator, rotationAnimator)
                duration = 800L
                doOnEnd {
                    splashScreenViewProvider.remove()
                }
                start()
            }
        }
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

        binding.webView.webViewClient = SMMYaWebViewClient(
            onErrorAction = this::handleWebViewErrors
        )
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

    private fun handleWebViewErrors(errorCode: Int) {
        when (errorCode) {
            WebViewClient.ERROR_HOST_LOOKUP -> {
                binding.errorText.text = resources.getText(R.string.some_error)
                binding.errorComponent.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
            }

            else -> {
                binding.errorText.text = resources.getText(R.string.some_error)
                binding.errorComponent.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
            }
        }
    }
}