package com.example.thundora.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.ApiResponse
import com.example.thundora.domain.model.api.Weather

@Database(entities =  [AlarmEntity::class, Weather::class,ApiResponse::class], version =2 )
@TypeConverters(ApiResponseConverter::class, Converters::class) // ✅ Add here
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