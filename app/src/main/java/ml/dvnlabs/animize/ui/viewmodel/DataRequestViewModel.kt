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

class DataRequestViewModel : ViewModel() {
    companion object {
        private const val CODE_GET_REQUEST = 1024
    }

    //LiveData for DataRequest
    var requestResult = MutableLiveData<ArrayList<RequestResult>>()

    init {
        requestResult.value = ArrayList()
    }

    fun pushRequestResult(ids: Int, result: String) {
        val hasRequestResult = requestResult.value?.singleOrNull {
            it.id == ids
        }
        if (hasRequestResult == null)
            requestResult.value?.add(RequestResult(ids, result))
        else
            removeExistingResult(hasRequestResult)
    }

    private fun removeExistingResult(existing: RequestResult) {
        requestResult.value?.remove(existing)
    }

    data class RequestResult(var id: Int, var result: String)
}