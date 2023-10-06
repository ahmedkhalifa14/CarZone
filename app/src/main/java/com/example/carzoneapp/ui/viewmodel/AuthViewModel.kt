package com.example.carzoneapp.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carzoneapp.R
import com.example.carzoneapp.helper.AuthValidation
import com.example.carzoneapp.utils.AuthState
import com.example.carzoneapp.utils.Event
import com.example.carzoneapp.utils.Resource
import com.example.domain.entity.GoogleAccountInfo
import com.example.domain.entity.User
import com.example.domain.usecase.LoginWithEmailUseCase
import com.example.domain.usecase.RegisterUseCase
import com.example.domain.usecase.SaveUserDataUseCase
import com.example.domain.usecase.SendVerificationCodeUseCase
import com.example.domain.usecase.SignInWithGoogleUseCase
import com.example.domain.usecase.VerifyCodeUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    @ApplicationContext  val context: Context,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _registerState = MutableStateFlow<Event<Resource<User>>>(Event(Resource.Init()))
    val registerState: StateFlow<Event<Resource<User>>> = _registerState

    private val _saveUserDataState = MutableStateFlow<Event<Resource<User>>>(Event(Resource.Init()))
    val saveUserDataState: StateFlow<Event<Resource<User>>> = _saveUserDataState

    private val _loginWithEmailState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val loginWithEmailState: StateFlow<Event<Resource<String>>> = _loginWithEmailState


    private val _loginWithGoogleState =
        MutableStateFlow<Event<Resource<AuthState>>>(Event(Resource.Init()))
    val loginWithGoogleState: StateFlow<Event<Resource<AuthState>>> = _loginWithGoogleState

    private val _sendVerificationCodeState = MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val sendVerificationCodeState:StateFlow<Event<Resource<String>>> =
        _sendVerificationCodeState


    private val _verifyCodeState = MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val verifyCodeState:StateFlow<Event<Resource<String>>> =
        _verifyCodeState


    fun sendVerificationCode(phoneNumber:String){
        viewModelScope.launch (Dispatchers.Main){
            _sendVerificationCodeState.emit(Event(Resource.Loading()))
            try {
                val verificationId= sendVerificationCodeUseCase(phoneNumber)
                _sendVerificationCodeState.emit(Event(Resource.Success(verificationId)))

            }catch (e:Exception){
                _sendVerificationCodeState.emit(Event(Resource.Error("Phone Number sign-in failed: ${e.message}")))

            }
        }
    }


    fun verifyCode(verificationId:String,code:String){
        viewModelScope.launch (Dispatchers.Main){
            _verifyCodeState.emit(Event(Resource.Loading()))
            try {
                verifyCodeUseCase(verificationId,code)
                _verifyCodeState.emit(Event(Resource.Success(" Sign in success")))
            }catch (e:Exception){
                _verifyCodeState.emit(Event(Resource.Error("Phone Number sign-in failed: ${e.message}")))

            }
        }
    }



    fun signInWithGoogle() {
        viewModelScope.launch(Dispatchers.Main) {
            _loginWithGoogleState.emit(Event(Resource.Loading()))
            try {
                val signInIntent = getGoogleSignInIntent()
                _loginWithGoogleState.emit(Event(Resource.Success(AuthState.GoogleSignIn(signInIntent))))
            } catch (e: Exception) {
                _loginWithGoogleState.emit(Event(Resource.Error("Google sign-in failed: ${e.message}")))
            }
        }
    }

    fun getGoogleSignInIntent(): Intent {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        return googleSignInClient.signInIntent
    }


    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch(Dispatchers.Main) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                _loginWithGoogleState.emit(Event(Resource.Error("Google sign-in failed: ${e.statusCode}")))
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String?) {
       viewModelScope.launch(Dispatchers.Main) {
            try {
                signInWithGoogleUseCase(idToken!!)
                val firebaseUser = firebaseAuth.currentUser
                val user = GoogleAccountInfo(
                    firebaseUser!!.uid,
                    firebaseUser.displayName,
                    firebaseUser.email,
                    firebaseUser.photoUrl.toString()
                )
                _loginWithGoogleState.emit(Event(Resource.Success(AuthState.Success(user))))
            } catch (e: Exception) {
                _loginWithGoogleState.emit(Event(Resource.Error("Error occurred: ${e.message}")))
            }
        }
    }


    fun registerUser(inputTextLayoutUserName: TextInputLayout, inputTextLayoutPhoneNumber: TextInputLayout, inputTextLayoutEmail: TextInputLayout, inputTextLayoutPassword: TextInputLayout) {
        viewModelScope.launch(Dispatchers.Main) {
            val username = inputTextLayoutUserName.editText!!.text.toString()
            val phoneNumber = inputTextLayoutPhoneNumber.editText!!.text.toString()
            val email = inputTextLayoutEmail.editText!!.text.toString()
            val password = inputTextLayoutPassword.editText!!.text.toString()

            when {
                // Validation code...
                else -> {
                    _registerState.emit(Event(Resource.Loading()))
                    try {
                        val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                        if (signInMethods.signInMethods?.size == 0) {
                            registerUseCase(email, password)
                            // Registration successful
                            val user = User(username, "", "", "", phoneNumber, email, "","")
                            _registerState.emit(Event(Resource.Success(user)))
                        } else {
                            // Email already registered
                            _registerState.emit(Event(Resource.Error("Email already registered")))
                        }
                    } catch (e: Exception) {
                        // Error occurred during sign-in methods fetch
                        _registerState.emit(Event(Resource.Error("Error occurred: ${e.message}")))
                    }
                }
            }
        }
    }
    fun saveUserData(user: User) {
        viewModelScope.launch(Dispatchers.Main) {
            _saveUserDataState.emit(Event(Resource.Loading()))
            try {
                saveUserDataUseCase(user)
                _saveUserDataState.emit(Event(Resource.Success(user)))
            } catch (e: Exception) {
                _saveUserDataState.emit(Event(Resource.Error("Error occurred: ${e.message}")))
            }
        }
    }
    fun loginWithEmail(
        inputTextLayoutEmail: TextInputLayout,
        inputTextLayoutPassword: TextInputLayout,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val email = inputTextLayoutEmail.editText!!.text.toString()
            val password = inputTextLayoutPassword.editText!!.text.toString()
            when {
                email.isEmpty() -> {
                    _loginWithEmailState.emit(Event(Resource.Error("Email is require")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "Required*"
                }

                !AuthValidation.isValidEmail(email = email) -> {
                    _loginWithEmailState.emit(Event(Resource.Error("Email is not valid")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "Not Valid"
                }

                password.isEmpty() -> {
                    _loginWithEmailState.emit(Event(Resource.Error("Password is require")))
                    inputTextLayoutPassword.isHelperTextEnabled = true
                    inputTextLayoutPassword.helperText = "Required*"
                }

                !AuthValidation.validatePassword(context, inputTextLayoutPassword) -> {
                    _loginWithEmailState.emit(Event(Resource.Error(inputTextLayoutPassword.helperText.toString())))
                }

                else -> {
                    _loginWithEmailState.emit(Event(Resource.Loading()))
                    try {
                        val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                        if (signInMethods.signInMethods?.size == 0) {
                            _loginWithEmailState.emit(Event(Resource.Error("Email does not exist")))
                        } else {
                            loginWithEmailUseCase(email, password)
                            firebaseAuth.currentUser?.isEmailVerified?.let { verified ->
                                if (verified) {
                                    _loginWithEmailState.emit(Event(Resource.Success("Login Successfully")))
                                } else {
                                    _loginWithEmailState.emit(Event(Resource.Error("Email is not verified, check your email")))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Error occurred during sign-in methods fetch
                        _registerState.emit(Event(Resource.Error("Error occurred: ${e.message}")))
                    }
                }

            }
        }
    }
}
