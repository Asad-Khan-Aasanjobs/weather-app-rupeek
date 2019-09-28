package com.asadkhan.global_module.di

import android.app.Application
import android.support.annotation.NonNull
import com.asadkhan.global_module.BuildConfig.DEBUG
import com.asadkhan.global_module.network.ApiService
import com.asadkhan.global_module.network.NetworkManager
import com.asadkhan.global_module.network.NetworkService
import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers.io
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.createWithScheduler
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
class DataModule() {

  constructor(baseUrl: String) : this() {
    BASE_URL = baseUrl
  }

  @Module
  companion object {

    private const val CACHE = "Cache-Control"
    private const val SHARED_PREFERENCE_NAME = "weather_app_preferences"

    @JvmStatic
    var BASE_URL = "http://www.mocky.io/v2/"

    val FORMAT_ONE = "yyyy-MM-dd'T'HH:mm:ss"

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun logInterceptor(): Interceptor {
      return HttpLoggingInterceptor().setLevel(BODY)
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun networkCache(app: Application): Cache {
      //setup cache
      val httpCacheDirectory = File(app.cacheDir, "responses")
      val cacheSize = 200 * 1024 * 1024L // 200 MiB
      return Cache(httpCacheDirectory, cacheSize)
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun okHttpClient(
      cache: Cache,
      loggingInterceptor: Interceptor
    ): OkHttpClient {

      return OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor)
        .addNetworkInterceptor(cacheControlInterceptor())
        .cache(cache)
        .connectTimeout(120, SECONDS)
        .readTimeout(80, SECONDS)
        .writeTimeout(80, SECONDS).build()
    }

    private fun cacheControlInterceptor(): Interceptor {
      return Interceptor {
        val response = it.proceed(it.request())
        val maxAge = if (DEBUG) {
          //60 // One minute for debug version
          60 * 60 * 24 * 30 * 12 // One year, yeah. You read that right.
        }
        else {
          60 * 60 * 24 * 30 * 12 // One year, yeah. You read that right.
        }
        return@Interceptor response
          .newBuilder()
          .header(CACHE, "public, max-age=$maxAge")
          .build()
      }
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
      return Retrofit.Builder().baseUrl(BASE_URL).addCallAdapterFactory(createWithScheduler(io())).client(okHttpClient)
        .build()
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun gson(): Gson {
      return GsonBuilder().setDateFormat(FORMAT_ONE).setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).setLenient()
        .setPrettyPrinting().create()
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun apiService(retrofit: Retrofit): ApiService {
      return retrofit.create(ApiService::class.java)
    }

    @NonNull
    @Provides
    @Singleton
    @JvmStatic
    fun networkService(networkManager: NetworkManager): NetworkService {
      return networkManager
    }
    //
    //    @NonNull
    //    @Provides
    //    @Singleton
    //    @JvmStatic
    //    fun database(@AppContext context: Context): PokedexDatabase {
    //      return databaseBuilder(context, PokedexDatabase::class.java, "pokedex-db")
    //        .addCallback(MyCallback)
    //        .build()
    //    }
    //
    //    @NonNull
    //    @Provides
    //    @Singleton
    //    @JvmStatic
    //    fun sharedPreferences(@AppContext context: Context): SharedPreferences {
    //      return context.applicationContext.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
    //    }
    //
    //    fun loadDataFromAsset(context: Context = contex(), fileName: String = "pokedex.json"): String? {
    //      val json: String
    //      try {
    //        val iStream = context.assets.open(fileName)
    //        val size = iStream.available()
    //        val buffer = ByteArray(size)
    //        iStream.read(buffer)
    //        iStream.close()
    //        json = String(buffer, UTF_8)
    //      }
    //      catch (ex: Exception) {
    //        ex.printStackTrace()
    //        return ""
    //      }
    //      return json
    //    }
    //
    //    fun convertPokedexStringToJson(jsonString: String): List<PokemonData> {
    //      return tryHere({
    //        start()
    //        val wrapper = tryHere({
    //          gson().fromJson(jsonString, PokedexListWrapper::class.java)
    //        }, PokedexListWrapper(LinkedList()))
    //        stop("convertPokedexStringToJson")
    //        return@tryHere wrapper.pokemon
    //      }, mutableListOf())
    //    }
    //
    //    object MyCallback : RoomDatabase.Callback() {
    //
    //      override fun onCreate(db: SupportSQLiteDatabase) {
    //        super.onCreate(db)
    //        e("Database created!")
    //      }
    //
    //      override fun onOpen(db: SupportSQLiteDatabase) {
    //        super.onOpen(db)
    //        e("Database opened!")
    //      }
    //    }
    //
    //    fun prePopulateDatabase() {
    //      tryThis {
    //        pokemonTable()
    //          .getMaxIdObservable()
    //          .filter(Companion::onlyEmptyList)
    //          .doOnNext {
    //            tryThis {
    //              val jsonString = loadDataFromAsset()
    //              if (jsonString.isNullOrEmpty()) {
    //                e("JSON is empty")
    //              }
    //              else {
    //                insertValuesIntoTable(jsonString)
    //              }
    //            }
    //          }
    //          .subscribe(dbObserver())
    //      }
    //    }
    //
    //    private fun onlyEmptyList(it: List<Int?>) = it.isEmpty() || it[0] == null
    //
    //    private fun insertValuesIntoTable(jsonString: String) {
    //      tryThis {
    //        val pokemonDataList = convertPokedexStringToJson(jsonString)
    //        e("Pokemon WeatherDataPoint List size: ${pokemonDataList.size}")
    //        val pokemonEntitiesList = pokemonDataList.map { PokemonEntity(it) }
    //        start()
    //        pokemonTable().insertAll(pokemonEntitiesList)
    //        stop("pokemonTable().insertAll(pokemonEntitiesList)")
    //      }
    //    }
    //
    //    private fun <T> dbObserver(): Observer<T> {
    //      return withSubscriber(Consumer { e("Yay! DB Populated!") })
    //    }
    //
    //  }

  }
}
