package com.riteshmaagadh.whatsappgrouplinks.ui

import android.content.Context
import android.net.ConnectivityManager

object Utils {

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

}