package com.example.carzoneapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.R
import com.example.carzoneapp.adapters.AccountItemAdapter
import com.example.carzoneapp.databinding.FragmentAccountBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.Constants
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding
    private lateinit var accountItemAdapter: AccountItemAdapter
    private lateinit var accountItemRV: RecyclerView
    private val homeViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccountItemRecyclerView()
        setAccountItemData()
        subscribeToObservables()
        homeViewModel.getUserInfoByUserId(firebaseAuth.currentUser?.uid.toString())
        binding!!.savedCard.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_savedItemsFragment)
        }
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userInfoState.collect(
                    EventObserver(
                        onLoading = {},
                        onSuccess = { user -> displayUserData(user) },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }

    private fun displayUserData(user: User) {
        binding?.apply {
            userName.text = user.userName
            Glide.with(requireContext()).load(user.image).into(profileImg)
        }
    }

    private fun setAccountItemData() {
        accountItemAdapter.differ.submitList(Constants.getAccountItems(requireContext()))
    }

    private fun setupAccountItemRecyclerView() {
        accountItemRV = binding!!.accountItemRv
        accountItemAdapter = AccountItemAdapter()
        accountItemRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        accountItemRV.adapter = accountItemAdapter
    }

}