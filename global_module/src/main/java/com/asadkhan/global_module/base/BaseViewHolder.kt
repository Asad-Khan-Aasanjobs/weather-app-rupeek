package com.asadkhan.global_module.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.asadkhan.global_module.AbstractApplication

abstract class BaseViewHolder<DATA>(

  val ctx: Context = AbstractApplication.component().applicationContext,
  val v: View,
  var data: DATA? = null

) : RecyclerView.ViewHolder(v) {

  // No initialization in constructor
  // Data will be bound in the onBind() callback

  fun bindData(newData: DATA?) {
    if (newData == null) {
      error("Data in ViewHolder cannot be null! ViewHolder: ${javaClass.simpleName}")
    }
    this.data = newData
    onBind(data)
  }

  abstract fun onBind(newData: DATA?)

}
