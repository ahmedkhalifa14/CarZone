package com.example.carzoneapp.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.carzoneapp.R
import com.example.carzoneapp.adapters.OnBoardingViewPagerAdapter
import com.example.carzoneapp.databinding.FragmentOnBoardingBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.LaunchInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding

    private val homeViewModel: MainViewModel by viewModels()

    companion object {
        const val MAX_STEP = 4
    }


    private val navOptions =
        NavOptions.Builder()
            .setPopUpTo(R.id.onBoardingFragment, true)
            .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        binding!!.viewPager.adapter = OnBoardingViewPagerAdapter()
        binding!!.dotsIndicator.attachTo(binding!!.viewPager)
        binding!!.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == MAX_STEP - 1) {
                    binding!!.nextBtn.text = getString(R.string.get_started)
                    binding!!.nextBtn.contentDescription =
                        getString(R.string.get_started)
                    binding!!.nextBtn.setOnClickListener {
                        homeViewModel.saveFirstTimeLaunch(
                            LaunchInfo(
                                isFirstTimeLaunch = true,
                                isLogin = false
                            )
                        )

                    }
                } else {
                    binding!!.nextBtn.text = getString(R.string.next)
                    binding!!.nextBtn.contentDescription = getString(R.string.next)
                }
            }
        })
        binding!!.skipBtn.setOnClickListener {
            homeViewModel.saveFirstTimeLaunch(
                LaunchInfo(
                    isFirstTimeLaunch = true,
                    isLogin = false
                )
            )


        }
        binding!!.nextBtn.setOnClickListener {
            if (binding!!.nextBtn.text.toString() == getString(R.string.get_started)) {
                homeViewModel.saveFirstTimeLaunch(
                    LaunchInfo(
                        isFirstTimeLaunch = true,
                        isLogin = false
                    )
                )

            } else {
                val current = (binding!!.viewPager.currentItem) + 1
                binding!!.viewPager.currentItem = current
                if (current >= MAX_STEP - 1) {
                    binding!!.nextBtn.text = getString(R.string.get_started)
                    binding!!.nextBtn.contentDescription =
                        getString(R.string.get_started)
                    binding!!.nextBtn.setOnClickListener {
                        homeViewModel.saveFirstTimeLaunch(
                            LaunchInfo(
                                isFirstTimeLaunch = true,
                                isLogin = false
                            )
                        )
                    }
                } else {
                    binding!!.nextBtn.text = getString(R.string.next)
                    binding!!.nextBtn.contentDescription = getString(R.string.next)
                }
            }
        }


    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.saveFirstTimeLaunchState.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = {
                            findNavController().navigate(R.id.startFragment, null, navOptions)
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }
}