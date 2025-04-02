package com.example.thundora.view.alarm.alarmviewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thundora.data.repositary.RepositoryImpl
import com.example.thundora.domain.model.api.AlarmEntity
import com.example.thundora.domain.model.api.Response
import com.example.thundora.services.AlarmScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AlarmViewModelTest {
    private lateinit var viewModel: AlarmViewModel
    private lateinit var repository: RepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    val testTime = LocalTime.of(8, 30)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxUnitFun = true)

        coEvery {
            repository.getAllAlarms()
        } returns flowOf(emptyList())
        coEvery {
            repository.insertAlarm(any())
        } returns Unit
        coEvery {
            repository.deleteAlarmById(any())
        }

        viewModel = AlarmViewModel(repository, mockk<AlarmScheduler>())

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllAlarms() = runTest {
        val alarmList = listOf(
            AlarmEntity(0, "ali", testTime, 60),
            AlarmEntity(1, "ali", testTime, 60),
            AlarmEntity(2, "ali", testTime, 60)
        )

        coEvery { repository.getAllAlarms() } returns flowOf(alarmList)


        viewModel.getAllAlarms()
        advanceUntilIdle()
        assertEquals(Response.Success(alarmList), viewModel.allAlarms.value)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAlarmById() = runTest {
        val alarmId = 123
        val remainingAlarms = listOf(
            AlarmEntity(1, "Alarm 1", testTime, 60),
            AlarmEntity(2, "Alarm 2", testTime, 60)
        )

        coEvery { repository.deleteAlarmById(alarmId) } returns Unit
        coEvery { repository.getAllAlarms() } returns flowOf(remainingAlarms)

        viewModel.deleteAlarmById(alarmId)
        advanceUntilIdle()

        coVerify { repository.deleteAlarmById(alarmId) }
        coVerify { repository.getAllAlarms() }

        assertEquals(Response.Success(remainingAlarms), viewModel.allAlarms.value)

    }


}