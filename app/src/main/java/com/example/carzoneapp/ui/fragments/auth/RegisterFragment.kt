package com.example.carzoneapp.ui.fragments.auth


import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentRegisterBinding
import com.example.carzoneapp.helper.convertLatLongToLocation
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.Constants.REQUEST_CHECK_SETTINGS
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.User
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latLng: LatLng
    private lateinit var geocoder: Geocoder
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        binding!!.signUpBtn.setOnClickListener {
            checkLocationSettings()
        }
        subscribeToObservables()
        binding!!.loginTxt.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.registerState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true
                        }, onSuccess = { user ->
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                "Verification email sent to ${user.email}",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }, onError = { error ->
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                        })
                )
            }
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.saveUserDataState.collect(
                    EventObserver(
                        onLoading = {

                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = {

                            Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()

                        }
                    )
                )
            }

        }
    }

    private fun register() {
        authViewModel.registerUser(
            binding!!.inputTextLayoutUserName,
            binding!!.inputTextLayoutPhone,
            binding!!.inputTextLayoutEmail,
            binding!!.inputTextLayoutPassword,
        )
        saveUserData()
    }

    private fun saveUserData() {

        val userId = firebaseAuth.currentUser?.uid
        val name = binding!!.inputTextLayoutUserName.editText?.text.toString()
        val email = binding!!.inputTextLayoutEmail.editText?.text.toString()
        val phone = binding!!.inputTextLayoutPhone.editText?.text.toString()
        val location = convertLatLongToLocation(
            latLng.latitude,
            latLng.longitude,
            geocoder
        )
        val user = User(
            userId!!,
            name,
            location,
            latLng.longitude.toString(),
            latLng.longitude.toString(),
            phone,
            email,
            ""
        )
        authViewModel.saveUserData(user)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val locationRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }.toList()
            val deniedPermissions = permissions.entries.filter { !it.value }.map { it.key }.toList()
            if (grantedPermissions.size == locationPermission.size) {
                // All permissions are granted, proceed with the desired operation
                checkLocationSettings()
            } else {
                // Some permissions are denied, handle accordingly (e.g., show a message, disable functionality)
                if (EasyPermissions.somePermissionPermanentlyDenied(this, deniedPermissions)) {
                    // Some permissions are permanently denied, show a dialog to open app settings
                    SettingsDialog.Builder(requireContext()).build().show()
                } else {
                    // Request the denied permissions again
                    requestLocationPermission()
                }
            }
        }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestLocationPermission() {
        locationRequestLauncher.launch(locationPermission)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latLng = LatLng(location.latitude, location.longitude)
                Toast.makeText(requireContext(), location.longitude.toString(), Toast.LENGTH_SHORT)
                    .show()
                register()
            } else {
                // Location is null
            }
        }.addOnFailureListener { exception ->
            // Location request failed
            Timber.tag("fusedLocationClientException").d(exception)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkLocationSettings() {
        if (hasLocationPermission()) {
            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            val builder =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest.build())
            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                // Location settings are satisfied, get location
                getLocation()
            }
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, show dialog to enable location
                    try {
                        exception.startResolutionForResult(
                            requireActivity(), REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        //Ignore the error
                    }
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Permissions granted, proceed with the desired operation
        checkLocationSettings()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Permissions denied, handle accordingly (e.g., show a message, disable functionality)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // Some permissions are permanently denied, show a dialog to open app settings
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            // Request the denied permissions again
            requestLocationPermission()
        }
    }
}




