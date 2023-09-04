package com.example.carzoneapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.adapters.AccountItemAdapter
import com.example.carzoneapp.databinding.FragmentAccountBinding
import com.example.carzoneapp.utils.Constants


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding
    private lateinit var accountItemAdapter: AccountItemAdapter
    private lateinit var accountItemRV: RecyclerView
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