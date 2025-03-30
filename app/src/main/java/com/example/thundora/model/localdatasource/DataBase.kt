package com.example.thundora.model.localdatasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thundora.model.pojos.api.AlarmEntity
import com.example.thundora.model.pojos.api.Weather

@Database(entities =  [AlarmEntity::class, Weather::class], version =2 )
 abstract class WeatherDataBase : RoomDatabase(){
    abstract fun getForecastDao(): Dao
    companion object{
        @Volatile
        private var instance: WeatherDataBase? = null
        fun getInstance(context: Context): WeatherDataBase {
            return instance ?: synchronized(this){
                val INSTANCE = Room.databaseBuilder(context, WeatherDataBase::class.java, "roomdb").build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }
}