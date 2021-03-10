package com.example.weatherapp.dagger.module

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.NonNull
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.dagger.component.PROVIDER_TAG
import com.example.weatherapp.repo.source.local.WeatherDAO
import com.example.weatherapp.repo.source.local.WeatherDatabase
import com.example.weatherapp.workmanager.DATABASE_NAME
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@SuppressLint("LogNotTimber")
@Module
class DatabaseProvider {

    @Provides
    @Singleton
    fun provideDatabase(@NonNull application: WeatherApplication): WeatherDatabase {
        Log.i(PROVIDER_TAG, "Database provided...")
        // If you want to keep data after upgrade version of database
        // Use this
//        return Room.databaseBuilder(application, WeatherDatabase::class.java, "WeatherDB.db")
//            .allowMainThreadQueries()
//            .addMigrations(MIGRATION_1_2)
//            .build()

        return Room.databaseBuilder(application, WeatherDatabase::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        /**
                         * Populate data for the first time create database
                         */
                        CoroutineScope(Dispatchers.IO).launch {
                            launch {
                                db.execSQL(
                                    "INSERT INTO weatherEntities(base,visibility,dt,id,name,cod,timezone) " +
                                            "values('stations', 10000.0,1614257929,1566083,'Ho Chi Minh City',200,25200)"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO coordEntity(weatherResponseDataId,lon,lat) " +
                                            "values(1566083,106.6602,10.7626)"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO weatherEntity(weatherResponseDataId,id,main,description,icon) " +
                                            "values(1566083,800,'Clear','clear sky','01n')"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO mainEntity(weatherResponseDataId,temp,feels_like,pressure,humidity,temp_min,temp_max) " +
                                            "values(1566083,300.15,302.06,1006,78,300.15,300.15)"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO windEntity(weatherResponseDataId,speed,deg) " +
                                            "values(1566083,4.63,170)"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO cloudsEntity(weatherResponseDataId,'all') " +
                                            "values(1566083,0)"
                                )
                            }
                            launch {
                                db.execSQL(
                                    "INSERT INTO sysEntity(weatherResponseDataId,type,id,country,sunrise,sunset) " +
                                            "values(1566083,1,9314,'VN',1614208212,1614250983)"
                                )
                            }
                        }
                    }
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDAO(@NonNull database: WeatherDatabase): WeatherDAO {
        Log.i(PROVIDER_TAG, "DAO provided...")
        return database.getWeatherDAO()
    }

}