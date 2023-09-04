package com.example.carzoneapp.ui.fragments.auth

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
import androidx.navigation.fragment.navArgs
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentVerifyCodeBinding
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel>()
    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: VerifyCodeFragmentArgs by navArgs()
        val verificationID = args.verificationId
        subscribeToObservables()
        binding!!.verifyBtn.setOnClickListener {
            val verifyCode = binding!!.verifyCodeView.text.toString()
            if (verifyCode.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Please enter the full verification code",
                    Toast.LENGTH_LONG
                ).show()
            }
            authViewModel.verifyCode(verificationID, verifyCode)
        }
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.verifyCodeState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_verifyCodeFragment_to_homeFragment)
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    )
                )
            }
        }
    }


}