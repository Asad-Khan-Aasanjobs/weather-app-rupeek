package com.asadkhan.global_module

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.os.Handler
import android.view.View
import android.view.View.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import com.asadkhan.global_module.AbstractApplication.Companion.component
import com.asadkhan.global_module.network.NetworkService
import com.asadkhan.global_module.utils.Profiler
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers.io
import timber.log.Timber.w
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Wraps the passed function with a try catch, handles all errors with generic
 * EHM (Error Handling Mechanism); It returns `true` if the function completed
 * successfully & `false` if exception occurred. Only works if block does not return.
 * */
fun tryThis(block: () -> Any?): Boolean {
  return try {
    block()
    true
  }
  catch (e: Exception) {
    e.printStackTrace()
    false
  }
}

/**
 * Wraps the passed function with a try catch, handles all errors with generic
 * EHM (Error Handling Mechanism). Returns null on failure
 * */
fun <RETURN> tryHere(block: () -> RETURN?): RETURN? {
  try {
    return block()
  }
  catch (e: Exception) {
    e.printStackTrace()
  }
  return null
}

/**
 * Wraps the passed function with a try catch, handles all errors with generic
 * EHM (Error Handling Mechanism). Returns default (Non-null) value on failure
 * */
fun <RETURN> tryHere(block: () -> RETURN?, default: RETURN): RETURN {
  try {
    return block() ?: default
  }
  catch (e: Exception) {
    e.printStackTrace()
  }
  return default
}

// Generic

fun Any?.isApiAboveOrEqual(minLevel: Int) = SDK_INT >= minLevel
fun Any?.isOreo() = SDK_INT >= O
fun Any?.isNougat() = SDK_INT >= N
fun Any?.isMarshmellow() = SDK_INT >= M
fun Any?.isLollipop() = SDK_INT >= LOLLIPOP

fun Any?.zeroPaddedNumber(number: Int): String {
  return when {
    number > 9999 -> "$number"     // 10000 & beyond
    number > 999  -> "0$number"    // 1000 to 9999
    number > 99   -> "00$number"   // 100 to 999
    number > 9    -> "000$number"  // 10 to 99
    else          -> "0000$number" // 0 to 9
  }
}

fun networkService(): NetworkService = component().networkService()

//fun database(): PokedexDatabase = component().database()
//fun pokemonTable(): PokemonDao = component().database().pokemonDao()
fun gson(): Gson = component().gson()
fun contex(): Context = component().application

// Time based

fun Any?.now() = Date().time

// Profiling

fun Any?.start() = Profiler.start()
fun Any?.stop(name: String = "Method") = Profiler.stop(name)

// Context based

fun Context?.asyncTost(text: String): Boolean {
  if (this == null) {
    return false
  }
  return tryThis {
    this.let {
      Handler(mainLooper).post {
        val makeText = makeText(applicationContext, text, LENGTH_LONG)
        makeText?.show()
      }
    }
  }
}

fun Context?.launchService(intent: Intent) {
  if (isOreo()) {
    this?.startForegroundService(intent)
  }
  else {
    this?.startService(intent)
  }
}

fun Context?.isNetworkAvailable(): Boolean {
  this?.let {
    val activeNetworkInfo = it.networkInfo()
    return activeNetworkInfo?.isConnected ?: false
  }
  return false
}

fun Context?.isWifiAvailable(): Boolean {
  this?.let {
    val activeNetworkInfo = it.networkInfo()
    return activeNetworkInfo?.subtype == ConnectivityManager.TYPE_WIFI
  }
  return false
}

fun Context?.networkInfo(): NetworkInfo? {
  val connectivityManager = this?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
  return connectivityManager.activeNetworkInfo
}

fun Context?.getDrawableId(name: String): Int {
  return this?.resources?.getIdentifier(name, "drawable", this.packageName) ?: -1
}

// View based

fun View?.show(): View? {
  tryThis {
    this?.apply {
      visibility = VISIBLE
    }
  }
  return this
}

fun View?.hide(gone: Boolean = true): View? {
  tryThis {
    this?.apply {
      visibility = if (gone) GONE else INVISIBLE
    }
  }
  return this
}

// RxJava extensions

public fun <DATA> withSubscriber(
  successConsumer: Consumer<DATA> = Consumer { w("Consumer: $it") },
  errorConsumer: Consumer<Throwable> = Consumer { t -> t.printStackTrace() }
): Observer<DATA> {
  return object : Observer<DATA> {

    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: DATA) {
      tryThis {
        successConsumer.accept(t)
      }
    }

    override fun onError(e: Throwable) {
      tryThis {
        errorConsumer.accept(e)
      }
    }
  }
}

fun <T> Observable<T>.ioThread(delayInMillis: Long = 0): Observable<T> {
  return this.subscribeOn(io()).delay(delayInMillis, TimeUnit.MILLISECONDS)
}

fun <T> Observable<T>.uiThread(): Observable<T> {
  return this.observeOn(mainThread())
}

fun <T> Observable<T>.async(scheduler: Scheduler = io()): Observable<T> {
  return this.subscribeOn(scheduler)
}

fun <T> Observable<T>.asyncOp(scheduler: Scheduler = io()): Observable<T> {
  return this.subscribeOn(scheduler).observeOn(mainThread())
}
