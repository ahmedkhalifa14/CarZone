package com.example.carzoneapp.ui.fragments.ad

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.FragmentPriceTabBinding
import com.example.domain.entity.AdData


class PriceTabFragment : Fragment() {
    private var _binding: FragmentPriceTabBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPriceTabBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.locationCard.setOnClickListener {
            Log.d("PriceTabFragment", "locationCard clicked")
            findNavController().navigate(R.id.action_sellDetailsFragment_to_chooseLocationFragment)
        }
    }

    fun getPriceTabData(): AdData {
        val calendar =Calendar.getInstance()
        val date = calendar.time
        val price = binding!!.inputTextLayoutPrice.editText!!.text.toString()
        val title = binding!!.inputTextLayoutTitle.editText!!.text.toString()
        val negotiable = binding!!.checkboxNegotiable.isChecked
        val description = binding!!.inputTextLayoutDescribe.editText!!.text.toString()
        return AdData(title, description, negotiable, price, "",date)
    }
}
