package com.example.carzoneapp.ui.fragments.auth

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
import com.example.carzoneapp.databinding.FragmentPhoneAuthBinding
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class PhoneAuthFragment : Fragment() {
    private val authViewModel by viewModels<AuthViewModel>()
    private var _binding: FragmentPhoneAuthBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPhoneAuthBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        binding!!.continueBtn.setOnClickListener {
            val phoneNumber = binding!!.inputTextPhone.text.toString()
            if (phoneNumber.isNotEmpty()) {
                authViewModel.sendVerificationCode(phoneNumber)
            } else {
                Toast.makeText(
                    requireContext(),
                    "please enter your phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }





    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.sendVerificationCodeState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true
                        },
                        onSuccess = {verificationId->
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), verificationId, Toast.LENGTH_SHORT).show()
                            val action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToVerifyCodeFragment(verificationId,binding!!.inputTextPhone.text.toString())
                            findNavController().navigate(action)
                            Timber.tag("verification").d(verificationId)
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

}