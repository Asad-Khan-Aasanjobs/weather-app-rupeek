package com.asadkhan.base_app

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.asadkhan.global_module.AbstractApplication
import com.asadkhan.global_module.asyncOp
import com.asadkhan.global_module.base.GPSTracker
import com.asadkhan.global_module.contex
import com.asadkhan.global_module.domain.WeatherListData
import com.asadkhan.global_module.network.NetworkService
import com.asadkhan.global_module.tryThis
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_weather_list.rv_pokemon_list
import kotlinx.android.synthetic.main.fragment_weather_list.srl_swipe_refresh
import kotlinx.android.synthetic.main.fragment_weather_list.tv_lat_long

/**
 * A placeholder fragment containing a simple view.
 */
class WeatherListFragment : Fragment() {

  private val adapter = WeatherAdapter()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_weather_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val networkService = AbstractApplication.component().networkService()

    tryThis {
      rv_pokemon_list.adapter = adapter
      rv_pokemon_list.layoutManager = LinearLayoutManager(context)

      srl_swipe_refresh.setOnRefreshListener {
        fetchData(networkService)
      }

      setLocation()

      fetchData(networkService)
      true
    }
  }

  private fun setLocation() {
    val location = GPSTracker.getLocation(contex())
    location?.let {
      val locationText = "Lat:${location.latitude}  Long:${location.longitude}"
      tv_lat_long.text = locationText
    }
  }

  private fun fetchData(networkService: NetworkService) {
    val subscribe: Disposable = networkService.get(WeatherListData::class.java, "5d3a99ed2f0000bac16ec13a")
      .map { response -> response.body() }
      .asyncOp()
      .subscribe({
        it?.let { list ->
          adapter.appendAll(list.data, true)
        }
      }, {
        it.printStackTrace()
        Handler().post {
          Toast.makeText(context, "Oops something went wrong", Toast.LENGTH_LONG).show()
        }
      })
  }
}
