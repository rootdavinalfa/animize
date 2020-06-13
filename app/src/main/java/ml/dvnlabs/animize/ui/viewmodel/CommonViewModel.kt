/*
 * Copyright (c) 2020.
 * Animize Devs
 * Copyright 2019 - 2020
 * Davin Alfarizky Putra Basudewa <dbasudewa@gmail.com>
 * This program used for watching anime without ads.
 *
 */

package ml.dvnlabs.animize.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ml.dvnlabs.animize.database.legacy.model.UserLand

class CommonViewModel : ViewModel() {
    var userLand: UserLand? = null
    var dashboardScrolledToTop : MutableLiveData<Boolean> = MutableLiveData(false)
    var libraryScrolledToTop : MutableLiveData<Boolean> = MutableLiveData(false)
    fun changeDashboardScrolledToTop() {
        dashboardScrolledToTop.value = when (dashboardScrolledToTop.value) {
            false -> true
            true -> false
            else -> false
        }
    }

    fun changeLibraryScrolledToTop() {
        libraryScrolledToTop.value = when (libraryScrolledToTop.value) {
            false -> true
            true -> false
            else -> false
        }
    }

    val selectedPageMainNavigation = MutableLiveData<Int>()
    fun changeSelectedPageMainNavigation(index: Int) {
        selectedPageMainNavigation.value = index
    }
}