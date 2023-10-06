package com.example.carzoneapp.ui.fragments.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.carzoneapp.databinding.FragmentSpecificationsTabBinding
import com.example.domain.entity.Car
import com.example.domain.entity.Motorcycle
import com.example.domain.entity.Truck
import com.example.domain.entity.Van
import com.example.domain.entity.Vehicle
import com.example.domain.entity.VehiclesCategories
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class SpecificationsTabFragment : Fragment() {
    private var _binding: FragmentSpecificationsTabBinding? = null
    private val binding get() = _binding
    private  var  category: VehiclesCategories?= null
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    //to create a new instance of SpecificationsTabFragment with arguments
    companion object {
        fun newInstance(category: VehiclesCategories): SpecificationsTabFragment {
            val fragment = SpecificationsTabFragment()
            val args = Bundle()
            args.putSerializable("category", category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSpecificationsTabBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         category = arguments?.getSerializable("category") as VehiclesCategories?

        if (category != null) {
            when (category!!.categoryName) {
                "Cars" -> {
                    binding!!.inputTextLayoutTransmissionCar.isVisible = true
                }

                "Vans" -> {
                    binding!!.inputTextLayoutCargoCapacityVan.isVisible = true
                }

                "Trucks" -> {
                    binding!!.inputTextLayoutEnginePowerMoto.isVisible = true
                    binding!!.inputTextLayoutWeightTruck.isVisible = true
                }

                "Motorcycles" -> {
                    binding!!.inputTextLayoutEngineTorqueMoto.isVisible = true
                    binding!!.inputTextLayoutKerbWeightMoto.isVisible = true
                    binding!!.inputTextLayoutEnginePowerMoto.isVisible = true
                }
            }

        }


    }

    fun getVehicleData(): Vehicle {
        val vehicleModel = binding!!.inputTextLayoutVehicleModel.editText!!.text.toString()
        val manufacturer = binding!!.inputTextLayoutManufacturer.editText!!.text.toString()
        val vehicleName = binding!!.inputTextLayoutVehicleName.editText!!.text.toString()
        val vehicleEngine = binding!!.inputTextLayoutVehicleEngine.editText!!.text.toString()
        val vehicleFuelType = binding!!.inputTextLayoutVehicleFuelType.editText!!.text.toString()
        val vehicleMileage = binding!!.inputTextLayoutMileageCar.editText!!.text.toString()
        val seatingCapacity =
            binding!!.inputTextLayoutSeatingCapacityCar.editText!!.text.toString()
        return Vehicle(
            vehicleModel,
            manufacturer,
            vehicleName,
            vehicleEngine,
            vehicleFuelType,
            vehicleMileage,
            seatingCapacity.toInt(),
            category!!.categoryName
        )

    }

    fun getCarData(): Car {
        val transmission = binding!!.inputTextLayoutTransmissionCar.editText!!.text.toString()
        return Car(transmission)
    }

    fun getVanData(): Van {
        val cargoCapacity = binding!!.inputTextLayoutCargoCapacityVan.editText!!.text.toString()
        return Van(cargoCapacity.toDouble())
    }

    fun getTruckData(): Truck {
        val weight = binding!!.inputTextLayoutWeightTruck.editText!!.text.toString()
        val enginePower = binding!!.inputTextLayoutEnginePowerMoto.editText!!.text.toString()
        return Truck(weight.toDouble(), enginePower.toInt())
    }

    fun getMotorcycleData(): Motorcycle {
        val enginePower = binding!!.inputTextLayoutEnginePowerMoto.editText!!.text.toString()
        val engineTorque = binding!!.inputTextLayoutEngineTorqueMoto.editText!!.text.toString()
        val kerbWight = binding!!.inputTextLayoutKerbWeightMoto.editText!!.text.toString()
        return Motorcycle(enginePower, engineTorque, kerbWight.toDouble())
    }
}
