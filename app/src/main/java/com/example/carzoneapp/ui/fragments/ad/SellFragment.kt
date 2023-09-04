package com.example.carzoneapp.ui.fragments.ad

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.CategoryAdapter
import com.example.carzoneapp.databinding.FragmentSellBinding
import com.example.carzoneapp.ui.viewmodel.HomeViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SellFragment : Fragment() {
    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryRecyclerView()
        subscribeToObservables()
        categoryAdapter.setOnItemClickListener {
            val action = SellFragmentDirections.actionSellFragmentToSellDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getAllVehiclesCategories()
    }

    private fun setupCategoryRecyclerView() {
        categoryRecyclerView = binding!!.sellFragmentRv
        categoryAdapter = CategoryAdapter(2)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.vehiclesCategoriesState.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = { vehiclesCategoriesList ->
                            categoryAdapter.differ.submitList(vehiclesCategoriesList)
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