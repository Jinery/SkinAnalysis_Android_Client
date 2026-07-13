package com.kychnoo.skinanalysis_android_client.data.model.events

sealed interface NavigationEvent {
    object NavigateToConnectionScreen : NavigationEvent
}