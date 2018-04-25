package com.thirteendollars.singlerent.data.repository

import com.google.android.gms.maps.model.LatLng
import com.thirteendollars.singlerent.data.model.Disposition
import com.thirteendollars.singlerent.data.repository.remote.DispositionService
import com.thirteendollars.singlerent.data.request.StartRentalRequest
import com.thirteendollars.singlerent.data.request.StartReservationRequest
import com.thirteendollars.singlerent.data.response.FinishRentalResponse
import com.thirteendollars.singlerent.injection.application.AppScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 19/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class DispositionRepository @Inject constructor(private val api: DispositionService ) {

    private var currentDisposition: Disposition? = null

    fun setCurrentDisposition(disposition: Disposition?) {
        currentDisposition = disposition
    }

    fun getCurrentDisposition(): Observable<Disposition?> {
        return if(currentDisposition!=null) {
            Observable.just(currentDisposition)
        }
        else {
            api.getCurrentDisposition()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.disposition }
                    .doOnNext({currentDisposition = it})
        }
    }

    fun startReservation(vehicleId: Int, userLocation: LatLng): Observable<Disposition> {
        return api.startReservation( StartReservationRequest(vehicleId, userLocation.latitude, userLocation.longitude) )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.disposition }
                .doOnNext({currentDisposition = it})
    }

    fun cancelReservation(): Completable {
        return api.cancelReservation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete({currentDisposition = null})
    }

    fun startRental(nfcCode: String): Observable<Disposition> {
        return api.startRental( StartRentalRequest(nfcCode) )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.disposition }
                .doOnNext({currentDisposition = it})
    }

    fun finishRental(): Observable<FinishRentalResponse> {
        return api.finishRental()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({currentDisposition = null})
    }


}