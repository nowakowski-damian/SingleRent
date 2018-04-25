package com.thirteendollars.singlerent.main.map

import android.databinding.ObservableField
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.thirteendollars.singlerent.Utils
import com.thirteendollars.singlerent.bus.EventBus
import com.thirteendollars.singlerent.bus.Events
import com.thirteendollars.singlerent.data.model.Disposition
import com.thirteendollars.singlerent.data.model.Vehicle
import com.thirteendollars.singlerent.data.model.Zone
import com.thirteendollars.singlerent.data.repository.DispositionRepository
import com.thirteendollars.singlerent.data.repository.PoiRepository
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.application.FragmentScope
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 13/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@FragmentScope
class MapViewModel @Inject constructor(
        private val poiRepository: PoiRepository,
        private val dispositionRepository: DispositionRepository,
        private val locationManager: LocationManager,
        private val timer: Timer,
        private val bus: EventBus,
        private val errorHandler: ErrorHandler
) {

    private var disposition: Disposition? = null
    private var timerDisposable: Disposable? = null
    private var nfcDisposable: Disposable? = null

    val loadingInProgress = PublishSubject.create<Boolean>()
    val events = PublishSubject.create<MapEvent>()
    val vehicles = PublishSubject.create<MutableList<Vehicle>>()
    val parkingZones = PublishSubject.create<MutableList<Zone>>()

    var vehicleBottomSheetHidden = ObservableField(true)


    //    Vehicle bottom sheet data
    var selectedVehicle = ObservableField<Vehicle>()
    var distanceToVehicle = ObservableField<Double>()  // km
    var walkTimeToVehicle = ObservableField<Int>() // min
    var rentalTime = ObservableField<Long>() // min
    var rentalCost = ObservableField<Double>() // zł
    var mapMode = ObservableField<MapMode>(MapMode.PREVIEW) // zł


    fun mapInitialized() {
        dispositionRepository.getCurrentDisposition()
                .subscribe(
                        {
                            updateCurrentDisposition(it)
                            setMapModeFor(it)
                            selectedVehicle.set(it?.vehicle)
                            loadMapComponents(mapMode.get())
                            if (it?.type == Disposition.Type.RENTAL) {
                                startRentalTimer(it.startDate)
                            } else {
                                stopRentalTimer()
                            }
                        },
                        {
                            it.printStackTrace()
                            val error = errorHandler.getError(it)
                            events.onNext(MapEvent.HttpError(error))
                            setMapModeFor(null)
                            loadMapComponents(mapMode.get())
                        }
                )
    }

    private fun startRentalTimer(startDate: String) {
        val date = Utils.dateFromString(startDate)
        timerDisposable = timer.start(date).subscribe {
            rentalTime.set(it)
            disposition?.costPerMinute?.apply {
                rentalCost.set(this * TimeUnit.SECONDS.toMinutes(it))
            }
        }
    }

    private fun stopRentalTimer() {
        timer.stop()
        timerDisposable?.dispose()
        rentalTime.set(null)
        rentalCost.set(null)
    }

    fun loadMapComponents(mapMode: MapMode?) {
        when (mapMode) {
            MapMode.PREVIEW -> fetchVehicles()
            MapMode.RESERVATION -> loadSelectedVehicle(disposition!!)
            else -> {
            }
        }
        fetchParkingZones()
    }

    fun setMapModeFor(disposition: Disposition?) {
        val mode = if (disposition == null) {
            MapMode.PREVIEW
        } else if (disposition.type == Disposition.Type.RESERVATION) {
            MapMode.RESERVATION
        } else {
            MapMode.RENTAL
        }
        mapMode.set(mode)
    }

    fun getUserLocation(): LatLng? {
        val location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        location?.apply {
            return LatLng(latitude, longitude)
        }
        return null
    }


    fun fetchVehicles() {
        poiRepository.getVehicles()
                .subscribe(
                        { vehicles.onNext(it) },
                        {
                            it.printStackTrace()
                            val error = errorHandler.getError(it)
                            events.onNext(MapEvent.HttpError(error))
                        }
                )
    }

    fun loadSelectedVehicle(disposition: Disposition?) {
        vehicles.onNext(mutableListOf(disposition!!.vehicle))
    }

    fun fetchParkingZones() {
        poiRepository.getParkingZones()
                .subscribe(
                        { parkingZones.onNext(it) },
                        {
                            it.printStackTrace()
                            val error = errorHandler.getError(it)
                            events.onNext(MapEvent.HttpError(error))
                        }
                )
    }


    fun onVehicleBottomSheetShowHide() {
        events.onNext(MapEvent.ChangeVehicleBottomSheetVisibility(vehicleBottomSheetHidden.get()!!))
    }

    fun startNfcTagListening(onTagFound: ((tag: String) -> Any?)) {
        nfcDisposable = bus.pipe.subscribe { action ->
            when (action) {
                is Events.NfcCollected -> onTagFound(action.nfc)
            }
        }
    }


    fun stopNfcTagListening() {
        nfcDisposable?.dispose()
    }


    fun onVehicleBottomSheetMainButton() {
        when (mapMode.get()) {
            MapMode.PREVIEW -> {
                val userLocation = getUserLocation()
                if (userLocation == null) {
                    events.onNext(MapEvent.NoLocationError())
                    return
                }
                loadingInProgress.onNext(true)
                poiRepository.getNearestVehicle(userLocation)
                        .subscribe(
                                {
                                    events.onNext(MapEvent.SelectMarkerOnMap(it))
                                    onMarkerClickAction(it)
                                },
                                {
                                    it.printStackTrace()
                                    val error = errorHandler.getError(it)
                                    events.onNext(MapEvent.HttpError(error))
                                    loadingInProgress.onNext(false)
                                },
                                { loadingInProgress.onNext(false) }
                        )
            }

            MapMode.PREVIEW_MARKER_CLICKED -> {
                val userLocation = getUserLocation()
                if (userLocation == null) {
                    events.onNext(MapEvent.NoLocationError())
                    return
                }
                loadingInProgress.onNext(true)
                dispositionRepository.startReservation(selectedVehicle.get()!!.id, userLocation)
                        .subscribe(
                                {
                                    updateCurrentDisposition(it)
                                    setMapModeFor(it)
                                    events.onNext(MapEvent.ClearVehicles())
                                    loadSelectedVehicle(it)
                                },
                                {
                                    it.printStackTrace()
                                    val error = errorHandler.getError(it)
                                    events.onNext(MapEvent.HttpError(error))
                                    loadingInProgress.onNext(false)
                                },
                                { loadingInProgress.onNext(false) }
                        )
            }

            MapMode.RESERVATION -> {
                events.onNext(MapEvent.ShowNfcReadingDialog())
                startNfcTagListening {
                    stopNfcTagListening()
                    events.onNext(MapEvent.HideNfcReadingDialog())
                    loadingInProgress.onNext(true)
                    dispositionRepository.startRental(it)
                            .subscribe(
                                    {
                                        updateCurrentDisposition(it)
                                        setMapModeFor(it)
                                        events.onNext(MapEvent.ClearVehicles())
                                        startRentalTimer(it.startDate)
                                    },
                                    {
                                        it.printStackTrace()
                                        val error = errorHandler.getError(it)
                                        events.onNext(MapEvent.HttpError(error))
                                        loadingInProgress.onNext(false)
                                    },
                                    { loadingInProgress.onNext(false) }
                            )
                }
            }

            MapMode.RENTAL -> {
                loadingInProgress.onNext(true)
                dispositionRepository.finishRental()
                        .subscribe(
                                {
                                    updateCurrentDisposition(null)
                                    setMapModeFor(null)
                                    selectedVehicle.set(null)
                                    fetchVehicles()
                                    loadingInProgress.onNext(false)
                                    stopRentalTimer()
                                    events.onNext(MapEvent.ShowSummaryDialog(it.costSummary, it.timeSummary, it.kmSummary))
                                },
                                {
                                    it.printStackTrace()
                                    val error = errorHandler.getError(it)
                                    events.onNext(MapEvent.HttpError(error))
                                    loadingInProgress.onNext(false)
                                },
                                { loadingInProgress.onNext(false) }
                        )
            }
        }
    }

    private fun updateCurrentDisposition(newDisposition: Disposition?) {
        disposition = newDisposition
        distanceToVehicle.set(newDisposition?.distanceToVehicle)
        walkTimeToVehicle.set(newDisposition?.walkTimeToVehicle)
    }

    fun onVehicleBottomSheetCancelButton() {
        loadingInProgress.onNext(true)
        dispositionRepository.cancelReservation()
                .subscribe(
                        {
                            updateCurrentDisposition(null)
                            setMapModeFor(null)
                            selectedVehicle.set(null)
                            events.onNext(MapEvent.ClearVehicles())
                            fetchVehicles()
                            loadingInProgress.onNext(false)
                        },
                        {
                            it.printStackTrace()
                            val error = errorHandler.getError(it)
                            events.onNext(MapEvent.HttpError(error))
                            loadingInProgress.onNext(false)
                        }
                )
    }

    fun onMarkerClickAction(vehicle: Vehicle?) {
        if (disposition == null) {
            selectedVehicle.set(vehicle)
            val mode = if (vehicle != null) MapMode.PREVIEW_MARKER_CLICKED else MapMode.PREVIEW
            mapMode.set(mode)
        }
    }


    sealed class MapEvent {
        data class HttpError(val error: ErrorHandler.Error) : MapEvent()
        data class ChangeVehicleBottomSheetVisibility(val shown: Boolean) : MapEvent()
        class ClearVehicles : MapEvent()
        data class ShowSummaryDialog(val costSummary: Double, val timeSummary: Int, val kmSummary: Int) : MapEvent()
        class ShowNfcReadingDialog : MapEvent()
        class HideNfcReadingDialog : MapEvent()
        data class SelectMarkerOnMap(val vehicle: Vehicle) : MapEvent()
        class NoLocationError : MapEvent()
    }

    enum class MapMode {
        PREVIEW,
        PREVIEW_MARKER_CLICKED,
        RESERVATION,
        RENTAL
    }

}