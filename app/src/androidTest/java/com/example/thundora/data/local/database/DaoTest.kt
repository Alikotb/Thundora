package com.example.thundora.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.thundora.domain.model.api.AlarmEntity
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime


@RunWith(AndroidJUnit4::class)
@SmallTest
class DaoTest {
    private lateinit var daoTest: WeatherDataBase
    private lateinit var dao: Dao

    @Before
    fun setup() {
        daoTest = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
        dao = daoTest.getForecastDao()
    }

    @After
    fun tearDown() = daoTest.close()

    @Test
    fun insertAlarm_StoresAlarmInDatabase()= runTest {

        val alarm= AlarmEntity(0,"Alaram1",LocalTime.now(),60)
        dao.updateAlarm(alarm)

        dao.insertAlarm(alarm)

        val result = dao.getAlarmById(0)
        assertNotNull(result as AlarmEntity)
        assertThat(result.id, `is`(alarm.id))
        assertThat(result.time, `is`(alarm.time))
        assertThat(result.label, `is`(alarm.label))
        assertThat(result.duration, `is`(alarm.duration))


    }

    @Test
    fun updateAlarm_UpdatesAlarmInDatabase() = runTest {
        val alarm= AlarmEntity(0,"Alaram1",LocalTime.now(),60)
        dao.insertAlarm(alarm)
        val updatedAlaram=alarm.copy(label = "updated")
        dao.updateAlarm(updatedAlaram)
        val result = dao.getAlarmById(alarm.id)
        assertNotNull(result)
        assertThat(result?.label, `is`("updated"))
    }

}