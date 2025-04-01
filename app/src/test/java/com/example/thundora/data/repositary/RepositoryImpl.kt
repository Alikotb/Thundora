package com.example.thundora.data.repositary

import com.example.thundora.data.local.source.ILocalDataSource
import com.example.thundora.data.remote.remotedatasource.IRemoteDataSource
import com.example.thundora.domain.model.api.AlarmEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalTime


class RepositoryImplTest {
    val alarm1 = AlarmEntity(0, "Alaram0", LocalTime.now(), 60)
    val alarm2 = AlarmEntity(1, "Alaram1", LocalTime.now(), 60)
    val alarm3 = AlarmEntity(2, "Alaram2", LocalTime.now(), 60)
    val alarm4 = AlarmEntity(3, "Alaram3", LocalTime.now(), 60)

    private lateinit var fakelocalDataSource: ILocalDataSource
    private lateinit var fakeRemoteDataSource: IRemoteDataSource
    private lateinit var repo: RepositoryImpl

    @Before
    fun setup() {
        fakelocalDataSource = FakeDataSource(mutableListOf())
        fakeRemoteDataSource = mockk()
        repo = RepositoryImpl(fakeRemoteDataSource, fakelocalDataSource)

    }

    @Test
    fun insertAlarm_addsAlarmToRepository() = runTest {
        val newAlarm = AlarmEntity(4, "New Alarm", LocalTime.now(), 45)
        repo.insertAlarm(newAlarm)

        val alarms = fakelocalDataSource.getAllAlarms().first()
        assertThat(alarms, hasItem(newAlarm))
    }

    @Test
    fun getAllAlarms_returnsCorrectData() = runTest {
        repo.insertAlarm(alarm1)
        repo.insertAlarm(alarm2)
        repo.insertAlarm(alarm3)
        repo.insertAlarm(alarm4)

        val alarms = repo.getAllAlarms().toList().first()
        assertThat(alarms.size, `is`(4))
    }

    @Test
    fun getWeather_returnsCorrectData() = runTest {

        coEvery {
            fakeRemoteDataSource.getWeather(any(), any(), any(), any())
        } returns mockk(relaxed = true)

        val weather = repo.getWeather(1.0, 1.0, "metric", "en")

        assertNotNull(weather)
        coVerify {
            fakeRemoteDataSource.getWeather(any(), any(), any(), any())
        }

    }

    @Test
    fun getForecast_returnsCorrectData() = runTest {
        coEvery {
            fakeRemoteDataSource.getForecast(any(), any(), any(), any())
        } returns mockk(relaxed = true)
        val forecast = repo.getForecast(1.0, 1.0, "metric", "en")
        assertNotNull(forecast)
        coVerify {
            fakeRemoteDataSource.getForecast(any(), any(), any(), any())
        }
    }

}