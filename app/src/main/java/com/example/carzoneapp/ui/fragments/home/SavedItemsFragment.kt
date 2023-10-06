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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.SavedItemAdapter
import com.example.carzoneapp.databinding.FragmentSavedItemsBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SavedItemsFragment : Fragment() {
    private var _binding: FragmentSavedItemsBinding? = null

    private val binding get() = _binding
    private lateinit var savedItemsAdapter: SavedItemAdapter
    private lateinit var savedItemsRecyclerView: RecyclerView
    private val homeViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSavedItemsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSavedItemsRecyclerView()
        subscribeToObservables()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        val uid = currentUser?.uid
        if (uid != null) {
            homeViewModel.getSavedItemsByUserId(uid,true)
        }
        savedItemsAdapter.setOnItemClickListener { savedItem ->
            val action =
                SavedItemsFragmentDirections.actionSavedItemsFragmentToAdDetailsFragment(savedItem.ad)
            findNavController().navigate(action)
        }
        savedItemsAdapter.setOnSaveItemClickListener { savedItem ->
            homeViewModel.removeFromSavedItemsByUserId(
                firebaseAuth.currentUser?.uid.toString(),
                savedItem.itemId
            )
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getSavedItemsByUserId(firebaseAuth.currentUser?.uid.toString(),true)
    }
    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getSavedItemsState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.noItemAvailableCard.isVisible = false
                            binding!!.saveItemsShimmerLayout.isVisible = true
                            binding!!.saveItemsShimmerLayout.startShimmerAnimation()
                        },
                        onSuccess = { savedItems ->
                            binding!!.saveItemsShimmerLayout.isVisible = false
                            binding!!.saveItemsShimmerLayout.stopShimmerAnimation()
                            if (savedItems.isEmpty()) {
                                binding!!.noItemAvailableCard.isVisible = true

                            } else {
                                savedItemsAdapter.differ.submitList(savedItems)
                            }
                        },
                        onError = {
                            binding!!.saveItemsShimmerLayout.isVisible = false
                            binding!!.saveItemsShimmerLayout.stopShimmerAnimation()
                            binding!!.noItemsTv.text = it
                            binding!!.noItemAvailableCard.isVisible = true
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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

                        },
                        onSuccess = {
                            homeViewModel.getSavedItemsByUserId(firebaseAuth.currentUser?.uid.toString(),false)
                            Toast.makeText(
                                requireContext(),
                                "Removed Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = {

                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }

    private fun setupSavedItemsRecyclerView() {
        savedItemsRecyclerView = binding!!.savedItemsRv
        savedItemsAdapter = SavedItemAdapter()
        savedItemsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        savedItemsRecyclerView.adapter = savedItemsAdapter
    }


}