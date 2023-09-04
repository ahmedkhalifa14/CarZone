package com.example.carzoneapp.ui.fragments.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.ChooseLocationAdapter
import com.example.carzoneapp.databinding.FragmentChooseLocationBinding
import com.example.carzoneapp.ui.viewmodel.HomeViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChooseLocationFragment : Fragment() {
    private var _binding: FragmentChooseLocationBinding? = null
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var chooseLocationAdapter: ChooseLocationAdapter
    private lateinit var chooseLocationRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseLocationBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        setupChooseLocationRecyclerView()
        homeViewModel.fetchRegionsInCountry("ahmedkhalifa14", "Eg", "ADM1")
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.fetchRegionsInCountryState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.chooseLocationFragmentShimmerLayout.isVisible = true
                            binding!!.chooseLocationFragmentShimmerLayout.startShimmerAnimation()
                        },
                        onSuccess = {
                            binding!!.chooseLocationFragmentShimmerLayout.isVisible = false
                            binding!!.chooseLocationFragmentShimmerLayout.stopShimmerAnimation()
                            chooseLocationAdapter.differ.submitList(it.geonames)
                        },
                        onError = {
                            binding!!.chooseLocationFragmentShimmerLayout.isVisible = false
                            binding!!.chooseLocationFragmentShimmerLayout.stopShimmerAnimation()
                        }
                    )
                )
            }

        }
    }

    private fun setupChooseLocationRecyclerView() {
        chooseLocationRecyclerView = binding!!.regionItemRv
        chooseLocationAdapter = ChooseLocationAdapter()
        chooseLocationRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        chooseLocationRecyclerView.adapter = chooseLocationAdapter
    }


}