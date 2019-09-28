package com.asadkhan.global_module.di;


import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.asadkhan.global_module.di.qualifiers.AppContext;
import com.asadkhan.global_module.network.NetworkService;
import com.google.gson.Gson;
import dagger.Component;
import javax.inject.Singleton;


@Singleton
@Component(
    modules = {
        AppModule.class,
        DataModule.class,
        //ServicesModule.class
    }
)
public interface AppComponent {
  
  @AppContext
  @NonNull
  Context getApplicationContext();
  
  
  @NonNull
  Application getApplication();
  
  
  @NonNull
  NetworkService networkService();
  
  
  @NonNull
  Gson gson();
  
}
