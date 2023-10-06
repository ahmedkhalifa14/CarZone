package com.example.carzoneapp.ui.fragments.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentStartBinding
import com.example.carzoneapp.ui.viewmodel.AuthViewModel
import com.example.carzoneapp.utils.AuthState
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.GoogleAccountInfo
import com.example.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                authViewModel.handleGoogleSignInResult(data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        binding.continueWithEmailBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_registerFragment)
        }
        binding.continueWithGoogleBtn.setOnClickListener {
            authViewModel.signInWithGoogle()
            googleSignInLauncher.launch(authViewModel.getGoogleSignInIntent())
        }
        binding.continueWithPhoneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_phoneAuthFragment)
        }
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginWithGoogleState.collect(
                    EventObserver(
                        onLoading = {
                            binding.spinKitProgress.isVisible = true

                        },
                        onSuccess = {
                            when (it) {
                                is AuthState.Success -> {
                                    val user = it.user
                                    convertObjectAndNavigate(user)
                                }

                                else -> {

                                }
                            }
                        },
                        onError = {
                            binding.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }

    private fun convertObjectAndNavigate(user: GoogleAccountInfo) {
        val userId = firebaseAuth.currentUser?.uid
        val userInfo = User(
            userId!!,
            user.displayName!!,
            "",
            "",
            "",
            "",
            user.email!!,
            user.photoUrl!!
        )
        binding.spinKitProgress.isVisible = false
        val action = StartFragmentDirections.actionStartFragmentToSetupFragment(userInfo)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
