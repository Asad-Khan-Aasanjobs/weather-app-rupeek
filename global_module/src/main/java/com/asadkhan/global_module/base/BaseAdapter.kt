package com.asadkhan.global_module.base

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import com.asadkhan.global_module.tryHere
import com.asadkhan.global_module.tryThis
import com.asadkhan.global_module.uiThread
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject.create
import java.util.concurrent.TimeUnit.MILLISECONDS

@Suppress("UNCHECKED_CAST")
public abstract class BaseAdapter<T>(
  open var items: HashSet<Any?>, open val itemsFull: HashSet<Any?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val viewClickSubject = create<T>()
  private val viewClickGenericSubject = create<Any?>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = from(parent.context).inflate(viewHolderLayout(), parent, false)
    val viewHolder = getViewHolder(view)
    viewHolder.itemView.setOnClickListener {
      if (viewHolder is BaseViewHolder<*>) {
        // This conversion should not fail as long as
        // the adapter:view-holder contract is strictly adhered to
        tryThis {
          val item = viewHolder.data as T
          viewClickSubject
            .onNext(tryHere({ item }, defaultValue()))
        }
      }
    }
    return viewHolder
  }

  abstract fun defaultValue(): T

  fun clickStream(): Observable<T> {
    return viewClickSubject.debounce(800, MILLISECONDS, io()).uiThread()
  }

  @LayoutRes
  abstract fun viewHolderLayout(): Int

  abstract fun getViewHolder(itemView: View): RecyclerView.ViewHolder

  abstract fun update(newItems: HashSet<Any?>)

  @Deprecated(message = "Deprecated in favor of update()")
  abstract fun append(newItems: HashSet<Any?>, refreshUI: Boolean)

  abstract fun clear(refreshUI: Boolean)

  abstract fun appendAll(newItems: Collection<Any?>, refreshUI: Boolean)
}
