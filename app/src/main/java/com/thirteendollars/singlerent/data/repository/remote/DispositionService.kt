package com.thirteendollars.singlerent.data.repository.remote

import com.thirteendollars.singlerent.data.request.StartRentalRequest
import com.thirteendollars.singlerent.data.request.StartReservationRequest
import com.thirteendollars.singlerent.data.response.DispositionResponse
import com.thirteendollars.singlerent.data.response.FinishRentalResponse
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Damian Nowakowski on 20/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
interface DispositionService {

    @GET("/disposition")
    fun getCurrentDisposition(): Observable<DispositionResponse>

    @POST("/disposition/reserve")
    fun startReservation(@Body request: StartReservationRequest): Observable<DispositionResponse>

    @POST("/disposition/cancel")
    fun cancelReservation(): Completable

    @POST("/disposition/rent")
    fun startRental(@Body request: StartRentalRequest): Observable<DispositionResponse>

    @POST("/disposition/finish")
    fun finishRental(): Observable<FinishRentalResponse>
}