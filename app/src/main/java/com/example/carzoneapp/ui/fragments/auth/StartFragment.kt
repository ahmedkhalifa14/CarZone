package com.example.carzoneapp.ui.fragments.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

                        },
                        onSuccess = {
                            when (it) {
                                is AuthState.Success -> {
                                    val user = it.user
                                    saveUserData(user)
                                    findNavController().navigate(R.id.action_startFragment_to_homeFragment)
                                }

                                else -> {}
                            }
                        },
                        onError = {

                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.saveUserDataState.collect(
                    EventObserver(
                        onLoading = {

                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = {

                            Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()

                        }
                    )
                )
            }

        }
    }

    private fun saveUserData(user: GoogleAccountInfo) {
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
        authViewModel.saveUserData(userInfo)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
