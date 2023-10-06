package com.example.carzoneapp.utils

import android.content.Context
import com.example.carzoneapp.R
import com.example.domain.entity.ProfileItem


object Constants {
    const val REQUEST_CHECK_SETTINGS = 2

    fun getAccountItems(context: Context): List<ProfileItem> {
        return listOf(
            ProfileItem(
                1,
                R.drawable.credit,
                R.drawable.right_arrow,
                context.getString(R.string.account_item_title1),
                context.getString(R.string.account_item_subtitle1)
            ),
            ProfileItem(
                2,
                R.drawable.list,
                R.drawable.right_arrow,
                context.getString(R.string.account_item_title2),
                context.getString(R.string.account_item_subtitle2)
            ),
            ProfileItem(
                3,
                R.drawable.settings,
                R.drawable.right_arrow,
                context.getString(R.string.account_item_title3),
                context.getString(R.string.account_item_subtitle3)
            ),
            ProfileItem(
                4,
                R.drawable.help,
                R.drawable.right_arrow,
                context.getString( R.string.account_item_title4),
                context.getString(R.string.account_item_subtitle4)
            ),
            ProfileItem(
                5,
                R.drawable.language,
                R.drawable.right_arrow,
                context.getString(R.string.account_item_title5),
                context.getString(R.string.account_item_subtitle5)
            ),

            )
    }



}