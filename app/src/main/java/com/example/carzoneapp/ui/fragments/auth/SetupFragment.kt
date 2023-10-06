package com.example.carzoneapp.ui.fragments.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentSetupBinding
import com.example.carzoneapp.helper.convertLatLongToLocation
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.Constants
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.LaunchInfo
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
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latLng: LatLng
    private lateinit var geocoder: Geocoder
    private val authViewModel: AuthViewModel by viewModels()
    private var user: User? = null
    private val args: SetupFragmentArgs by navArgs()
    private var uriArray: ArrayList<Uri>? = null
    private val homeViewModel: MainViewModel by viewModels()
    private var isNotLogin = false
    private var isLoginWithGoogle = false

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val navOptions =
        NavOptions.Builder()
            .setPopUpTo(R.id.setupFragment, true)
            .build()
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                uriArray = ArrayList()
                binding!!.profileImg.setImageURI(uri)
                uriArray?.add(uri)
            } else {
                Toast.makeText(requireContext(), "Unknown error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.inputTextEmail.doAfterTextChanged {
            binding!!.inputTextLayoutEmail.isHelperTextEnabled = false
        }
        binding!!.inputTextPhone.doAfterTextChanged {
            binding!!.inputTextLayoutPhone.isHelperTextEnabled = false
        }
        binding!!.inputTextUserName.doAfterTextChanged {
            binding!!.inputTextLayoutUserName.isHelperTextEnabled = false
        }
        user = args.userData
        displayUserData(user!!)

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        checkLocationSettings()
        subscribeToObservables()
        binding!!.locationCard.setOnClickListener {
            checkLocationSettings()
        }

        binding!!.profileImg.setOnClickListener {
            openGallery()
        }
        binding!!.LetsGoBtn.setOnClickListener {

            if (isLoginWithGoogle) {
                saveUserData(user!!.image)
            }
            uriArray?.let { image -> homeViewModel.uploadImages(image) }
        }
    }

    private fun openGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun displayUserData(user: User) {
        if (user.userName.isNotEmpty()) {
            binding!!.inputTextLayoutUserName.editText?.text =
                Editable.Factory.getInstance().newEditable(user.userName)
            binding!!.inputTextLayoutUserName.editText?.isEnabled = false
        }

        if (user.phoneNumber.isNotEmpty()) {
            binding!!.inputTextLayoutPhone.editText?.text =
                Editable.Factory.getInstance().newEditable(user.phoneNumber)
            binding!!.inputTextLayoutPhone.editText?.isEnabled = false
        }
        if (user.email.isNotEmpty()&&user.phoneNumber.isNotEmpty()){
            isNotLogin=true
        }
        if (user.email.isNotEmpty()) {
            binding!!.inputTextLayoutEmail.editText?.text =
                Editable.Factory.getInstance().newEditable(user.email)
            binding!!.inputTextLayoutEmail.editText?.isEnabled = false
        }
        if (user.image.isNotEmpty()) {
            Glide.with(requireContext()).load(user.image).into(binding!!.profileImg)
            binding!!.profileImg.isClickable = false
            isLoginWithGoogle = true
        }

    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uploadImagesState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true
                        },
                        onSuccess = { img ->
                            binding!!.spinKitProgress.isVisible = false
                            saveUserData(img[0])
                        },
                        onError = {
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.saveUserDataState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true

                        },
                        onSuccess = {
                            binding!!.spinKitProgress.isVisible = false
                            if (isNotLogin) {
                                findNavController().navigate(R.id.action_setupFragment_to_loginFragment)
                            } else {
                                homeViewModel.saveFirstTimeLaunch(
                                    LaunchInfo(
                                        isFirstTimeLaunch = true,
                                        isLogin = true
                                    )
                                )
                            }
                        },
                        onError = {
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    )
                )
            }

        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.saveFirstTimeLaunchState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true
                        },
                        onSuccess = {
                            binding!!.spinKitProgress.isVisible = false
                            findNavController().navigate(R.id.homeFragment, null, navOptions)
                          //  findNavController().navigate(R.id.action_setupFragment_to_homeFragment)
                        },
                        onError = {
                            binding!!.spinKitProgress.isVisible = false

                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    )
                )
            }

        }

    }

    private fun saveUserData(image: String) {
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
            latLng.latitude.toString(),
            latLng.longitude.toString(),
            phone,
            email,
            image,
            System.currentTimeMillis()
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
                showLocation()
            } else {
                // Location is null
            }
        }.addOnFailureListener { exception ->
            // Location request failed
            Timber.tag("fusedLocationClientException").d(exception)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showLocation() {
        try {
            val location = convertLatLongToLocation(latLng.latitude, latLng.longitude, geocoder)
            binding?.locationTv?.text = location
        } catch (e: IOException) {
            Timber.tag(TAG).e("Geocoder IOException: %s", e.message)
            binding?.locationTv?.text = "Location data not available"
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
                            requireActivity(), Constants.REQUEST_CHECK_SETTINGS
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