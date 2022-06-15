package com.onseen.livecare.fragments.main.TripRequests

import android.app.DatePickerDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.data.kml.KmlPlacemark
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.TripRequests.ViewModel.RideViewModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.TripRequest.DataModel.EnumTripRequestTiming
import com.onseen.livecare.models.TripRequest.DataModel.EnumTripRequestWayType
import kotlinx.android.synthetic.main.fragment_rider_details.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.libraries.places.api.Places
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLngBounds
import com.onseen.livecare.models.Utils.*


class RiderDetailsFragment: BaseMainHomeFragment() {

    private var vmRide: RideViewModel? = null

    private var googlePlaceMarkselectedForPickup: KmlPlacemark? = null
    private var googlePlaceMarkSelectedForDropoff: KmlPlacemark? = null
    private var arrayAllTimes: ArrayList<String> = ArrayList()
    private var googlePickupAddress: String? = ""
    private var googleDropoffAddress: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.title_schedule_ride), false)

        initUI()
    }

    private fun initUI() {

        if(this.vmRide == null) return

        val arrayConsumers = ConsumerManager.sharedInstance().arrayConsumers
        val arrayConsumerNames: ArrayList<String> = ArrayList()
        for(consumer in arrayConsumers) {
            val name = consumer.szName
            arrayConsumerNames.add(name)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, arrayConsumerNames)
        spnConsumer.setAdapter(adapter)
        spnConsumer.setOnClickListener {
            spnConsumer.showDropDown()
        }

        var indexConsumer = -1
        if(this.vmRide!!.modelConsumer != null) {
            var i = 0
            for(consumer in arrayConsumerNames) {
                if(consumer == this.vmRide!!.modelConsumer!!.szName) {
                    indexConsumer = i
                    break
                }
                i += 1
            }

            if(indexConsumer != -1)
                spnConsumer.setText(arrayConsumerNames.get(i))
        }

        layoutOneway.setOnClickListener {
            this.vmRide!!.enumWayType = EnumTripRequestWayType.ONE_WAY
            refreshReturnDateTimePanel()
        }

        layoutRoundTrip.setOnClickListener {
            this.vmRide!!.enumWayType = EnumTripRequestWayType.ROUND_TRIP
            refreshReturnDateTimePanel()
        }

        // Pickup / Dropoff Location
        val token = AutocompleteSessionToken.newInstance()
        Places.initialize(activity!!, UtilsConfig.GOOGLE_PLACE_API_KEY)

        val placesClient = Places.createClient(activity!!.applicationContext)
        val boundsBuilder = LatLngBounds(LatLng(38.639645, -84.891110), LatLng(42.318241, -80.499079))
        val bounds = RectangularBounds.newInstance(boundsBuilder)

        txtPickupAddress.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            googlePickupAddress = txtPickupAddress.text.toString()
        }
        txtPickupAddress.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()) return

                vmRide!!.geoPickup.initialize()
                val arrayPickupLocation:ArrayList<String> = ArrayList()

                val request = FindAutocompletePredictionsRequest.builder()
//                    .setLocationBias(bounds)
                    .setLocationRestriction(bounds)
                    .setCountry("US")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()

                placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                    for (prediction in response.autocompletePredictions) {
                        UtilsGeneral.log("Place getFullText=====" + prediction.getFullText(null).toString())
                        arrayPickupLocation.add(prediction.getFullText(null).toString())
                    }
                    if(!googlePickupAddress.equals(s.toString())) {
                        googlePickupAddress = ""
                        val adapterPickup: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayPickupLocation)
                        txtPickupAddress.setAdapter(adapterPickup)
                        txtPickupAddress.showDropDown()
                    }
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        UtilsGeneral.log("Place not found: " + exception.statusCode)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        txtDropoffAddress.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            googleDropoffAddress = txtDropoffAddress.text.toString()
        }
        txtDropoffAddress.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()) return

                val arrayDropoffLocation:ArrayList<String> = ArrayList()
                vmRide!!.geoDropoff.initialize()
                val request = FindAutocompletePredictionsRequest.builder()
