package com.onseen.livecare.fragments.main.TripRequests

import android.os.Bundle
import android.view.*
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Coordination
import com.akexorcist.googledirection.model.Direction
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.models.TripRequest.DataModel.EnumTripRequestTiming
import com.onseen.livecare.models.TripRequest.DataModel.TripRequestDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsConfig.Companion.GOOGLE_DIRECTION_API_KEY
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsGeneral
import kotlinx.android.synthetic.main.fragment_trip_request_details.*

class TripRequestDetailsFragment: BaseMainHomeFragment() {

    private var googleMap: GoogleMap? = null
    private var modelTripRequest: TripRequestDataModel? = null
    private var mPolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_request_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavTitle("Request Details", false)
        refreshFields()
    }

    override fun viewAppeared() {

    }

    private fun refreshFields() {
        reloadMap()

        if(this.modelTripRequest == null) return
        if(modelTripRequest!!.enumTiming == EnumTripRequestTiming.ARRIVE_BY) {
            this.txtPickupTitle.text = "Pick-up"
            this.txtDropoffTitle.text = "Drop-off\n" + UtilsDate.getStringFromDateTimeWithFormat(modelTripRequest!!.dateTime, EnumDateTimeFormat.hhmma.value, null)
        } else {
            this.txtPickupTitle.text = "Pick-up\n" + UtilsDate.getStringFromDateTimeWithFormat(modelTripRequest!!.dateTime, EnumDateTimeFormat.hhmma.value, null)
            this.txtDropoffTitle.text = "Drop-off"
        }

        if(this.modelTripRequest!!.isScheduled() == false) {
            this.txtConsumer.text = this.modelTripRequest!!.refConsumer.szName
            this.txtDriver.text = "N/A"
            this.txtVehicle.text = "N/A"
            this.txtPlateNum.text = "N/A"
            this.txtTransOrg.text = "N/A"
        }
    }

    private fun reloadMap() {
        mapView.onCreate(null)
        mapView.onResume()

        MapsInitializer.initialize(context)
        mapView.getMapAsync {  map ->
            googleMap = map
            googleMap!!.isTrafficEnabled = true
            googleMap!!.setOnMapLoadedCallback(this::addStopPins)
        }
    }

    private fun addStopPins() {
        googleMap!!.clear()

        val markerPickup: MarkerOptions = MarkerOptions()
        val markerDropOff: MarkerOptions = MarkerOptions()

        markerPickup.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin_pink))
        markerDropOff.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_departure))

        markerPickup.position(modelTripRequest!!.geoPickup.getCoord())
        markerDropOff.position(modelTripRequest!!.geoDropoff.getCoord())
        markerPickup.anchor(0.5f, 0.5f)
        markerDropOff.anchor(0.5f, 0.5f)

        googleMap!!.addMarker(markerPickup)
        googleMap!!.addMarker(markerDropOff)

        val builder = LatLngBounds.Builder()
        builder.include(markerPickup.position)
        builder.include(markerDropOff.position)

        val bounds: LatLngBounds = builder.build()
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 40)
        googleMap!!.animateCamera(cameraUpdate)

        GoogleDirection.withServerKey(GOOGLE_DIRECTION_API_KEY)
            .from(markerPickup.position)
            .to(markerDropOff.position)
            .avoid(AvoidType.HIGHWAYS)
            .transitMode(TransportMode.DRIVING)
            .execute( object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                    if(direction!!.isOK) {
                        val route = direction.routeList.get(0)
                        val opts: PolylineOptions = PolylineOptions().addAll(route.legList.get(0).directionPoint).color(R.color.SHARE_LIGHT_BLUE).width(12.0f)
                        val polyline: Polyline = googleMap!!.addPolyline(opts)
                        updateMapLocation(route.bound.northeastCoordination, route.bound.southwestCoordination)

                        if(mPolyline != null)
                            mPolyline!!.remove()
                        mPolyline = polyline

                    } else {
                        UtilsGeneral.log("Getting Direction Failed")
                    }
                }

                override fun onDirectionFailure(t: Throwable?) {

                }
            })
    }

    private fun updateMapLocation(northEast: Coordination, southWest: Coordination) {
        val bounds = LatLngBounds(southWest.coordination, northEast.coordination)
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemNext = menu.findItem(R.id.next)
        itemNext.isVisible = false
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
            showBackButton()
        } else {
            setHasOptionsMenu(false)
            hideBackButton()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(tripRequest: TripRequestDataModel) =
            TripRequestDetailsFragment().apply {
                arguments = Bundle().apply {
                    modelTripRequest = tripRequest
                }
            }
    }

}