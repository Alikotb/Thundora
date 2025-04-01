package com.example.thundora.data.local.source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.thundora.data.local.database.Dao
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var database: WeatherDataBase
    private lateinit var dao: Dao

    private lateinit var shared: SharedPreference

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.getForecastDao()
        shared = mockk(relaxed = true)
        localDataSource = LocalDataSource(dao,shared)
    }

    @After
    fun tearDown() = database.close()


}