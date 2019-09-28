package com.asadkhan.base_app

import com.asadkhan.global_module.AbstractApplication
import com.asadkhan.global_module.di.AppComponent
import com.asadkhan.global_module.di.AppModule
import com.asadkhan.global_module.di.DaggerAppComponent

class MainApplication : AbstractApplication() {
  var component: AppComponent? = null

  override fun createComponent(): AppComponent {
    component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    return component!!
  }

}
