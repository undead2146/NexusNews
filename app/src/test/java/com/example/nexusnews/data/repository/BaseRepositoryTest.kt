package com.example.nexusnews.data.repository

import app.cash.turbine.test
import com.example.nexusnews.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BaseRepositoryTest {
    private val repository =
        object : BaseRepository() {
            fun testNetworkBoundResource(
                localData: String?,
                remoteData: String,
                shouldFail: Boolean = false,
            ): Flow<Result<String>> =
                networkBoundResource(
                    fetchFromLocal = { localData },
                    fetchFromRemote = {
                        if (shouldFail) throw IOException("Network error")
                        remoteData
                    },
                    saveRemoteData = { },
                )
        }

    @Test
    fun `networkBoundResource emits loading then success`() =
        runTest {
            // When
            repository
                .testNetworkBoundResource(
                    localData = "cached",
                    remoteData = "fresh",
                ).test {
                    // Then
                    assertTrue(awaitItem() is Result.Loading)
                    assertEquals("cached", (awaitItem() as Result.Success).data)
                    assertEquals("fresh", (awaitItem() as Result.Success).data)
                    awaitComplete()
                }
        }

    @Test
    fun `networkBoundResource emits error when network fails and no cache`() =
        runTest {
            // When
            repository
                .testNetworkBoundResource(
                    localData = null,
                    remoteData = "fresh",
                    shouldFail = true,
                ).test {
                    // Then
                    assertTrue(awaitItem() is Result.Loading)
                    assertTrue(awaitItem() is Result.Error)
                    awaitComplete()
                }
        }

    @Test
    fun `networkBoundResource uses cache when network fails`() =
        runTest {
            // When
            repository
                .testNetworkBoundResource(
                    localData = "cached",
                    remoteData = "fresh",
                    shouldFail = true,
                ).test {
                    // Then
                    assertTrue(awaitItem() is Result.Loading)
                    assertEquals("cached", (awaitItem() as Result.Success).data)
                    awaitComplete()
                }
        }
}
