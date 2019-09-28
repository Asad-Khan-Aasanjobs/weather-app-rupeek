package com.asadkhan.base_app

import android.content.Context
import android.view.View
import com.asadkhan.global_module.base.BaseViewHolder
import com.asadkhan.global_module.contex
import com.asadkhan.global_module.domain.WeatherDataPoint
import com.asadkhan.global_module.tryThis
import kotlinx.android.synthetic.main.weather_row_list.view.tv_date
import kotlinx.android.synthetic.main.weather_row_list.view.tv_rain
import kotlinx.android.synthetic.main.weather_row_list.view.tv_temperature
import kotlinx.android.synthetic.main.weather_row_list.view.tv_wind
import java.util.Date

open class WeatherViewHolder(
  ctx: Context = contex(), view: View
) :
  BaseViewHolder<WeatherDataPoint>(ctx, view) {

  var id = -1

  override fun onBind(newData: WeatherDataPoint?) {
    val tryThisResult = tryThis {

      newData.let { dataPoint ->
        if (dataPoint is WeatherDataPoint) {
          bindPokemonDetailsAsync(newData)
          //loadPokemonImage(dataPoint)
        }
      }
    }
    if (tryThisResult) {
      return
    }
    else {
      error("Data is null or invalid! Please check")
    }
  }

  private fun bindPokemonDetailsAsync(newData: WeatherDataPoint?) {
    newData?.let {
      itemView.tv_date.text = Date(it.time * 1000).toString()
      itemView.tv_rain.text = "${it.rain}%"
      itemView.tv_temperature.text = "${it.temp} °C"
      itemView.tv_wind.text = "${it.wind} km/hr"
    }
  }

  //
  //  private fun loadPokemonImage(it: PokemonEntity, front: Boolean = true) {
  //    tryThis {
  //      val showFrontImage = if (front)
  //        ctx.getDrawableId("${it.id}.png")
  //      else
  //        ctx.getDrawableId("back/${it.id}.png")
  //
  //      ImageUtils()
  //        .loadImage(
  //          ctx, itemView.iv_sprite, showFrontImage,  it.frontDefault, R.drawable.ic_pokeball_loading
  //        )
  //    }
  //  }
}
