package com.example.carzoneapp.ui.fragments.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.adapters.MessageAdapter
import com.example.carzoneapp.databinding.FragmentChatBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.User
import com.example.domain.entity.UserChat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<MainViewModel>()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val args: ChatFragmentArgs by navArgs()
    private var user: User? = null
    private var currentUser: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        setupAdsRecyclerView()
        homeViewModel.getUserInfoByUserId(firebaseAuth.currentUser?.uid.toString())
        user = args.user
        displayUserData(user!!)
        binding.sendBtn.isEnabled = false
        binding.messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val message = binding.messageBox.text.toString().trim()
                binding.sendBtn.isEnabled = message.isNotEmpty()
            }
            override fun afterTextChanged(editable: Editable) {
            }
        })
        binding.sendBtn.setOnClickListener {
            sendMessage()
        }

    }
    private fun displayUserData(user: User) {
        binding.chatUsernameTv.text = user.userName
        Glide.with(requireContext()).load(user.image).into(binding.chatUserImg)
    }
    private fun sendMessage() {
        val message = ChatMessage(
            firebaseAuth.currentUser?.uid.toString(),
            user!!.userId,
            binding.messageBox.text.toString(),
        )
        val userChat = UserChat(
            firebaseAuth.currentUser?.uid.toString(),
            firebaseAuth.currentUser?.uid + user!!.userId,
            user!!.userId,
            user!!.userName,
            currentUser!!.userName,
            currentUser!!.image,
            user!!.image,
            binding.messageBox.text.toString(),
            System.currentTimeMillis()
        )
        homeViewModel.sendMessage(message)
        homeViewModel.saveUserChatList(userChat)
        binding.messageBox.text.clear()
    }
    override fun onResume() {
        super.onResume()
        homeViewModel.getMessages(
            user!!.userId,
            firebaseAuth.currentUser?.uid.toString()
        )
    }
    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getMessagesState.collect(
                    EventObserver(
                        onLoading = {
                            //  binding.spinKitProgress.isVisible = true
                        },
                        onSuccess = { messages ->
                            messages.observe(viewLifecycleOwner) { messagesList ->
                                if (messagesList.isNotEmpty()) {
                                    messageAdapter.setMessages(messagesList)
                                    Timber.tag("getMessagesState").d("Data is present")

                                    for (message in messagesList) {
                                        Timber.tag("Message").d(message.toString())
                                    }
                                } else {
                                    Timber.tag("getMessagesState").d("No data found")
                                }

                            }
                        },
                        onError = {
                            // binding.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.sendMessageState.collect(
                    EventObserver(
                        onLoading = {
                            //  binding.spinKitProgress.isVisible = true
                        },
                        onSuccess = {
                            homeViewModel.getMessages(
                                user!!.userId,
                                firebaseAuth.currentUser?.uid.toString()
                            )
                        },
                        onError = {
                            // binding.spinKitProgress.isVisible = false
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.saveUserChatListState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.userInfoState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                             currentUser=it
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }
    }
    private fun setupAdsRecyclerView() {
        messagesRecyclerView = binding.messageRv
        messageAdapter = MessageAdapter(firebaseAuth.currentUser?.uid.toString(),messagesRecyclerView)
        messagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        messagesRecyclerView.adapter = messageAdapter
    }

}