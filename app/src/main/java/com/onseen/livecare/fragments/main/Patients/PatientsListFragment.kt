package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applikeysolutions.com.moonrefresh.MoonRefresh
import com.onseen.livecare.R
import com.onseen.livecare.adapters.ConsumersAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.LocalNotification.LocalNotificationObserver
import kotlinx.android.synthetic.main.fragment_consumers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PatientsListFragment : BaseMainHomeFragment(), LocalNotificationObserver {

    private lateinit var consumersAdapter: ConsumersAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var arrayConsumers: MutableList<ConsumerDataModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LocalNotificationManager.sharedInstance().addObserver(this)
        return inflater.inflate(R.layout.fragment_consumers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.tab_consumers), true)

        btnClearQuery.setOnClickListener {
            if (edtSearchQuery.text.isNotBlank()) {
                edtSearchQuery.setText("")
            } else if (edtSearchQuery.isFocused) {
                hideKeyboard(edtSearchQuery)
            }
        }
        edtSearchQuery.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                btnClearQuery.visibility = View.VISIBLE
            else
                btnClearQuery.visibility = View.GONE
        }
        edtSearchQuery.addTextChangedListener {
            reloadData()
        }
        btnClearQuery.visibility = View.GONE

        setupRecyclerView()
        reloadData()
    }

    fun reloadData() {
        val keyword = edtSearchQuery.text.toString()
        val results =
            ConsumerManager.sharedInstance().arrayConsumers.filter { it.testWithKeyword(keyword) }

        this.arrayConsumers.clear()
        this.arrayConsumers.addAll(results)

        consumersAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {

        consumersAdapter = ConsumersAdapter(activity, this.arrayConsumers)
        consumersAdapter.itemClickListener = object: RowItemClickListener<ConsumerDataModel> {
            override fun onClickedRowItem(obj: ConsumerDataModel, position: Int) {
                mainFragment.addFragment(ConsumerDetailsFragment.newInstance(obj))
            }
        }
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = consumersAdapter
        }

        pull_to_refresh.setOnRefreshListener(object : MoonRefresh.OnRefreshListener {
            override fun onRefresh() {
                ConsumerManager.sharedInstance().requestGetConsumers(object: NetworkManagerResponse {
                    override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                        GlobalScope.launch(Dispatchers.Main) {
                            pull_to_refresh.setRefreshing()
                            if(responseDataModel.isSuccess()) {
                                reloadData()
                            }
                        }
                    }
                })
            }
        })

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
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
        itemNext.isVisible = false
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun consumerListUpdated() {
        super.consumerListUpdated()
        reloadData()
    }

    override fun onDestroy() {
        LocalNotificationManager.sharedInstance().removeObserver(this)
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientsListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
