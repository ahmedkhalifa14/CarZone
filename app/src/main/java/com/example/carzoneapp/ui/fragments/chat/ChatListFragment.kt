package com.example.carzoneapp.ui.fragments.chat

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
import com.example.carzoneapp.adapters.ChatListAdapter
import com.example.carzoneapp.databinding.FragmentChatListBinding
import com.example.carzoneapp.ui.viewmodel.MainViewModel
import com.example.carzoneapp.utils.EventObserver
import com.example.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatListRecyclerView: RecyclerView
    var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservables()
        setupChatListRecyclerView()
        homeViewModel.getUserChatList(firebaseAuth.currentUser?.uid.toString())

        chatListAdapter.setOnItemClickListener { userChat ->
            if (firebaseAuth.currentUser?.uid.toString() == userChat.userId) {
                homeViewModel.getUserInfoByUserId(userChat.otherUserId)
            } else {
                homeViewModel.getUserInfoByUserId(userChat.userId)
            }
            val action =
                user?.let { ChatListFragmentDirections.actionChatListFragmentToChatFragment(it) }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
    }

    private fun subscribeToObservables() {
        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.saveUserChatListState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
                homeViewModel.getUserChatListState.collect(
                    EventObserver(
                        onLoading = {
                        },
                        onSuccess = { chatList ->
                            chatListAdapter.differ.submitList(chatList)
                            Timber.tag("chatList").d(chatList.toString())
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
                            user = it
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    )
                )
            }
        }

    }

    private fun setupChatListRecyclerView() {
        chatListRecyclerView = binding.chatListRv
        chatListAdapter = ChatListAdapter(firebaseAuth.currentUser?.uid.toString())
        chatListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        chatListRecyclerView.adapter = chatListAdapter
    }

}