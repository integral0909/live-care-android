package com.onseen.livecare.fragments.main.Settings


import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.RecyclerView

import com.onseen.livecare.R
import com.onseen.livecare.activities.MainActivity
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsGeneral
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsMainHomeFragment : BaseMainHomeFragment() {

    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.tab_consumers), true)

        txtLogout.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                (activity as MainActivity).logout()
            }
        }

    }

    override fun viewAppeared() {
        super.viewAppeared()
    }

    private fun refreshFields() {
        if(UserManager.sharedInstance().currentUser == null) return

        val me = UserManager.sharedInstance().currentUser
        txtAccountName.text = me!!.szUsername
        edtFullName.text = me.szName
        edtEmail.text = me.szEmail
        edtAppInfo.text = "App Info:  " + UtilsGeneral.getBeautifiedAppVersionInfo()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }

        refreshFields()
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

    companion object {
        @JvmStatic
        fun newInstance() =
            SettingsMainHomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
