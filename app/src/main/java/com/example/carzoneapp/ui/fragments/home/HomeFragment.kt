package com.example.carzoneapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.CategoryAdapter
import com.example.carzoneapp.adapters.ParentAdsAdapter
import com.example.carzoneapp.databinding.FragmentHomeBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.Ad
import com.example.domain.entity.HomeAdsAdapterItem
import com.example.domain.entity.SavedItem
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView

    private lateinit var parentAdsAdapter: ParentAdsAdapter
    private lateinit var parentAdsRecyclerView: RecyclerView
    private val homeViewModel: MainViewModel by viewModels()
    private var savedItems: List<SavedItem>? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


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
        subscribeToObservables()
        homeViewModel.getUserInfoByUserId(firebaseAuth.currentUser?.uid.toString())
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.getSavedItemsByUserId(firebaseAuth.currentUser?.uid.toString(), true)
        homeViewModel.getAllVehiclesCategories()
    }

    private fun setupCategoryRecyclerView() {
        categoryRecyclerView = binding!!.categoryRv
        categoryAdapter = CategoryAdapter(1)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = categoryAdapter
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
                homeViewModel.getSavedItemsState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.adsShimmerLayout.isVisible = true
                            binding!!.adsShimmerLayout.startShimmerAnimation()
                        }, onSuccess = { savedItemsList ->
                            savedItems = savedItemsList
                            homeViewModel.getAllAds(false)
                        },
                        onError = {
                            binding!!.adsShimmerLayout.stopShimmerAnimation()
                            binding!!.adsShimmerLayout.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                "cannot find user data",
                                Toast.LENGTH_SHORT
                            ).show()
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
                            val updatedAdsList = adsList.map { ad ->
                                val isInSavedItems = savedItems?.any { savedItem ->
                                    savedItem.itemId == ad.adId
                                }
                                ad.copy(isInSavedItems = isInSavedItems)
                            }
                            displayData(updatedAdsList)
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
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userInfoState.collect(
                    EventObserver(
                        onLoading = {

                        }, onSuccess = { user ->
                            binding!!.locationTv.text = user.location
                        },
                        onError = {
                            Toast.makeText(
                                requireContext(),
                                "cannot find user data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.addToSavedItemsState.collect(
                    EventObserver(
                        onLoading = {

                        }, onSuccess = {
                            homeViewModel.getSavedItemsByUserId(
                                firebaseAuth.currentUser?.uid.toString(),
                                false
                            )


                        },
                        onError = {

                            Toast.makeText(
                                requireContext(),
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.removeSavedItemsState.collect(
                    EventObserver(
                        onLoading = {

                        }, onSuccess = {
                            homeViewModel.getSavedItemsByUserId(
                                firebaseAuth.currentUser?.uid.toString(),
                                false
                            )
                        },
                        onError = {

                            Toast.makeText(
                                requireContext(),
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                )
            }
        }


    }

    private fun displayData(adsList: List<Ad>) {
        val vanList: MutableList<Ad> = mutableListOf()
        val carList: MutableList<Ad> = mutableListOf()
        val motoList: MutableList<Ad> = mutableListOf()
        val truckList: MutableList<Ad> = mutableListOf()
        for (ad in adsList) {
            when (ad.vehicle.vehicleType) {
                "Vans" -> {
                    vanList.add(ad)
                }

                "Cars" -> {
                    carList.add(ad)
                }

                "Motorcycles" -> {
                    motoList.add(ad)
                }

                "Trucks" -> {
                    truckList.add(ad)
                }

            }
        }
        val listOfAds = listOf(
            carList.takeIf { it.isNotEmpty() }?.let { HomeAdsAdapterItem("Cars", it) },
            vanList.takeIf { it.isNotEmpty() }?.let { HomeAdsAdapterItem("Vans", it) },
            truckList.takeIf { it.isNotEmpty() }?.let { HomeAdsAdapterItem("Trucks", it) },
            motoList.takeIf { it.isNotEmpty() }?.let { HomeAdsAdapterItem("Motorcycle", it) }
        ).mapNotNull { it }
        binding?.apply {
            parentAdsRecyclerView = binding!!.adsRv
            parentAdsAdapter = ParentAdsAdapter(listOfAds)
            parentAdsRecyclerView.adapter = parentAdsAdapter
            parentAdsAdapter.setOnItemClickListener { _, childItem ->
                val action = HomeFragmentDirections.actionHomeFragmentToAdDetailsFragment(childItem)
                findNavController().navigate(action)
            }
            parentAdsAdapter.setOnSaveIconClickListener { _, ad ->
                if (ad.isInSavedItems == true) {
                    homeViewModel.removeFromSavedItemsByUserId(
                        firebaseAuth.currentUser?.uid.toString(),
                        ad.adId
                    )
                } else {
                    homeViewModel.addToSavedItems(
                        SavedItem(
                            firebaseAuth.currentUser?.uid.toString(),
                            ad.adId,
                            ad
                        )

                    )
                }


            }
        }


    }


}





