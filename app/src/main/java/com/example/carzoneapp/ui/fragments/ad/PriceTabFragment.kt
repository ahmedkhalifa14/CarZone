package com.example.carzoneapp.ui.fragments.ad

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.carzoneapp.databinding.FragmentPriceTabBinding
import com.example.domain.entity.AdData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PriceTabFragment : Fragment() {
    private var _binding: FragmentPriceTabBinding? = null
    val priceTabFragmentBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPriceTabBinding.inflate(inflater, container, false)
        return priceTabFragmentBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    fun getPriceTabData(): AdData {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val price = priceTabFragmentBinding!!.inputTextLayoutPrice.editText!!.text.toString()
        val title = priceTabFragmentBinding!!.inputTextLayoutTitle.editText!!.text.toString()
        val negotiable = priceTabFragmentBinding!!.checkboxNegotiable.isChecked
        val description =
            priceTabFragmentBinding!!.inputTextLayoutDescribe.editText!!.text.toString()
        return AdData(title, description, negotiable, price, "", date)
    }

}
