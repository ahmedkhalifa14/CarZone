package com.example.carzoneapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.ActivityMainBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val homeViewModel: MainViewModel by viewModels()
    private var launch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            launch
        }
        subscribeToObservables()
        homeViewModel.isFirstTimeLaunch()

        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        navController = findNavController(R.id.navHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.myAdsFragment,
                R.id.accountFragment
                -> binding.bottomNavigation.isVisible = true

                else -> binding.bottomNavigation.isVisible = false
            }
        }

    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.isFirstTimeLaunchState.collect(
                    EventObserver(onLoading = {},
                        onSuccess = { flow ->
                            flow.asLiveData().observe(this@MainActivity) {launchInfo->
                                if (launchInfo.isFirstTimeLaunch&&launchInfo.isLogin) {
                                    launch = true
                                    navController.navigate(R.id.homeFragment)
                                } else if (!launchInfo.isFirstTimeLaunch && !launchInfo.isLogin) {
                                    launch = true
                                    navController.navigate(R.id.onBoardingFragment)
                                }
                                else{
                                    launch = true
                                    navController.navigate(R.id.startFragment)
                                }
                            }

                        },
                        onError = {
                            Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                        })
                )
            }
        }
    }




}
