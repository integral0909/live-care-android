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
import com.onseen.livecare.fragments.main.TripRequests.ViewModel.RideViewModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.TripRequest.TripRequestManager
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsConfig.Companion.GOOGLE_DIRECTION_API_KEY
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsGeneral
import kotlinx.android.synthetic.main.fragment_ride_summary.*
import kotlinx.android.synthetic.main.fragment_trip_request_details.*
import kotlinx.android.synthetic.main.fragment_trip_request_details.mapView
import kotlinx.android.synthetic.main.fragment_trip_request_details.txtConsumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RideSummaryFragment: BaseMainHomeFragment() {

    private var googleMap: GoogleMap? = null
    private var vmRide: RideViewModel? = null
    private var mPolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavTitle("Ride Summary", false)
        refreshFields()
    }

    override fun viewAppeared() {

    }

    private fun refreshFields() {
        reloadMap()

        if(this.vmRide == null) return

        this.txtConsumer.text = this.vmRide!!.modelConsumer!!.szName
        this.txtType.text = this.vmRide!!.enumWayType.value
        this.txtPickupAddress.text = this.vmRide!!.geoPickup.szAddress
        this.txtDropoffAddress.text = this.vmRide!!.geoDropoff.szAddress
        this.txtDate.text = UtilsDate.getStringFromDateTimeWithFormat(this.vmRide!!.date, EnumDateTimeFormat.MMMMdd.value, null)
        this.labelTiming.text = this.vmRide!!.enumTiming.value
        this.txtTime.text = this.vmRide!!.szTime

        if(this.vmRide!!.isRecurring) {
            this.txtRecurring.text = UtilsDate.getStringFromDateTimeWithFormat(this.vmRide!!.dateRepeatUntil, EnumDateTimeFormat.MMMMdd.value, null)
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

        markerPickup.position(this.vmRide!!.geoPickup.getCoord())
        markerDropOff.position(this.vmRide!!.geoDropoff.getCoord())
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

    private fun requestCreateTripRequest() {
        if(this.vmRide == null || this.vmRide!!.modelConsumer == null) return

        val tripRequest = this.vmRide!!.toDataModel()
        showProgressHUD()
        TripRequestManager.sharedInstance().requestCreateTripRequestForConsumer(this.vmRide!!.modelConsumer!!, tripRequest, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        gotoListFragment()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun gotoListFragment() {
        mainFragment.removeAllFragments()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.submit, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemSubmit = menu.findItem(R.id.submit)
        itemSubmit.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.submit) {
            requestCreateTripRequest()
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
        fun newInstance(modelRide: RideViewModel) =
            RideSummaryFragment().apply {
                arguments = Bundle().apply {
                    vmRide = modelRide
                }
            }
    }

}