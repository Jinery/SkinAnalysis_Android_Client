package com.kychnoo.skinanalysis_android_client.data.manager.snackbar

import com.kychnoo.skinanalysis_android_client.data.model.states.snackbar.SnackbarState
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class SnackbarManagerImpl @Inject constructor() : SnackbarManager {
    private val _snackbarEvents: Channel<SnackbarState> = Channel(Channel.BUFFERED)
    override val snackbarEvents: Flow<SnackbarState> = _snackbarEvents.receiveAsFlow()

    override suspend fun showSnackbar(
        message: String,
        type: SnackbarType
    ) {
        _snackbarEvents.send(SnackbarState(message, type))
    }
}