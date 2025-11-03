package com.example.nexusnews.domain.usecase

import app.cash.turbine.test
import com.example.nexusnews.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BaseUseCaseTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `invoke should emit success result`() = runTest {
        // Given
        val useCase = TestUseCase(testDispatcher, shouldFail = false)
        
        // When & Then
        useCase("test").test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            assertEquals("test", (result as Result.Success).data)
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke should emit error result on exception`() = runTest {
        // Given
        val useCase = TestUseCase(testDispatcher, shouldFail = true)
        
        // When & Then
        useCase("test").test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception is IOException)
            awaitComplete()
        }
    }
    
    // Test implementation
    private class TestUseCase(
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
        private val shouldFail: Boolean
    ) : BaseUseCase<String, String>(dispatcher) {
        override fun execute(params: String) = flow {
            if (shouldFail) {
                throw IOException("Test exception")
            }
            emit(Result.Success(params))
        }
    }
}

