package com.onseen.livecare.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.onseen.livecare.fragments.main.Appointments.AppointmentsFragment
import com.onseen.livecare.fragments.main.MainHomeFragment
import com.onseen.livecare.fragments.main.Patients.PatientsListFragment
import com.onseen.livecare.fragments.main.TripRequests.TripRequestsListFragment
import com.onseen.livecare.fragments.main.Transactions.TransactionsListFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var currentFragment: MainHomeFragment? = null

    override fun getCount(): Int  = 4

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> MainHomeFragment.newInstance(PatientsListFragment::class.java.simpleName, i)
            1 -> MainHomeFragment.newInstance(TransactionsListFragment::class.java.simpleName, i)
            2 -> MainHomeFragment.newInstance(TripRequestsListFragment::class.java.simpleName, i)
            3 -> MainHomeFragment.newInstance(AppointmentsFragment::class.java.simpleName, i)
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Consumers"
            1 -> "Transactions"
            2 -> "Transportation"
            3 -> "Appointments"
            else -> ""
        }
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (currentFragment != `object`)
            currentFragment = `object` as MainHomeFragment

        super.setPrimaryItem(container, position, `object`)
    }
}