package com.example.weatherapp.dagger.component

import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.dagger.module.DatabaseProvider
import com.example.weatherapp.dagger.module.RepositoryProvider
import com.example.weatherapp.dagger.module.ServiceProvider
import com.example.weatherapp.workmanager.MyWorkManagerInitializer
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Component is stand for the "THE INJECTOR"
 */
@Component(
    modules = [AppModule::class, DatabaseProvider::class, RepositoryProvider::class, ServiceProvider::class]
)
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        /**
         * This is how It is done. Using @BindsInstance in your [AppComponent]
         * will inject [application] to all of your modules. In your case just to [AppModule] or
         * another modules need it
         */
        @BindsInstance
        fun application(application: WeatherApplication): Builder
    }

    /**
     *  This tells Dagger that [WeatherApplication] requests injection so the graph needs to
     *  satisfy all the dependencies of the fields that [WeatherApplication] is requesting.
     */
    fun inject(application: WeatherApplication)

    /**
     *  This tells Dagger that [MyWorkManagerInitializer] requests injection so the graph needs to
     *  satisfy all the dependencies of the fields that [MyWorkManagerInitializer] is requesting.
     */
    fun inject(workerInitializer: MyWorkManagerInitializer)

//    fun inject(activity: MainActivity) -> if it requests injection [@Inject]
}

const val PROVIDER_TAG = "PROVIDER_TAG"