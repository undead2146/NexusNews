package com.example.nexusnews.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkMonitorTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        network = mockk(relaxed = true)
        networkCapabilities = mockk(relaxed = true)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @Test
    fun `isCurrentlyConnected returns true when network is available and validated`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } returns true

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.isCurrentlyConnected()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isCurrentlyConnected returns false when network is null`() {
        // Given
        every { connectivityManager.activeNetwork } returns null
        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.isCurrentlyConnected()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isCurrentlyConnected returns false when capabilities are null`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null
        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.isCurrentlyConnected()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isCurrentlyConnected returns false when internet capability is missing`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns false
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } returns true

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.isCurrentlyConnected()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isCurrentlyConnected returns false when validation capability is missing`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } returns false

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.isCurrentlyConnected()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getNetworkType returns WIFI when connected via WiFi`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } returns true

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.WIFI, result)
    }

    @Test
    fun `getNetworkType returns CELLULAR when connected via cellular`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } returns false
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } returns true

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.CELLULAR, result)
    }

    @Test
    fun `getNetworkType returns ETHERNET when connected via ethernet`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } returns false
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } returns false
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } returns true

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.ETHERNET, result)
    }

    @Test
    fun `getNetworkType returns OTHER for unknown transport type`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } returns false
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } returns false
        every {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } returns false

        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.OTHER, result)
    }

    @Test
    fun `getNetworkType returns NONE when no network available`() {
        // Given
        every { connectivityManager.activeNetwork } returns null
        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.NONE, result)
    }

    @Test
    fun `getNetworkType returns NONE when capabilities are null`() {
        // Given
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null
        val networkMonitor = NetworkMonitor(context)

        // When
        val result = networkMonitor.getNetworkType()

        // Then
        assertEquals(NetworkType.NONE, result)
    }
}