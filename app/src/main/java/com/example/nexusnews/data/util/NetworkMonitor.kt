package com.example.nexusnews.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors network connectivity state and provides real-time updates.
 * Provides connectivity status and network type information.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Flow emitting network connectivity state.
     * True when network is available and validated, false otherwise.
     * Automatically handles connectivity changes in real-time.
     */
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                networks.add(network)
                trySend(true)
                Timber.d("Network available: $network")
            }

            override fun onLost(network: Network) {
                networks.remove(network)
                trySend(networks.isNotEmpty())
                Timber.d("Network lost: $network. Remaining networks: ${networks.size}")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val validated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                if (hasInternet && validated) {
                    networks.add(network)
                    trySend(true)
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Send initial state
        trySend(isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Checks current network connectivity state synchronously.
     *
     * @return true if network is available and validated, false otherwise
     */
    fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Returns the type of current network connection.
     *
     * @return NetworkType enum representing the connection type
     */
    fun getNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.OTHER
        }
    }
}

/**
 * Enum representing network connection types.
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER,
    NONE
}