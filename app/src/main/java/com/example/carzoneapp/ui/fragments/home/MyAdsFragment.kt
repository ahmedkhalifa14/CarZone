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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.AdsAdapter
import com.example.carzoneapp.databinding.FragmentMyAdsBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyAdsFragment : Fragment() {
    private var _binding: FragmentMyAdsBinding? = null
    private val binding get() = _binding
    private lateinit var adsAdapter: AdsAdapter
    private lateinit var adsRecyclerView: RecyclerView
    private val homeViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyAdsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdsRecyclerView()
        subscribeToObservables()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        val uid = currentUser?.uid
        if (uid != null) {
            homeViewModel.getUserAds(uid)
        }

    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userAdsState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.noItemAvailable.isVisible= false
                            binding!!.myAdsShimmerLayout.isVisible = true
                            binding!!.myAdsShimmerLayout.startShimmerAnimation()
                        },
                        onSuccess = { myAds ->
                            binding!!.noItemAvailable.isVisible= false

                            binding!!.myAdsShimmerLayout.isVisible = false
                            binding!!.myAdsShimmerLayout.stopShimmerAnimation()
                            adsAdapter.differ.submitList(myAds)
                        },
                        onError = {
                            binding!!.noItemAvailable.isVisible= true
                            binding!!.myAdsShimmerLayout.isVisible = false
                            binding!!.myAdsShimmerLayout.stopShimmerAnimation()
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }

    private fun setupAdsRecyclerView() {
        adsRecyclerView = binding!!.myAdsRv
        adsAdapter = AdsAdapter(2)
        adsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adsRecyclerView.adapter = adsAdapter
    }

}