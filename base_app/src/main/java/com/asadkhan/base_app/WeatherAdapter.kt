package com.asadkhan.base_app

import android.support.v7.widget.RecyclerView
import android.view.View
import com.asadkhan.global_module.base.BaseAdapter
import com.asadkhan.global_module.domain.WeatherDataPoint

class WeatherAdapter(
  items: HashSet<WeatherDataPoint> = linkedSetOf()
) : BaseAdapter<WeatherDataPoint>(
  items = LinkedHashSet(items), itemsFull = LinkedHashSet(items)
) {
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is WeatherViewHolder) {
      val elementAt = items.elementAt(position)
      val element = elementAt as WeatherDataPoint?
      element?.let {
        holder.bindData(it)
      }
    }
  }

  override fun defaultValue(): WeatherDataPoint {
    return WeatherDataPoint(10, 27, 1564617600, 23)
  }

  override fun viewHolderLayout(): Int {
    return R.layout.weather_row_list
  }

  override fun getViewHolder(itemView: View): RecyclerView.ViewHolder {
    return WeatherViewHolder(view = itemView)
  }

  override fun update(newItems: HashSet<Any?>) {
    this.items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun append(newItems: HashSet<Any?>, refreshUI: Boolean) {
    this.items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun clear(refreshUI: Boolean) {
    this.items.clear()
    notifyDataSetChanged()
  }

  override fun appendAll(newItems: Collection<Any?>, refreshUI: Boolean) {
    update(LinkedHashSet(newItems))
    if (refreshUI) {
      notifyDataSetChanged()
    }
  }

  override fun getItemCount(): Int {
    return this.items.size
  }

}
