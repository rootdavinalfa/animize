/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ml.dvnlabs.animize.database.notification.StarredNotification
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class ListViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val starredRoom: StarredNotificationDatabase by inject { parametersOf(application) }

    private var _listNotification = fetchNotification().asLiveData(viewModelScope.coroutineContext)
    val listNotification: LiveData<List<StarredNotification>> = _listNotification


    private fun fetchNotification() = flow {
        while (true) {
            delay(1000)
            val data = starredRoom.starredNotificationDAO().getStarredNotificationList()
            emit(data)
        }
    }
}