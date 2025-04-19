package com.example.thundora.data.local.source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.thundora.data.local.database.Dao
import com.example.thundora.data.local.database.WeatherDataBase
import com.example.thundora.data.local.sharedpreference.SharedPreference
import com.example.thundora.domain.model.api.AlarmEntity
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

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



    @Test
    fun insertAlarm_StoresAlarmInDatabase() = runTest {
        val alarm = AlarmEntity(0, "Alaram1", LocalTime.now(), 60)

       localDataSource.insertAlarm(alarm)
        val result = localDataSource.getAlarmById(alarm.id)

        assertNotNull(result)
        assertThat(result?.label, `is`("Alaram1"))
        assertThat(result?.duration, `is`(60))
        assertThat(result?.id, `is`(result?.id))
    }



    @Test
    fun updateAlarm_UpdatesAlarmInDatabase() = runTest {
        val alarm= AlarmEntity(0,"Alaram1",LocalTime.now(),60)
        localDataSource.insertAlarm(alarm)
        val updatedAlaram=alarm.copy(label = "updated")
        localDataSource.updateAlarm(updatedAlaram)
        val result = localDataSource.getAlarmById(alarm.id)
        assertNotNull(result)
        MatcherAssert.assertThat(result?.label, `is`("updated"))
    }

}