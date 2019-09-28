package com.asadkhan.global_module.domain

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WeatherListData(
  @SerializedName("data")
  val `data`: List<WeatherDataPoint>
) : Parcelable
