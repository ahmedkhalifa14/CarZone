package com.example.carzoneapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.AdsAdapter
import com.example.carzoneapp.adapters.CategoryAdapter
import com.example.carzoneapp.databinding.FragmentHomeBinding
import com.example.carzoneapp.ui.viewmodel.HomeViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var adsAdapter: AdsAdapter
    private lateinit var adsRecyclerView: RecyclerView
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryRecyclerView()
        setupAdsRecyclerView()
        subscribeToObservables()
        adsAdapter.setOnItemClickListener { ad->
            val action = HomeFragmentDirections.actionHomeFragmentToAdDetailsFragment(ad)
            findNavController().navigate(action)
        }


    }


    override fun onResume() {
        super.onResume()
        homeViewModel.getAllAds()
        homeViewModel.getAllVehiclesCategories()
    }

    private fun setupCategoryRecyclerView() {
        categoryRecyclerView = binding!!.categoryRv
        categoryAdapter = CategoryAdapter(1)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupAdsRecyclerView() {
        adsRecyclerView = binding!!.adsRv
        adsAdapter = AdsAdapter()
        adsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adsRecyclerView.adapter = adsAdapter
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.vehiclesCategoriesState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.categoryShimmerLayout.isVisible = true
                            binding!!.categoryShimmerLayout.startShimmerAnimation()
                        },
                        onSuccess = { vehiclesCategoriesList ->
                            binding!!.categoryShimmerLayout.isVisible = false
                            binding!!.categoryShimmerLayout.stopShimmerAnimation()
                            categoryAdapter.differ.submitList(vehiclesCategoriesList)
                        },
                        onError = {
                            binding!!.categoryShimmerLayout.isVisible = false
                            binding!!.categoryShimmerLayout.stopShimmerAnimation()
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allAdsState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.adsShimmerLayout.isVisible = true
                            binding!!.adsShimmerLayout.startShimmerAnimation()
                        },
                        onSuccess = { adsList ->
                            binding!!.adsShimmerLayout.stopShimmerAnimation()
                            binding!!.adsShimmerLayout.isVisible = false
                            adsAdapter.differ.submitList(adsList)
                        },
                        onError = {
                            binding!!.adsShimmerLayout.stopShimmerAnimation()
                            binding!!.adsShimmerLayout.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }

        }
    }

}


