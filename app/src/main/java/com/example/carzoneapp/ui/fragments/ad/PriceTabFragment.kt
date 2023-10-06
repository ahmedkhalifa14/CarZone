package com.example.carzoneapp.ui.fragments.ad

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentPriceTabBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.ui.viewmodel.SharedLocationViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.AdData
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PriceTabFragment : Fragment() {
    private var _binding: FragmentPriceTabBinding? = null
    private val priceTabFragmentBinding get() = _binding
    private lateinit var selectedLocation: String
    private lateinit var userLocation: String
    private val homeViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPriceTabBinding.inflate(inflater, container, false)
        return priceTabFragmentBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedLocation= requireContext().getString(R.string.location2)
        userLocation= requireContext().getString(R.string.location2)
        priceTabFragmentBinding?.locationCard?.setOnClickListener {
            val chooseLocationDialogFragment = ChooseLocationFragment()
            chooseLocationDialogFragment.show(childFragmentManager, "ChooseLocationDialogFragment")
        }
        observeSelectedLocation()
        subscribeToObservables()
        homeViewModel.getUserInfoByUserId(firebaseAuth.currentUser?.uid.toString())
        updateLocationUI()

    }

    private fun observeSelectedLocation() {
        val sharedLocationViewModel =
            ViewModelProvider(requireActivity())[SharedLocationViewModel::class.java]
        sharedLocationViewModel.getSelectedLocationLiveData().observe(
            viewLifecycleOwner
        ) { location ->
            selectedLocation = if (location != "current location") {
                location
            } else {
                userLocation
            }
            updateLocationUI()
        }
    }


    fun getPriceTabData(): AdData {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val price = priceTabFragmentBinding!!.inputTextLayoutPrice.editText!!.text.toString()
        val title = priceTabFragmentBinding!!.inputTextLayoutTitle.editText!!.text.toString()
        val negotiable = priceTabFragmentBinding!!.checkboxNegotiable.isChecked
        val description =
            priceTabFragmentBinding!!.inputTextLayoutDescribe.editText!!.text.toString()
        return AdData(title, description, negotiable, price, selectedLocation, date)
    }

    private fun updateLocationUI() {
        priceTabFragmentBinding?.locationTv?.text = selectedLocation
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userInfoState.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = {
                            userLocation = it.location
                            selectedLocation = it.location
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
