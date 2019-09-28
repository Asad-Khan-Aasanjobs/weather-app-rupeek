package com.asadkhan.global_module

import android.app.Application
import com.asadkhan.global_module.di.AppComponent
import com.asadkhan.global_module.di.DataModule
import dagger.internal.Preconditions

/**
 * The base application class responsible for exposing Dagger injection
 * */
abstract class AbstractApplication : Application() {
  companion object {

    var component: AppComponent? = null

    fun component(): AppComponent {
      val errorMessage = "Component cannot be null! Please check if you are instantiating DaggerAppComponent Class"
      return Preconditions.checkNotNull(component!!, errorMessage)
    }
  }

  override fun onCreate() {
    super.onCreate()
    component = createComponent()
  }

  abstract fun createComponent(): AppComponent
}
