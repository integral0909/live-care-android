package com.onseen.livecare.fragments.main.TripRequests


import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applikeysolutions.com.moonrefresh.MoonRefresh

import com.onseen.livecare.R
import com.onseen.livecare.adapters.TripRequestListAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.TripRequests.ViewModel.RideViewModel
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.TripRequest.DataModel.TripRequestDataModel
import com.onseen.livecare.models.TripRequest.TripRequestManager
import kotlinx.android.synthetic.main.fragment_trip_requests_list.*
import kotlinx.android.synthetic.main.fragment_trip_requests_list.fabAdd
import kotlinx.android.synthetic.main.fragment_trip_requests_list.pull_to_refresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TripRequestsListFragment : BaseMainHomeFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var indexConsumer: Int = -1
    private var arrayTripRequests: ArrayList<TripRequestDataModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_requests_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.tab_transportation), true)

        fabAdd.setOnClickListener {
            val ride = RideViewModel()
            if(this.indexConsumer != -1) {
                // Pre-select consumer in Ride-Details screen
                ride.modelConsumer = ConsumerManager.sharedInstance().arrayConsumers[this.indexConsumer]
            }

            mainFragment.addFragment(RiderDetailsFragment.newInstance(ride))
        }

        initUI()
    }

    override fun viewAppeared() {
        super.viewAppeared()

    }

    private fun initUI() {
        refreshFields()
        reloadData()
    }

    private fun refreshFields() {
        val arrayConsumers: ArrayList<ConsumerDataModel> = ArrayList()
        arrayConsumers.addAll(ConsumerManager.sharedInstance().arrayConsumers)

        val arrayConsumerNames: ArrayList<String> = ArrayList()
        arrayConsumerNames.add("Please select consumer")

        for(consumer in arrayConsumers) {
            arrayConsumerNames.add(consumer.szName)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayConsumerNames)
        spnConsumer.setAdapter(adapter)
        spnConsumer.setOnItemSelectedListener(this)
        if(this.indexConsumer != -1) {
            spnConsumer.setSelection(indexConsumer + 1)
        }
    }

    private fun reloadData() {

        arrayTripRequests.clear()
        if(this.indexConsumer == -1) {
            recyclerView_transportation.visibility = View.INVISIBLE
            txtNoRequest.visibility = View.INVISIBLE
        } else {
            val consumer = ConsumerManager.sharedInstance().arrayConsumers[this.indexConsumer]
            this.arrayTripRequests.addAll(TripRequestManager.sharedInstance().getTripRequestsByConsumerId(consumer.id!!))

            if(this.arrayTripRequests.size == 0) {
                recyclerView_transportation.visibility = View.INVISIBLE
                pull_to_refresh.visibility = View.INVISIBLE
                txtNoRequest.visibility = View.VISIBLE
            } else {
                recyclerView_transportation.visibility = View.VISIBLE
                pull_to_refresh.visibility = View.VISIBLE
                txtNoRequest.visibility = View.INVISIBLE
            }
        }

        if(recyclerView_transportation.isVisible) {
            GlobalScope.launch(Dispatchers.Main) {
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        val consumersAdapter = TripRequestListAdapter(activity, this.arrayTripRequests)
        consumersAdapter.itemClickListener = object: RowItemClickListener<TripRequestDataModel> {
            override fun onClickedRowItem(obj: TripRequestDataModel, position: Int) {
                val modelTripRequest = arrayTripRequests[position]
                mainFragment.addFragment(TripRequestDetailsFragment.newInstance(modelTripRequest))
            }
        }
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView_transportation.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = consumersAdapter
        }

        pull_to_refresh.setOnRefreshListener(object : MoonRefresh.OnRefreshListener {
            override fun onRefresh() {
                pull_to_refresh.setRefreshing()
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0) return

        this.indexConsumer = position - 1
        this.requestGetRequests(false)
    }

    private fun requestGetRequests(forceLoad: Boolean) {
        if(this.indexConsumer == -1 || this.indexConsumer >= ConsumerManager.sharedInstance().arrayConsumers.size) {
            return
        }

        val consumer = ConsumerManager.sharedInstance().arrayConsumers[this.indexConsumer]
        this.showProgressHUD()
        TripRequestManager.sharedInstance().requestGetTripRequestsForConsumer(consumer, forceLoad, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        reloadData()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }

        initUI()
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
            TripRequestsListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
