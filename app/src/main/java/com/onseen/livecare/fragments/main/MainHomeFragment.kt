package com.onseen.livecare.fragments.main

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Appointments.AppointmentsFragment
import com.onseen.livecare.fragments.main.Patients.PatientsListFragment
import com.onseen.livecare.fragments.main.Transactions.TransactionsListFragment
import com.onseen.livecare.fragments.main.TripRequests.TripRequestsListFragment
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.ARG_TAB_INDEX
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.ARG_TAG
import java.util.*
import kotlin.system.exitProcess

class MainHomeFragment : BaseFragment() {

    private var tabTitle: String? = null
    private var tabIndex: Int = 0

    private var pressedBackButton = false

    private var mFragmentStack: Stack<String> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabTitle = it.getString(ARG_TAG)
            tabIndex = it.getInt(ARG_TAB_INDEX)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.fragments.size > 0) return

        setTabRootFragment()
    }

    override fun viewAppeared() {

    }

    private fun setTabRootFragment() {
        val fragment: Fragment
        when (tabTitle) {
            PatientsListFragment::class.java.simpleName -> {
                fragment = PatientsListFragment.newInstance()
            }
            TransactionsListFragment::class.java.simpleName -> {
                fragment = TransactionsListFragment.newInstance()
            }
            TripRequestsListFragment::class.java.simpleName -> {
                fragment = TripRequestsListFragment.newInstance()
            }
            AppointmentsFragment::class.java.simpleName -> {
                fragment = AppointmentsFragment.newInstance()
            }
            else -> {
                return
            }
        }
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.viewMainContainer, fragment, tabTitle)
        transaction.commit()

        mFragmentStack.add(fragment.tag)
    }

    fun addFragment(mainHomeFragment: BaseMainHomeFragment) {
        var prevfragment: BaseMainHomeFragment? = null

        if (mFragmentStack.size != 0) {
            prevfragment = childFragmentManager.findFragmentByTag(mFragmentStack.peek()) as BaseMainHomeFragment
        }
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        transaction.add(R.id.viewMainContainer, mainHomeFragment, mainHomeFragment::class.java.simpleName)
        transaction.addToBackStack(mainHomeFragment.javaClass.simpleName)
        transaction.commit()

        if(prevfragment != null) {
            prevfragment.userVisibleHint = false
        }

        mFragmentStack.add(mainHomeFragment.tag)
        mainHomeFragment.userVisibleHint = true
    }

    fun removeFragment(mainHomeFragment: BaseMainHomeFragment) {

        var currentFragment: BaseMainHomeFragment? = null
        if(!mFragmentStack.isEmpty()) {
            mFragmentStack.pop()
            currentFragment = childFragmentManager.findFragmentByTag(mFragmentStack.peek()) as BaseMainHomeFragment
        }

        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        mainHomeFragment.userVisibleHint = false
        transaction.remove(mainHomeFragment)
        transaction.commit()

        if(currentFragment != null) {
            activity?.setTitle(currentFragment.getNavTitle())
            currentFragment.viewAppeared()
            currentFragment.userVisibleHint = true
        }
    }

    fun removeAllFragments() {
        showProgressHUD()

        for (i in 1..childFragmentManager.fragments.size-1) {
            val fragment = childFragmentManager.fragments[i] as BaseMainHomeFragment
            removeFragment(fragment)
        }

//        setTabRootFragment()
        hideBackButton()
        hideProgressHUD()
    }

    fun onBackPressed() {

        if (mFragmentStack.size > 1) {
            val fragment = childFragmentManager.fragments.last() as BaseMainHomeFragment
            removeFragment(fragment)
        } else {
            if (pressedBackButton) {
                exitProcess(-1)
            } else {
                pressedBackButton = true
                showToast(getString(R.string.press_back_button))

                Handler().postDelayed({
                    pressedBackButton = false
                }, 3000)
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(tag: String, tabIndex: Int) =
            MainHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TAG, tag)
                    putInt(ARG_TAB_INDEX, tabIndex)
                }
            }
    }
}
