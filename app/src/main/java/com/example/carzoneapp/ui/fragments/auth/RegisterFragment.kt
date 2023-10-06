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
import com.example.carzoneapp.databinding.FragmentRegisterBinding
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val authViewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        binding!!.inputTextPhone.doAfterTextChanged {
            binding!!.inputTextLayoutPhone.isHelperTextEnabled=false
        }
        binding!!.inputTextUserName.doAfterTextChanged {
            binding!!.inputTextLayoutUserName.isHelperTextEnabled=false
        }
        binding!!.signUpBtn.setOnClickListener {
            register()
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
                        }, onSuccess = {
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                "Verification email sent to your mail",
                                Toast.LENGTH_LONG
                            ).show()
                            sendDataAndNavigate()
                        }, onError = { error ->
                            binding!!.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                        })
                )
            }


        }
    }
    private fun sendDataAndNavigate() {

        val user = firebaseAuth.currentUser?.let {
            User(
                it.uid,
                binding!!.inputTextLayoutUserName.editText?.text.toString(),
                "",
                "",
                "",
                binding!!.inputTextLayoutPhone.editText?.text.toString(),
                binding!!.inputTextLayoutEmail.editText?.text.toString(),
                "",
            )
        }
        val action = user?.let {
            RegisterFragmentDirections.actionRegisterFragmentToSetupFragment(
                it
            )
        }
        if (action != null) {
            //findNavController().popBackStack()
            findNavController().navigate(action)

        }
    }
    private fun register() {
        authViewModel.registerUser(
            binding!!.inputTextLayoutUserName,
            binding!!.inputTextLayoutPhone,
            binding!!.inputTextLayoutEmail,
            binding!!.inputTextLayoutPassword,
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




