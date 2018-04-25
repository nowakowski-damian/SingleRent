package com.thirteendollars.singlerent.main.map


import android.app.AlertDialog
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseFragment
import com.thirteendollars.singlerent.data.model.Vehicle
import com.thirteendollars.singlerent.data.model.Zone
import com.thirteendollars.singlerent.databinding.FragmentMapBinding
import com.thirteendollars.singlerent.injection.application.FragmentComponent
import com.thirteendollars.singlerent.main.MainActivity
import com.thirteendollars.singlerent.main.MarkerFabric
import com.thirteendollars.singlerent.main.map.MapViewModel.MapEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MapFragment : BaseFragment<FragmentMapBinding>() {

    @Inject
    lateinit var viewModel: MapViewModel

    @Inject
    lateinit var markerFabric: MarkerFabric

    private var googleMap: GoogleMap? = null
    private var vehicleBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private var vehicleMarkers: MutableList<Marker> = mutableListOf()
    private var polygons: MutableList<Polygon> = mutableListOf()

    private var nfcDialog: AlertDialog? = null

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun provideLayout(): Int = R.layout.fragment_map

    override fun bindData(binding: FragmentMapBinding) {
        binding.mapViewModel = viewModel
        vehicleBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetIncluded!!.vehicleInfoBottomSheet)
    }

    override fun subscribeViewModel(): CompositeDisposable? {
        val loadingDisposable = viewModel
                .loadingInProgress
                .subscribe(this@MapFragment::showLoadingOverlay)
        val eventDisposable = viewModel
                .events
                .subscribe(this@MapFragment::handleEvents)
        val vehicleDisposable = viewModel
                .vehicles
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{ clearVehicleMarkers() }
                .flatMapIterable { it }
                .subscribe{ addVehicleMarker(it) }
        val parkingZoneDisposable = viewModel
                .parkingZones
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{ clearParkingZones() }
                .flatMapIterable { it }
                .subscribe(this@MapFragment::addParkingZone)

        return CompositeDisposable(loadingDisposable,eventDisposable, vehicleDisposable, parkingZoneDisposable)
    }

    override fun onStart() {
        super.onStart()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync{
            googleMap = it.apply {
                isMyLocationEnabled = true
                uiSettings.isMapToolbarEnabled = false
                uiSettings.isRotateGesturesEnabled = false
                uiSettings.isCompassEnabled = false
                setOnMarkerClickListener {
                    viewModel.onMarkerClickAction(it.tag as Vehicle)
                    false
                }
                setOnMapClickListener {
                    viewModel.onMarkerClickAction(null)
                }
            }
            val coordinate = viewModel.getUserLocation()
            if(coordinate==null){
                showToast(getString(R.string.location_not_available))
            }
            else {
                zoomToPosition(coordinate, 13f)
            }
            viewModel.mapInitialized()
        }
        vehicleBottomSheetBehavior?.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if( newState==BottomSheetBehavior.STATE_EXPANDED ) {
                    viewModel.vehicleBottomSheetHidden.set(false)
                }
                else {
                    viewModel.vehicleBottomSheetHidden.set(true)
                }
            }
        })
    }



    private fun showLoadingOverlay(show: Boolean) {
        (activity as? MainActivity)?.showLoadingOverlay(show)
    }


    private fun handleEvents(event: MapEvent) {
        when(event) {
            is MapEvent.HttpError -> {
                showToast(event.error.message)
                if(event.error.is401) (activity as MainActivity).showLoginActivity()
            }
            is MapEvent.ClearVehicles -> clearVehicleMarkers()
            is MapEvent.ChangeVehicleBottomSheetVisibility -> vehicleBottomSheetBehavior?.state =
                    if(event.shown) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
            is MapEvent.ShowSummaryDialog -> showSummaryDialog(event.costSummary, event.kmSummary, event.timeSummary)
            is MapEvent.SelectMarkerOnMap -> selectMarker(event.vehicle)
            is MapEvent.NoLocationError -> showToast(getString(R.string.location_not_available))
            is MapEvent.ShowNfcReadingDialog -> showNfcReadingDialog()
            is MapEvent.HideNfcReadingDialog -> hideNfcReadingDialog()
        }
    }

    private fun showNfcReadingDialog() {
        nfcDialog = AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_nfc)
                .setTitle(getString(R.string.scanning))
                .setMessage(getString(R.string.scanning_info))
                .setNegativeButton(R.string.cancel,{ dialog, _ -> run {
                    viewModel.stopNfcTagListening()
                    dialog.dismiss()
                    nfcDialog = null
                } })
                .setCancelable(false)
                .create()
        nfcDialog?.show()
    }

    private fun hideNfcReadingDialog() {
        nfcDialog?.dismiss()
    }

    private fun showSummaryDialog(costSummary: Double, kmSummary: Int, timeSummary: Int) {
        val costDescription = getString(R.string.summary_cost_description)
        val currency = getString(R.string.zl)
        val distanceDescription = getString(R.string.summary_distance_description)
        val distanceUnit = getString(R.string.km)
        val timeDescription = getString(R.string.summary_time_description)
        val timeUnit = getString(R.string.min)
        AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_cash)
                .setTitle(getString(R.string.rental_summary))
                .setMessage("${costDescription} ${String.format("%.2f",costSummary)} ${currency}\n${distanceDescription} ${kmSummary} ${distanceUnit}\n${timeDescription} ${timeSummary} ${timeUnit}")
                .setPositiveButton(getString(R.string.ok),{ dialog, _ -> dialog.dismiss() })
                .setCancelable(false)
                .create()
                .show()
    }

    private fun clearVehicleMarkers() {
        vehicleMarkers.forEach{ it.remove() }
    }

    private fun addVehicleMarker(vehicle: Vehicle): Marker? {
        val markerOptions = markerFabric.createVehicle(vehicle)
        return googleMap?.addMarker(markerOptions)?.apply {
            tag = vehicle
            vehicleMarkers.add(this)
        }
    }

    private fun clearParkingZones() {
        polygons.forEach{ it.remove() }
    }

    private fun zoomToPosition(position: LatLng, zoom: Float) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom)
        googleMap?.animateCamera(cameraUpdate)
    }

    private fun selectMarker(vehicle: Vehicle) {
        val foundMarkers = vehicleMarkers.filter { (it.tag as? Vehicle)?.id==vehicle.id }

        if( foundMarkers.isEmpty() ) {
            val newMarker = addVehicleMarker(vehicle)
            newMarker?.apply {
                showInfoWindow()
                zoomToPosition(this.position,15f )
            }
        }
        else {
            foundMarkers.first().apply {
                showInfoWindow()
                zoomToPosition(this.position,15f )
            }
        }
    }

    private fun addParkingZone(zone: Zone) {
        zone.coordinates.polygons().forEach( {
            it.lineStrings().forEach( {
                val points = it.points()
                        .map { LatLng(it.lat(),it.lon()) }
                        .toMutableList()
                val polygonOptions = markerFabric
                        .createZonePolygon(zone.name, points,context)
                googleMap?.addPolygon(polygonOptions)?.apply { polygons.add(this) }
            })
        }
        )
    }
}
