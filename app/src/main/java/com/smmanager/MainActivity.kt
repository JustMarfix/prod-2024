package com.smmanager

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.smmanager.databinding.ActivityMainBinding
import com.smmanager.web_view.SMMYaWebViewClient
import com.smmanager.web_view.WebViewState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private val webViewClient = SMMYaWebViewClient(
        this::handleSwipeRefreshing
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        handleOrientationChanges()

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setupSplashScreenAnimation(splashScreen)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        setupBackPressOverriding()
        setupSwipeRefreshing()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                webViewClient.webViewState.collectLatest { state ->
                    handleWebViewState(state)
                }
            }
        }
    }

    private fun handleWebViewState(state: WebViewState) {
        handleSwipeRefreshing(false)
        when (state) {
            is WebViewState.Content -> {
                binding.webView.visibility = View.VISIBLE
                binding.errorComponent.visibility = View.GONE
            }

            is WebViewState.SomeError -> {
                binding.webView.visibility = View.GONE
                binding.errorComponent.visibility = View.VISIBLE
                binding.errorText.text = resources.getText(R.string.some_error)
            }

            is WebViewState.HttpError -> {
                binding.webView.visibility = View.GONE
                binding.errorComponent.visibility = View.VISIBLE
                binding.errorText.text = resources.getText(R.string.some_error)
            }
        }
    }


    private fun setupSwipeRefreshing() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            webViewClient.setRefreshingToTrue()
            binding.webView.reload()
        }
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

        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.swipeRefreshLayout.isEnabled = scrollY == 0
        }
        binding.webView.webViewClient = webViewClient
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

    private fun handleSwipeRefreshing(isEnabled: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isEnabled
    }
}