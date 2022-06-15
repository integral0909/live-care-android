package com.onseen.livecare.fragments.main.TripRequests

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.TripRequests.ViewModel.RideViewModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate
import kotlinx.android.synthetic.main.fragment_rider_recurring_trip.*
import java.util.*

class RiderRecurringFragment: BaseMainHomeFragment() {

    public var vmRide: RideViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rider_recurring_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.title_recurring_ride), true)

        initUI()
        refreshFields()
    }

    private fun initUI() {

        layoutYes.setOnClickListener {
            this.vmRide!!.isRecurring = true
            refreshFields()
        }

        layoutNo.setOnClickListener {
            this.vmRide!!.isRecurring = false
            refreshFields()
        }

        layoutSunday.setOnClickListener {
            this.vmRide!!.flagWeekdays[0] = !this.vmRide!!.flagWeekdays[0]
            refreshFields()
        }

        layoutMonday.setOnClickListener {
            this.vmRide!!.flagWeekdays[1] = !this.vmRide!!.flagWeekdays[1]
            refreshFields()
        }

        layoutTuesday.setOnClickListener {
            this.vmRide!!.flagWeekdays[2] = !this.vmRide!!.flagWeekdays[2]
            refreshFields()
        }

        layoutWednesday.setOnClickListener {
            this.vmRide!!.flagWeekdays[3] = !this.vmRide!!.flagWeekdays[3]
            refreshFields()
        }

        layoutThursday.setOnClickListener {
            this.vmRide!!.flagWeekdays[4] = !this.vmRide!!.flagWeekdays[4]
            refreshFields()
        }

        layoutFriday.setOnClickListener {
            this.vmRide!!.flagWeekdays[5] = !this.vmRide!!.flagWeekdays[5]
            refreshFields()
        }

        layoutSaturday.setOnClickListener {
            this.vmRide!!.flagWeekdays[6] = !this.vmRide!!.flagWeekdays[6]
            refreshFields()
        }

        txtRepeatDate.setOnClickListener {
            showDatePopupDialog()
        }
    }

    private fun refreshFields() {
        if(this.vmRide!!.isRecurring) {
            layoutRepeatOn.visibility = View.VISIBLE
            layoutRepeatUntil.visibility = View.VISIBLE
            rdoYes.setImageResource(R.drawable.ic_radio_button_checked)
            rdoNo.setImageResource(R.drawable.ic_radio_button_unchecked)
        } else {
            layoutRepeatOn.visibility = View.GONE
            layoutRepeatUntil.visibility = View.GONE
            rdoYes.setImageResource(R.drawable.ic_radio_button_unchecked)
            rdoNo.setImageResource(R.drawable.ic_radio_button_checked)
        }

        if(this.vmRide!!.flagWeekdays[0])
            imgSunday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgSunday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[1])
            imgMonday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgMonday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[2])
            imgTuesday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgTuesday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[3])
            imgWednesday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgWednesday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[4])
            imgThursday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgThursday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[5])
            imgFriday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgFriday.setImageResource(R.drawable.ic_checkbox)

        if(this.vmRide!!.flagWeekdays[6])
            imgSaturday.setImageResource(R.drawable.ic_checkbox_checked)
        else
            imgSaturday.setImageResource(R.drawable.ic_checkbox)
    }

    private fun validateFields(): Boolean {
        if(this.vmRide!!.isRecurring == false) {
            return true
        }

        var anyFlagChecked = false
        for(flag in vmRide!!.flagWeekdays) {
            anyFlagChecked = anyFlagChecked || flag
        }
        if(!anyFlagChecked) {
            showToast("Please select the days of week to repeat.")
            return false
        }

        if(vmRide!!.dateRepeatUntil == null) {
            showToast("Please select the date to repeat.")
            return false
        }

        return true
    }

    private fun showDatePopupDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            val date = calendar.time
            if(UtilsDate.isSameDate(Date(), date)) {
                showToast("You cannot book a ride for the past date.")
            } else if(date.before(this.vmRide!!.date)) {
                showToast("End date should be at least 1 day behind the start date.")
            } else {
                this.vmRide!!.dateRepeatUntil = date
                txtRepeatDate.text = UtilsDate.getStringFromDateTimeWithFormat(date, EnumDateTimeFormat.MMddyyyy.value, null)
            }
        }, year, month, day)

        dialog.show()
    }

    private fun gotoRideSummaryFragment() {
        hideKeyboard()
        if(!this.validateFields()) return

        mainFragment.addFragment(RideSummaryFragment.newInstance(this.vmRide!!))
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
            gotoRideSummaryFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance(modelRide: RideViewModel?) =
            RiderRecurringFragment().apply {
                arguments = Bundle().apply {
                    vmRide = modelRide
                }
            }
    }
}