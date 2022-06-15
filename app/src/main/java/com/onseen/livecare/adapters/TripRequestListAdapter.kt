package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.TripRequest.DataModel.TripRequestDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate

class TripRequestListAdapter(private val context: Context?, private val arrayTripRequests: ArrayList<TripRequestDataModel>): RecyclerView.Adapter<TripRequestListAdapter.TransportationViewHolder>() {

    // TODO Transportation list
    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)

    var itemClickListener: RowItemClickListener<TripRequestDataModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): TransportationViewHolder {
        return TransportationViewHolder(
            mInflater.inflate(R.layout.row_transportantion_list, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayTripRequests.size
    }

    override fun onBindViewHolder(viewHolder: TransportationViewHolder, position: Int) {
        viewHolder.bindTo(arrayTripRequests[position])
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onClickedRowItem(arrayTripRequests[position], position)
        }
    }

    class TransportationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)
        private val txtStatus: TextView = itemView.findViewById(R.id.txtRouteStatus)
        private val txtPickupAddress: TextView = itemView.findViewById(R.id.txtRouteStartAddress)
        private val txtDropoffAddress: TextView = itemView.findViewById(R.id.txtRouteEndAddress)
        private val txtConsumer: TextView = itemView.findViewById(R.id.txtRouteConsumerName)

        fun bindTo(tripRequest: TripRequestDataModel) {

            txtDate.text = UtilsDate.getStringFromDateTimeWithFormat(tripRequest.dateTime, EnumDateTimeFormat.MMdd.value, null)
            txtTime.text = UtilsDate.getStringFromDateTimeWithFormat(tripRequest.dateTime, EnumDateTimeFormat.hhmma.value, null)
            txtStatus.text = tripRequest.enumStatus.value.toUpperCase()
            txtPickupAddress.text = tripRequest.geoPickup.szAddress
            txtDropoffAddress.text = tripRequest.geoDropoff.szAddress

            txtConsumer.text = tripRequest.refConsumer.szName
        }
    }
}