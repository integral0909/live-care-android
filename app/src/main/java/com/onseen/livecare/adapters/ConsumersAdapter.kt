package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel

class ConsumersAdapter(
    private val context: Context?,
    private val arrayConsumers: MutableList<ConsumerDataModel>
): RecyclerView.Adapter<ConsumersAdapter.ConsumerViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)

    var itemClickListener: RowItemClickListener<ConsumerDataModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ConsumerViewHolder {
        return ConsumerViewHolder(
            mInflater.inflate(
                R.layout.row_consumer,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayConsumers.size
    }

    override fun onBindViewHolder(viewHolder: ConsumerViewHolder, position: Int) {
        viewHolder.bindTo(arrayConsumers[position])
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onClickedRowItem(arrayConsumers[position], position)
        }
    }

    class ConsumerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtExternalKey: TextView = itemView.findViewById(R.id.txtExternalKey)
        private val txtRegion: TextView = itemView.findViewById(R.id.txtRegion)
        private val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)

        fun bindTo(consumer: ConsumerDataModel) {
            txtTitle.text = consumer.szName
            txtExternalKey.text = consumer.szExternalKey
            txtRegion.text = consumer.szRegion
//            txtAddress.text = "N/A"
        }
    }
}