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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.R
import com.example.carzoneapp.adapters.AdImageAdapter
import com.example.carzoneapp.adapters.AdsAdapter
import com.example.carzoneapp.databinding.FragmentAdDetailsBinding
import com.example.carzoneapp.helper.extractDateAndTime
import com.example.carzoneapp.helper.extractFormattedDate
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.Ad
import com.example.domain.entity.CarData
import com.example.domain.entity.MotorCycleData
import com.example.domain.entity.TruckData
import com.example.domain.entity.User
import com.example.domain.entity.VanData
import com.example.domain.entity.VehicleData
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AdDetailsFragment : Fragment() {
    private var _binding: FragmentAdDetailsBinding? = null

    private val binding get() = _binding
    private val args: AdDetailsFragmentArgs by navArgs()
    private var ad: Ad? = null
    private lateinit var adImageAdapter: AdImageAdapter
    private lateinit var adImagesRecyclerView: RecyclerView
    private val homeViewModel: MainViewModel by viewModels()
    private lateinit var adsAdapter: AdsAdapter
    private lateinit var adsRecyclerView: RecyclerView

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdDetailsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ad = args.ad
        displayAdData(ad!!)
        setupAdImagesRecyclerView(ad!!.vehicleImages!!)
        setupAdsRecyclerView()
        subscribeToObservables()
        homeViewModel.getUserInfoByUserId(ad!!.seller)
        homeViewModel.getAdsByVehicleType(ad!!.vehicle.vehicleType)
        binding!!.adDetailsSellerSeeProfile.setOnClickListener {

        }
    }


    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userInfoState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = { user ->
                            displayUserData(user)
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allAdsByVehicleTypeState.collect(
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

    private fun displayUserData(user: User) {
        binding!!.adDetailsSellerName.text = user.userName
        Glide.with(requireContext()).load(user.image).into(binding!!.adDetailsSellerImg)
        binding!!.location2Txt.text = user.location
        val joinedAt = getString(R.string.joined_at)+ extractFormattedDate(user.joinedAt)
         binding!!.adDetailsSellerJoinedSince.text= joinedAt
        binding.apply {
            binding!!.chatBtn.setOnClickListener {
                if (user.userId == firebaseAuth.currentUser?.uid) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.my_friend_you_are_stupid_you_will_send_a_message_to_yourself),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val action =
                        AdDetailsFragmentDirections.actionAdDetailsFragmentToChatFragment(user)
                    findNavController().navigate(action)
                }

            }
        }
    }


    private fun displayAdData(ad: Ad) {
        binding!!.adBrandTxt.text = ad.vehicle.manufacturer
        binding!!.adYearTxt.text = ad.vehicle.vehicleModel
        binding!!.adEngineTxt.text = ad.vehicle.vehicleEngine
        binding!!.adKilometersTxt.text = ad.vehicle.vehicleMileage
        binding!!.adFuelTypeTxt.text = ad.vehicle.vehicleFuelType
        binding!!.adSeatingCapacityTxt.text = ad.vehicle.seatingCapacity.toString()
        binding!!.adModelTxt.text = ad.vehicle.vehicleName
        binding!!.priceTv.text = ad.adsData.price
        binding!!.adTitleTv.text = ad.adsData.title
        binding!!.adLocationTv.text = ad.adsData.location


        val firebaseDate = ad.adsData.date
        val dateAndTime = firebaseDate?.let { extractDateAndTime(it) }
        val date =
            dateAndTime!!.day + " " + dateAndTime.month + " " + dateAndTime.year + " at " + dateAndTime.hour + ":" + dateAndTime.minute + " " + dateAndTime.amPm
        binding!!.adDateTv.text = date
        binding!!.adDescriptionText.text = ad.adsData.description
        binding!!.collapsingToolbar.title = ad.vehicle.vehicleName
        ad.vehicleType?.let { displayVehicleData(it) }


    }

    private fun displayVehicleData(vehicle: VehicleData) {
        when (vehicle) {
            is CarData -> {
                binding!!.adCarTransmissionTv.isVisible = true
                binding!!.adCarTransmissionTxt.text = vehicle.car.transmission
            }

            is VanData -> {
                binding!!.adVanCargoCapacityTv.isVisible = true
                binding!!.adVanCargoCapacityTxt.text = vehicle.van.cargoCapacity.toString()

            }

            is TruckData -> {

                binding!!.adTruckEnginePowerTv.isVisible = true
                binding!!.adTruckEnginePowerTxt.text = vehicle.truck.enginePower.toString()
                binding!!.adTruckWeightTv.isVisible = true
                binding!!.adTruckWeightTxt.text = vehicle.truck.weight.toString()

            }

            is MotorCycleData -> {
                binding!!.adMotorcycleEngineTorqueTv.isVisible = true
                binding!!.adMotorcycleEngineTorqueTxt.text = vehicle.motorcycle.engineTorque
                binding!!.adMotorcycleKerbWeightTv.isVisible = true
                binding!!.adMotorcycleKerbWeightTxt.text = vehicle.motorcycle.kerbWeight.toString()
                binding!!.adTruckEnginePowerTv.isVisible = true
                binding!!.adTruckEnginePowerTxt.text = vehicle.motorcycle.enginePower
            }
        }
    }

    private fun setupAdImagesRecyclerView(imageUrls: List<String>) {
        adImagesRecyclerView = binding!!.adImagesRv
        adImageAdapter = AdImageAdapter(imageUrls)
        adImagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adImagesRecyclerView.adapter = adImageAdapter
    }

    private fun setupAdsRecyclerView() {
        adsRecyclerView = binding!!.relatedAdsRv
        adsAdapter = AdsAdapter(1)
        adsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adsRecyclerView.adapter = adsAdapter
    }

}