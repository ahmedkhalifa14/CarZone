package com.example.carzoneapp.ui.fragments.ad

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.carzoneapp.R
import com.example.carzoneapp.adapters.TabAdapter
import com.example.carzoneapp.databinding.FragmentSellDetailsBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.Ad
import com.example.domain.entity.CarData
import com.example.domain.entity.MotorCycleData
import com.example.domain.entity.TruckData
import com.example.domain.entity.VanData
import com.example.domain.entity.VehicleData
import com.example.domain.entity.VehiclesCategories
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SellDetailsFragment : Fragment() {
    private lateinit var tab1Fragment: PriceTabFragment
    private lateinit var tab2Fragment: SpecificationsTabFragment
    private var _binding: FragmentSellDetailsBinding? = null
    private val binding get() = _binding
    private val args: SellDetailsFragmentArgs by navArgs()
    private var category: VehiclesCategories? = null
    private val homeViewModel: MainViewModel by viewModels()
    private var uriArray: ArrayList<Uri>? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(7)) { uris ->
        uriArray = ArrayList()
        if (!uris.isNullOrEmpty()) {
            val imageUri: Uri?
            if (uris.size > 1) {
                // Multiple images selected
                for (uri in uris) {
                    uriArray?.add(uri)
                }
                imageUri = uris[0]
            } else {
                // Single image selected
                imageUri = uris[0]
                uriArray?.add(imageUri)
            }
            binding!!.addImg.setImageURI(imageUri)

        } else {
            Toast.makeText(requireContext(), "unknown error occurred", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSellDetailsBinding.inflate(inflater, container, false)
        val adapter = TabAdapter(requireActivity())
        category = args.category

        tab1Fragment = PriceTabFragment()
        tab2Fragment = SpecificationsTabFragment.newInstance(category!!)
        adapter.addFragment(tab1Fragment, "Price")
        adapter.addFragment(tab2Fragment, "Specifications")
        binding!!.viewPager.adapter = adapter
        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val args: SellDetailsFragmentArgs by navArgs()
        binding!!.categoryName.text = category!!.categoryName
        Glide.with(requireContext()).load(category!!.categoryImage).into(binding!!.categoryImg)
        subscribeToObservables()
        binding!!.addImg.setImageURI(uriArray?.get(0))
        binding!!.addImg.setOnClickListener {
            openGallery()
        }
        binding!!.nextBtn.setOnClickListener {
            uriArray?.toList()?.let { imagesList -> homeViewModel.uploadImages(imagesList) }
        }
    }


    private fun addAds(imageList: List<String>) {

        val adsData = tab1Fragment.getPriceTabData()
        val vehicle = tab2Fragment.getVehicleData()

        val vehicleType: VehicleData = when (category?.categoryName) {
            "Cars" -> CarData(tab2Fragment.getCarData())
            "Vans" -> VanData(tab2Fragment.getVanData())
            "Trucks" -> TruckData(tab2Fragment.getTruckData())
            "Motorcycles" -> MotorCycleData(tab2Fragment.getMotorcycleData())
            else -> throw IllegalArgumentException("Unknown category: ${category?.categoryName}")
        }
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        val uid = currentUser?.uid

        val ad = Ad(
            firebaseAuth.currentUser?.uid.toString() + System.currentTimeMillis(),
            adsData,
            vehicle,
            imageList,
            uid.toString(),
            vehicleType
        )

        homeViewModel.addVehicleAd(ad)
    }


    private fun openGallery() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uploadImagesState.collect(
                    EventObserver(
                        onLoading = {

                        },
                        onSuccess = {
                            addAds(it)

                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                )
            }

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.addVehicleAdsState.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = {
                            findNavController().navigate(R.id.action_sellDetailsFragment_to_homeFragment)
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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