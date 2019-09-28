package com.asadkhan.global_module.domain

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WeatherDataPoint(
  @SerializedName("rain")
  val rain: Int, // 60
  @SerializedName("temp")
  val temp: Int, // 24
  @SerializedName("time")
  val time: Long, // 1564617600
  @SerializedName("wind")
  val wind: Int // 7
) : Parcelable
