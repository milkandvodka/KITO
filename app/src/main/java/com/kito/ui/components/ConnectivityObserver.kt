package com.kito.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Qualifier
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

@Singleton
class ConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope private val appScope: CoroutineScope
) {

    private val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    val isOnline: StateFlow<Boolean> =
        callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }

                override fun onUnavailable() {
                    trySend(false)
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            cm.registerNetworkCallback(request, callback)

            // Emit initial state
            val activeNetwork = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(activeNetwork)
            trySend(
                caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            )

            awaitClose {
                cm.unregisterNetworkCallback(callback)
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = true
            )
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope