package com.example.thundora.model.localdatasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thundora.model.pojos.ForecastDto

@Database(entities = [ForecastDto::class], version = 1)
 abstract class ForecastDataBase : RoomDatabase(){

    abstract fun getForecastDao(): Dao

    companion object{
        @Volatile
        private var instance: ForecastDataBase? = null
        fun getInstance(context: Context): ForecastDataBase {
            return instance ?: synchronized(this){
                val INSTANCE = Room.databaseBuilder(context, ForecastDataBase::class.java, "roomdb").build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }

}