package com.onseen.livecare.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.RowItemClickListener
import java.io.File

class GridImageAdapter: BaseAdapter {
    var itemClickListener: RowItemClickListener<GridItem>? = null
    private var context: Context? = null
    private var arrayImages: ArrayList<GridItem> = ArrayList()

    constructor(context: Context?, arrayImages: ArrayList<GridItem>) {
        this.context = context
        this.arrayImages.addAll(arrayImages)
    }

    override fun getCount(): Int {
        return arrayImages.size
    }

    override fun getItem(position: Int): Any {
        return arrayImages.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val mInflater: LayoutInflater = LayoutInflater.from(this.context)
        val item = this.arrayImages[position]
        val gridView: View?

        if(item.type == EnumGripRowType.ROW_IMAGE) {
            gridView = mInflater.inflate(R.layout.row_grid_image, parent, false)
            val imgView: ImageView = gridView.findViewById(R.id.imgCell)

            imgView.setImageBitmap(BitmapFactory.decodeFile(item.imgFile!!.getAbsolutePath()))
            imgView.setOnClickListener{
                itemClickListener?.onClickedRowItem(item, position)
            }
        } else {
            gridView = mInflater.inflate(R.layout.row_grid_add_new_image, parent, false)
            val imgView: ImageView = gridView.findViewById(R.id.imgCell)
            imgView.setOnClickListener{
                itemClickListener?.onClickedRowItem(item, position)
            }
        }
        return gridView
    }
}

class GridItem {
    var imgFile: File? = null
    var type: EnumGripRowType = EnumGripRowType.ROW_IMAGE
}

enum class EnumGripRowType(val value: Int) {
    ROW_IMAGE(0),
    ROW_ADD_IMAGE(1)
}