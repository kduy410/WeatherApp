package com.example.weatherapp.ui.home

import android.app.Activity.RESULT_OK
import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.weatherapp.Event
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.location.FusedLocationService
import com.example.weatherapp.repo.WeatherRepository
import com.example.weatherapp.repo.source.local.Entities.WeatherResponseEntity
import kotlinx.coroutines.*
import timber.log.Timber

class HomeViewModel constructor(
    application: Application,
    private val defaultRepository: WeatherRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val ioDispatcher = Dispatchers.IO
    private val handlerException = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        Timber.e("Coroutine context: $coroutineContext,Exception: $throwable")
    }
    private val backGroundScope = CoroutineScope(ioDispatcher + handlerException)

    private val fused: FusedLocationService by lazy {
        (application as WeatherApplication).fusedLocationService
    }

    private val workerManager by lazy { WorkManager.getInstance(application) }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var resultMessageShown = false

    private val _dataLoading = MutableLiveData<Boolean>(false)
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isDataLoadingError = MutableLiveData<Boolean>(false)

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val networkError: LiveData<Boolean> = _networkError

    private val _noDataLabel = MutableLiveData<Int>()
    val noDataLabel: LiveData<Int> = _noDataLabel

    private val _noDataIconRes = MutableLiveData<Int>()
    val noDataIconRes: LiveData<Int> = _noDataIconRes

    // At initial state --> data is null
    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weather = MediatorLiveData<WeatherResponse>()

    private val URL_ICON = "http://openweathermap.org/img/wn/10d@2x.png"

    private val _iconUrl = _weatherResponse.switchMap {
        val url = MutableLiveData<String>(URL_ICON)
        if (it != null) {
            url.value = "http://openweathermap.org/img/wn/${it.weather?.last()?.icon}@2x.png"
        }
        return@switchMap url
    }

    val iconUrl = _iconUrl

    val isCurrentUTC = MutableLiveData<Boolean>(true)
    fun changeCurrentUTC() {
        /**
         * if(isCurrentUTC.value == true)
         *      isCurrentUTC.value = false
         * else
         *      isCurrentUTC.value = true
         *
         * cannot set [isCurrentUTC.value = !isCurrentUTC.value] because we don't know whether it's [null] or not
         */
        isCurrentUTC.value = isCurrentUTC.value != true
    }

    // The first time it will be null
    private val _savedID = savedStateHandle.getLiveData<Int>(WEATHER_ID_SAVED_STATE_KEY)

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    // If it has no observer -> it won't run
    private val update =
        _forceUpdate.switchMap { _forceUpdate ->
            _dataLoading.value = true
            if (_forceUpdate) {
                viewModelScope.launch(ioDispatcher + handlerException) {
                    // When running the first time -> update -> setWeatherResponse
                    if (_savedID.value == null || _savedID.value == 0) {
                        defaultRepository.updateWeathers()
                    } else {
                        // When running after the first time && savedID have been set
                        defaultRepository.updateWeatherById(_savedID.value!!)
                    }
                    _dataLoading.postValue(false)
                }
            }

            defaultRepository.observeWeathers().distinctUntilChanged().switchMap {
                getLatestResponse()
            }
        }

    val empty: LiveData<Boolean> = update.map {
        try {
            setData(it)
            it?.id == null
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    private fun setData(response: WeatherResponse?) {
        _weatherResponse.value = response
        _savedID.value = response?.id
    }

    private fun getLatestResponse(): LiveData<WeatherResponse?> {
        val result = MutableLiveData<WeatherResponse?>()
        runBlocking(ioDispatcher + handlerException) {
            try {
                val latest = defaultRepository.getLastResponseId()
                if (latest != null) {
                    val weather = defaultRepository.getWeatherById(latest)
                    if (weather != null) {
                        Timber.e("LATEST: [${weather.name}]")
                        result.postValue(weather)
                    } else {
                        result.postValue(null)
                        showSnackbarMessageBackground(R.string.data_not_found)
                    }
                } else {
                    result.postValue(null)
                    showSnackbarMessageBackground(R.string.unable_to_fetch_latest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result.postValue(null)
                _isDataLoadingError.postValue(true)
                showSnackbarMessageBackground(R.string.loading_data_error)
            }
        }
        return result
    }

    init {
        setResources(R.string.no_data_label, R.drawable.no_data)
        weather.addSource(_weatherResponse) {
            if (it != null) {
                Timber.e("onChanged: ${it.name}")
                weather.value = it
            }
        }
    }

    private fun saveID(id: Int?) {
        if (id != null || id != 0) {
            savedStateHandle[WEATHER_ID_SAVED_STATE_KEY] = id
            Timber.e(
                "SAVED ID: $id ,CONTAINED: ${
                    savedStateHandle.contains(
                        WEATHER_ID_SAVED_STATE_KEY
                    )
                }"
            )
        }
    }

    fun savedInstanceState() {
        saveID(_savedID.value)
    }

    private fun setResources(
        @StringRes noDataLabelString: Int,
        @DrawableRes noDataIconDrawable: Int,
    ) {
        _noDataLabel.value = noDataLabelString
        _noDataIconRes.value = noDataIconDrawable
    }

    /**
     * Get current location's Weather data and save it to database
     */
    fun fetchData() {
        // Update current location's weather
        viewModelScope.launch(ioDispatcher + handlerException) {
            try {
                defaultRepository.updateWeathers { response, throwable ->
                    viewModelScope.launch(ioDispatcher + handlerException) {
                        handle(response, throwable)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbarMessage(R.string.loading_data_error)
            }
        }
    }

    fun fetchDataByName(cityName: String) {
        // Update current location's weather
        _dataLoading.value = true
        viewModelScope.launch(ioDispatcher + handlerException) {
            try {
                defaultRepository.updateWeatherByName(cityName) { response, throwable ->
                    if (response == null) {
                        showSnackbarMessageBackground(R.string.not_found)
                    } else {
                        viewModelScope.launch(ioDispatcher + handlerException) {
                            handle(response, throwable)
                        }
                    }
                }
                _dataLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _dataLoading.postValue(false)
                showSnackbarMessage(R.string.not_found)
            }
        }
    }

    fun fetchDataByID(id: Int) {
        viewModelScope.launch(ioDispatcher + handlerException) {
            Timber.e("FETCH WEATHER BY ID")
            defaultRepository.updateWeatherById(id) { response, throwable ->
                viewModelScope.launch(ioDispatcher + handlerException) {
                    handle(response, throwable)
                }
            }
        }
    }

    fun fetchLatestDataID(): LiveData<WeatherResponse?> {
        val latest = MutableLiveData<WeatherResponse?>()
        viewModelScope.launch(ioDispatcher + handlerException) {
            try {
                val lastResponse = defaultRepository.getLastResponseId()
                if (lastResponse != null) {
                    val weather = defaultRepository.getWeatherById(lastResponse)
                    if (weather != null) {
                        Timber.e("LATEST: ID[$latest]")
                        Timber.e("WEATHER: [${weather.name}]")
                        latest.postValue(weather)
                    } else {
                        latest.postValue(null)
                        showSnackbarMessageBackground(R.string.data_not_found)
                    }
                } else {
                    latest.postValue(null)
                    showSnackbarMessageBackground(R.string.unable_to_fetch_latest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbarMessageBackground(R.string.loading_data_error)
            }
        }
        return latest
    }

    fun refresh(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        refresh(true)
    }

    private fun showSnackbarMessage(result: Int) {
        _snackbarText.value = Event(result)
    }

    private fun showSnackbarMessageBackground(result: Int) {
        _snackbarText.postValue(Event(result))
    }

    fun showResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            RESULT_OK -> showSnackbarMessage(R.string.result_ok)
        }
        resultMessageShown = true
    }

    private suspend fun convertEntityToPOJO(entity: WeatherResponseEntity?): WeatherResponse? =
        viewModelScope.async {
            try {
                defaultRepository.convertEntityToPOJO(entity)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }.await()

    private suspend fun handle(response: WeatherResponse?, throwable: Throwable?) {
        withContext(ioDispatcher + handlerException) {
            try {
                if (response == null && throwable is NullPointerException) {
                    _isDataLoadingError.postValue(true)
                    showSnackbarMessageBackground(R.string.location_null)
                }
                if (response != null) {
                    _weatherResponse.postValue(response)
                    _savedID.postValue(response.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isDataLoadingError.postValue(true)
            }
        }
    }

    fun delete() {
        weather.removeSource(_weatherResponse)
        viewModelScope.launch(ioDispatcher + handlerException) {
            try {
                defaultRepository.deleteAll()
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbarMessage(R.string.delete_data_error)
            }
        }
        refresh(true)
        weather.addSource(_weatherResponse) {
            if (it != null) {
                Timber.e("onChanged: ${it.id}")
                it.let {
                    weather.value = it
                }
            }
        }
        showSnackbarMessage(R.string.delete_status)
    }

    fun convertEpochToDate(epoch: Long?): String {
        return com.example.weatherapp.extension.toDate(epoch)
    }

    fun convertEpochToDate(epoch: Long?, timeZone: Long?): String {
        return com.example.weatherapp.extension.toDateWithTimeZone(epoch, timeZone)
    }

    fun convertEpochToSunrise(epoch: Long?): String {
        return com.example.weatherapp.extension.toSunrise(epoch)
    }

    fun convertEpochToSunrise(epoch: Long?, timeZone: Long?): String {
        return com.example.weatherapp.extension.toSunriseWithTimeZone(epoch, timeZone)
    }
}


const val WEATHER_ID_SAVED_STATE_KEY = "WEATHER_ID_SAVED_STATE_KEY"
