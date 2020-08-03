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
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ml.dvnlabs.animize.database.AnimizeDatabase
import ml.dvnlabs.animize.database.RecentPlayed
import ml.dvnlabs.animize.database.notification.StarredNotification
import ml.dvnlabs.animize.database.notification.StarredNotificationDatabase
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class ListViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val starredRoom: StarredNotificationDatabase by inject { parametersOf(application) }
    private val animizeDB: AnimizeDatabase by inject { parametersOf(application) }

    private var _listNotification = fetchNotification().asLiveData(viewModelScope.coroutineContext).distinctUntilChanged()
    val listNotification: LiveData<List<StarredNotification>> = _listNotification


    private fun fetchNotification() = flow {
        while (true) {
            delay(1000)
            val data = starredRoom.starredNotificationDAO().getStarredNotificationList()
            emit(data)
        }
    }

    private var _listRecent = fetchRecent().asLiveData(viewModelScope.coroutineContext).distinctUntilChanged()
    val listRecent: LiveData<List<RecentPlayed>> = _listRecent


    private fun fetchRecent() = flow {
        while (true) {
            delay(1000)
            val data = animizeDB.recentPlayedDAO().getRecentList()
            emit(data)
        }
    }
}