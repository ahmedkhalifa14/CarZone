package com.example.carzoneapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.HomeAdItemBinding
import com.example.carzoneapp.databinding.MyAdsItemBinding
import com.example.carzoneapp.helper.extractDateAndTime
import com.example.domain.entity.Ad


class AdsAdapter(private val type: Int, private val onItemClickListener: ((Ad) -> Unit)? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ViewType {
        TYPE_ONE,
        TYPE_TWO
    }

    inner class AdsAdapterViewHolder(val adsBinding: HomeAdItemBinding) :
        RecyclerView.ViewHolder(adsBinding.root)

    inner class MyAdsAdapterViewHolder(val myAdsBinding: MyAdsItemBinding) :
        RecyclerView.ViewHolder(myAdsBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(
            oldItem: Ad,
            newItem: Ad
        ): Boolean {
            return oldItem.vehicle.vehicleId == newItem.vehicle.vehicleId
        }

        override fun areContentsTheSame(
            oldItem: Ad,
            newItem: Ad
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.TYPE_ONE.ordinal -> {
                AdsAdapterViewHolder(
                    HomeAdItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )

            }

            ViewType.TYPE_TWO.ordinal -> {
                MyAdsAdapterViewHolder(
                    MyAdsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ads = differ.currentList[position]
        when (holder.itemViewType) {
            ViewType.TYPE_ONE.ordinal -> {
                val adsViewHolder = holder as AdsAdapter.AdsAdapterViewHolder
                adsViewHolder.itemView.apply {
                    Glide.with(this).load(ads.vehicleImages[0])
                        .into(adsViewHolder.adsBinding.adImg)
                    adsViewHolder.adsBinding.adName.text = ads.adsData.title
                    adsViewHolder.adsBinding.adPrice.text = ads.adsData.price
                    if (ads.adsData.negotiable) {
                        adsViewHolder.adsBinding.negotiableTv.text = "Negotiable"
                    } else {
                        adsViewHolder.adsBinding.negotiableTv.text = "Not negotiable"
                    }

                    val firebaseDate = ads.adsData.date
                    val dateAndTime = extractDateAndTime(firebaseDate!!)
                    val date = dateAndTime.day + " " + dateAndTime.month
                    adsViewHolder.adsBinding.listedDate.text = date
//                    setOnClickListener {
//                        onItemClickListener?.let { it(ads,position) }
//                    }
                    setOnClickListener {
                        onItemClickListener?.invoke(ads)
                    }
                }
            }

            ViewType.TYPE_TWO.ordinal -> {
                val myAdsViewHolder = holder as AdsAdapter.MyAdsAdapterViewHolder
                myAdsViewHolder.itemView.apply {
                    Glide.with(this).load(ads.vehicleImages[0])
                        .into(myAdsViewHolder.myAdsBinding.carImg)
                    myAdsViewHolder.myAdsBinding.carName.text = ads.adsData.title
                    myAdsViewHolder.myAdsBinding.carPrice.text = ads.adsData.price
                    if (ads.adsData.negotiable) {
                        myAdsViewHolder.myAdsBinding.negotiableTv.text = "Negotiable"
                    } else {
                        myAdsViewHolder.myAdsBinding.negotiableTv.text = "Not negotiable"
                    }
                    val firebaseDate = ads.adsData.date
                    val dateAndTime = extractDateAndTime(firebaseDate!!)
                    val date = dateAndTime.day + " " + dateAndTime.month
                    myAdsViewHolder.myAdsBinding.listedDate.text = date
                    setOnClickListener {
                        onItemClickListener?.invoke(ads)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface OnItemClickListener {
        fun onItemClick(childPosition: Int)
    }

    //    private var onItemClickListener: ((Ad) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (Ad) -> Unit) {
//        onItemClickListener = listener
//    }

//    private var onItemClickListener: ((Ad, Int) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (Ad, Int) -> Unit) {
//        onItemClickListener = listener
//    }


    override fun getItemViewType(position: Int): Int {
        //return if (position % 2 == 0) TYPE_VIEW_HOLDER_ONE else TYPE_VIEW_HOLDER_TWO
        return when (type) {
            1 -> {
                ViewType.TYPE_ONE.ordinal
            }
            2 -> {
                ViewType.TYPE_TWO.ordinal
            }
            else -> throw IllegalArgumentException("Invalid value")

        }
    }


}