//            .setLocationBias(bounds)
                    .setLocationRestriction(bounds)
                    .setCountry("us")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()

                placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                    for (prediction in response.autocompletePredictions) {
                        UtilsGeneral.log("place =====" + prediction.getFullText(null).toString())
                        arrayDropoffLocation.add(prediction.getFullText(null).toString())
                    }

                    if(!googleDropoffAddress.equals(s.toString())) {
                        googleDropoffAddress = ""
                        val adapterDropoff: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayDropoffLocation)
                        txtDropoffAddress.setAdapter(adapterDropoff)
                        txtDropoffAddress.showDropDown()
                    }
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        val apiException = exception as ApiException
                        UtilsGeneral.log("Place not found: " + apiException.statusCode)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        // Timing
        val arrayTiming = arrayListOf(EnumTripRequestTiming.ARRIVE_BY.value, EnumTripRequestTiming.READY_BY.value)
        val adapterTiming: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayTiming)
        spnTiming.setAdapter(adapterTiming)
        spnTiming.setOnClickListener {
            spnTiming.showDropDown()
        }

        // Time & Return Time
        // building all time-slots from "00:00 AM" to "11:30 PM"
        this.arrayAllTimes = ArrayList()

        for(hh in 0..23) {
            for(mm in 0..59 step 30) {
                var hour = hh % 12
                val ampm = if(hh >= 12) "PM" else "AM"
                if(hour == 0) hour = 12
                val title = UtilsString.padLeadingZerosForTwoDigits(hour) + ":" + UtilsString.padLeadingZerosForTwoDigits(mm) + " " + ampm
                this.arrayAllTimes.add(title)
            }
        }

        layoutTBD.setOnClickListener{
            this.vmRide!!.isReturnTbd = !this.vmRide!!.isReturnTbd
            refreshReturnTbdPanel()
        }

        txtDate.setOnClickListener {
            showCalendar(EnumCalendarFor.DATE)
        }

        val adapterTime: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayAllTimes)
        spnTime.setAdapter(adapterTime)
        spnTime.setOnClickListener {
            spnTime.showDropDown()
        }

        txtReturnDate.setOnClickListener {
            showCalendar(EnumCalendarFor.RETURN_DATE)
        }

        val adapterReturnTime: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayAllTimes)
        spnReturnTime.setAdapter(adapterReturnTime)
        spnReturnTime.setOnClickListener {
            spnReturnTime.showDropDown()
        }

        layoutWheelchair.setOnClickListener {
            this.vmRide!!.modelSpecialNeeds.isWheelchair = !this.vmRide!!.modelSpecialNeeds.isWheelchair
            refreshSpecialNeedsPanel()
        }

        layoutWalker.setOnClickListener {
            this.vmRide!!.modelSpecialNeeds.isWalker = !this.vmRide!!.modelSpecialNeeds.isWalker
            refreshSpecialNeedsPanel()
        }

        layoutDeaf.setOnClickListener{
            this.vmRide!!.modelSpecialNeeds.isDeaf = !this.vmRide!!.modelSpecialNeeds.isDeaf
            refreshSpecialNeedsPanel()
        }

        layoutBlind.setOnClickListener {
            this.vmRide!!.modelSpecialNeeds.isBlind = !this.vmRide!!.modelSpecialNeeds.isBlind
            refreshSpecialNeedsPanel()
        }

        layoutServiceAnimal.setOnClickListener {
            this.vmRide!!.modelSpecialNeeds.isServiceAnimal = !this.vmRide!!.modelSpecialNeeds.isServiceAnimal
            refreshSpecialNeedsPanel()
        }

        txtPhone.addTextChangedListener(object: TextWatcher {
            private var backspacingFlag = false
            private var editedFlag = false
            private var cursorComplement = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                cursorComplement = s!!.length - txtPhone.selectionStart
                if (count > after)
                    backspacingFlag = true
                else
                    backspacingFlag = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val string = s.toString()
                var phone = string.replace("[^\\d]".toRegex(), "")
                if(phone.length > 10) {
                    phone = phone.substring(0, 10)
                }

                if (!editedFlag) {

                    if (phone.length >= 6 && !backspacingFlag) {
                        editedFlag = true
                        val ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6)
                        txtPhone.setText(ans)
                        txtPhone.setSelection(txtPhone.getText().length - cursorComplement)

                    } else if (phone.length >= 3 && !backspacingFlag) {
                        editedFlag = true
                        val ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3)
                        txtPhone.setText(ans)
                        txtPhone.setSelection(txtPhone.getText().length - cursorComplement)
                    }
                } else {
                    editedFlag = false
                }
            }
        })

        refreshFields()

    }

    private fun refreshFields() {
        refreshReturnDateTimePanel()
        refreshSpecialNeedsPanel()
    }

    private fun refreshReturnDateTimePanel() {
        if(this.vmRide!!.enumWayType == EnumTripRequestWayType.ONE_WAY) {
            rdoOneWay.setImageResource(R.mipmap.circle_selected_gray)
            rdoRoundTrip.setImageResource(R.mipmap.circle_not_selected_gray)
            layoutReturnDate.visibility = GONE
            layoutReturnTime.visibility = GONE
        } else {
            rdoOneWay.setImageResource(R.mipmap.circle_not_selected_gray)
            rdoRoundTrip.setImageResource(R.mipmap.circle_selected_gray)
            layoutReturnDate.visibility = VISIBLE
            layoutReturnTime.visibility = VISIBLE
        }
        refreshReturnTbdPanel()
    }

    private fun refreshReturnTbdPanel() {
        if(this.vmRide!!.isReturnTbd) {
            chkTBD.setImageResource(R.mipmap.rect_selected_gray)
        } else {
            chkTBD.setImageResource(R.mipmap.rect_not_selected_gray)
        }
    }

    private fun refreshSpecialNeedsPanel() {
        this.chkWheelchair.setImageResource(if(this.vmRide!!.modelSpecialNeeds.isWheelchair) R.mipmap.rect_selected_gray else R.mipmap.rect_not_selected_gray)
        this.chkWalker.setImageResource(if(this.vmRide!!.modelSpecialNeeds.isWalker) R.mipmap.rect_selected_gray else R.mipmap.rect_not_selected_gray)
        this.chkDeaf.setImageResource(if(this.vmRide!!.modelSpecialNeeds.isDeaf) R.mipmap.rect_selected_gray else R.mipmap.rect_not_selected_gray)
        this.chkBlind.setImageResource(if(this.vmRide!!.modelSpecialNeeds.isBlind) R.mipmap.rect_selected_gray else R.mipmap.rect_not_selected_gray)
        this.chkServiceAnimal.setImageResource(if(this.vmRide!!.modelSpecialNeeds.isServiceAnimal) R.mipmap.rect_selected_gray else R.mipmap.rect_not_selected_gray)
    }

    private fun showCalendar(type: EnumCalendarFor) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            if(type == EnumCalendarFor.DATE) {
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                this.vmRide!!.date = calendar.time
                txtDate.text = UtilsDate.getStringFromDateTimeWithFormat(calendar.time, EnumDateTimeFormat.MMddyyyy.value, null)
            } else if(type == EnumCalendarFor.RETURN_DATE){
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                this.vmRide!!.dateReturnDate = calendar.time
                txtReturnDate.text = UtilsDate.getStringFromDateTimeWithFormat(calendar.time, EnumDateTimeFormat.MMddyyyy.value, null)
            }
        }, year, month, day)

        dialog.show()
    }

    // MARK: Biz Logic
    private fun validateFields(): Boolean {
        if(!this.spnConsumer.text.isEmpty()) {
            var isFound = false
            val arrayConsumers = ConsumerManager.sharedInstance().arrayConsumers
            for(consumer in arrayConsumers) {
                if(this.spnConsumer.text.toString().equals(consumer.szName)) {
                    this.vmRide!!.modelConsumer = consumer
                    isFound = true
                    break
                }
            }
            if(!isFound) {
                showToast("Please select consumer.")
                return false
            }
        } else {
            showToast("Please select consumer.")
            return false
        }

        if(this.txtPickupAddress.text.isNotEmpty()) {
            val latlng = getLocationFromAddress(this.txtPickupAddress.text.toString())
            this.vmRide!!.geoPickup.fLatitude = latlng.latitude.toFloat()
            this.vmRide!!.geoPickup.fLongitude = latlng.longitude.toFloat()
            this.vmRide!!.geoPickup.szAddress = this.txtPickupAddress.text.toString()
            if(!this.vmRide!!.geoPickup.isValid()) {
                showToast("Please enter pickup address")
                return false
            }
        } else {
            showToast("Please enter pickup address")
            return false
        }

        if(this.txtDropoffAddress.text.isNotEmpty()) {
            val latlng = getLocationFromAddress(this.txtDropoffAddress.text.toString())
            this.vmRide!!.geoDropoff.fLatitude = latlng.latitude.toFloat()
            this.vmRide!!.geoDropoff.fLongitude = latlng.longitude.toFloat()
            this.vmRide!!.geoDropoff.szAddress = this.txtDropoffAddress.text.toString()
            if(!this.vmRide!!.geoDropoff.isValid()) {
                showToast("Please enter drop-off address")
                return false
            }
        } else {
            showToast("Please enter drop-off address")
            return false
        }

        if(this.spnTiming.text.toString().isEmpty()) {
           showToast("Please select the timing.")
            return false
        } else {
            this.vmRide!!.enumTiming = EnumTripRequestTiming.fromString(this.spnTiming.text.toString())
        }
        if(this.vmRide!!.date == null) {
            showToast("Please select the Date.")
            return false
        }
        if(this.spnTime.text.toString().isEmpty()) {
            showToast("Please select the time.")
            return false
        } else {
            this.vmRide!!.szTime = this.spnTime.text.toString()
        }
        if(this.vmRide!!.enumWayType == EnumTripRequestWayType.ROUND_TRIP) {
            if(this.vmRide!!.dateReturnDate == null) {
                showToast("Please select the return date.")
                return false
            }
            if(this.vmRide!!.dateReturnDate!!.before(this.vmRide!!.date)) {
                showToast("A return trip cannot occur before the pickup trip.")
                return false
            }
            if(this.spnReturnTime.text.toString().isEmpty()) {
                showToast("Please select the return time.")
                return false
            } else {
                this.vmRide!!.szReturnTime = this.spnReturnTime.text.toString()
            }
        }

        this.vmRide!!.szContactName = this.txtContact.text.toString()
        this.vmRide!!.szContactPhone = this.txtPhone.text.toString()

        return true
    }

    private fun getLocationFromAddress(strAddress: String): LatLng {

        val coder : Geocoder = Geocoder(activity)
        val address:List<Address>

        address = coder.getFromLocationName(strAddress,5)
        if (address==null) {
            return LatLng(0.0, 0.0)
        }
        val location : Address = address.get(0)
        return LatLng(location.getLatitude(), location.getLongitude())
    }

    private fun gotoRecurringFragment() {
        hideKeyboard()
        if(this.validateFields()) {
            mainFragment.addFragment(RiderRecurringFragment.newInstance(this.vmRide))
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemNext = menu.findItem(R.id.next)
        itemNext.isVisible = true
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.next) {
            // TODO Next button action
            gotoRecurringFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance(vmModel: RideViewModel?) =
            RiderDetailsFragment().apply {
                arguments = Bundle().apply {
                    vmRide = vmModel
                }
            }
    }
}

enum class EnumCalendarFor(val value:Int) {
    DATE(0),
    RETURN_DATE(1)
}