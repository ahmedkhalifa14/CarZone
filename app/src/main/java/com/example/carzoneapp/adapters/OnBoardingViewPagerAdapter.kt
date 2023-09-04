package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.OnBoardingLayoutBinding
import com.example.carzoneapp.ui.fragments.onboarding.OnBoardingFragment.Companion.MAX_STEP

class OnBoardingViewPagerAdapter: RecyclerView.Adapter<PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            OnBoardingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = MAX_STEP

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) =

        holder.itemView.run {

            with(holder) {
                if (position == 0) {
                    binding.onBoardingDescriptionTv.text =
                        context.getString(R.string.on_boarding_description_1)
                    binding.onBoardingTitleTv.text =
                        context.getString(R.string.on_boarding_title_1)
                    binding.onBoardingIv.setImageResource(R.drawable.onboarding1)
                }
                if (position == 1) {
                    binding.onBoardingDescriptionTv.text =
                        context.getString(R.string.on_boarding_description_2)
                    binding.onBoardingTitleTv.text =
                        context.getString(R.string.on_boarding_title_2)
                    binding.onBoardingIv.setImageResource(R.drawable.onboarding2)
                }
                if (position == 2) {
                    binding.onBoardingDescriptionTv.text =
                        context.getString(R.string.on_boarding_description_3)
                    binding.onBoardingTitleTv.text =
                        context.getString(R.string.on_boarding_title_3)
                    binding.onBoardingIv.setImageResource(R.drawable.onboarding3)
                }
                if (position == 3) {
                    binding.onBoardingDescriptionTv.text =
                        context.getString(R.string.on_boarding_description_4)
                    binding.onBoardingTitleTv.text =
                        context.getString(R.string.on_boarding_title_4)
                    binding.onBoardingIv.setImageResource(R.drawable.onboarding4)
                }
            }
        }
}

class PagerViewHolder(val binding: OnBoardingLayoutBinding) : RecyclerView.ViewHolder(binding.root)
