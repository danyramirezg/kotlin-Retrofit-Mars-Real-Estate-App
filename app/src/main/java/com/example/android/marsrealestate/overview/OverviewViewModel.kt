/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */

enum class MarsApiStatus {

    LOADING,
    ERROR,
    DONE
}
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Gets Mars real estate property information from the Mars API Retrofit service and updates the
     * [MarsProperty] [List] [LiveData]. The Retrofit service returns a coroutine Deferred, which we
     * await to get the result of the transaction.
     */
    private fun getMarsRealEstateProperties() {

        coroutineScope.launch {
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties()

            try {

                _status.value = MarsApiStatus.LOADING

                val listResult = getPropertiesDeferred.await()
                _status.value = MarsApiStatus.DONE
                _properties.value = listResult

//                if (listResult.size > 0){
//                    _properties.value = listResult
//                }

                //_response.value = "Success: ${listResult.size} Mars properties retrieved"
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }

        }
    }
    // Cancel the coroutine job after the viewModel is finished in on cleared

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    // Using a callback, instead of coroutines
//    MarsApi.retrofitService.getProperties().enqueue(
//    object : Callback, retrofit2.Callback<List<MarsProperty>> {
//        override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
//            _response.value = "Failure: " + t.message
//        }
//
//        override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
//            _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
//        }
//    })
}

