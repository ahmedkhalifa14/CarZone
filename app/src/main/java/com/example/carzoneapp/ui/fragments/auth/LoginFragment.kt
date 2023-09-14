package com.example.carzoneapp.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentLoginBinding
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.inputTextEmail.doAfterTextChanged {
            binding!!.inputTextLayoutEmail.isHelperTextEnabled = false
        }
        binding!!.inputTextPassword.doAfterTextChanged {
            binding!!.inputTextLayoutPassword.isHelperTextEnabled = false
        }
        binding!!.loginBtn.setOnClickListener {
            Toast.makeText(requireContext(), "login", Toast.LENGTH_SHORT).show()
            loginWithEmail()
        }
        subscribeToObservables()
    }

    private fun loginWithEmail() {
        authViewModel.loginWithEmail(
            binding!!.inputTextLayoutEmail,
            binding!!.inputTextLayoutPassword
        )
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginWithEmailState.collect(
                    EventObserver(
                        onLoading = {
                            binding!!.spinKitProgress.isVisible = true
                        },
                        onSuccess = {
                            binding!!.spinKitProgress.isVisible = false
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        },
                        onError = {
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }

}