package com.example.carzoneapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.AdImageAdapter
import com.example.carzoneapp.databinding.FragmentAdDetailsBinding
import com.example.carzoneapp.helper.extractDateAndTime
import com.example.domain.entity.Ad
import com.example.domain.entity.CarData
import com.example.domain.entity.MotorCycleData
import com.example.domain.entity.TruckData
import com.example.domain.entity.VanData
import com.example.domain.entity.VehicleData


class AdDetailsFragment : Fragment() {
    private var _binding: FragmentAdDetailsBinding? = null

    private val binding get() = _binding
    private val args: AdDetailsFragmentArgs by navArgs()
    private var ad: Ad? = null
    private lateinit var adImageAdapter: AdImageAdapter
    private lateinit var adImagesRecyclerView: RecyclerView

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
        setupAdImagesRecyclerView(ad!!.vehicleImages)
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
        val firebaseDate = ad.adsData.date
        val dateAndTime = extractDateAndTime(firebaseDate)
        val date = dateAndTime.day + " " + dateAndTime.month +" "+dateAndTime.year+ " at " + dateAndTime.hour + ":" + dateAndTime.minute+" "+dateAndTime.amPm
        binding!!.adDateTv.text =date
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


}