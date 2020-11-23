package com.example.landmarkremark.ui.collections

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.R
import com.example.landmarkremark.interfaces.RecyclerViewListener
import com.example.landmarkremark.models.LocationData
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CollectionsAdapter (val onRecyclerViewOnClickListener: RecyclerViewListener) : RecyclerView.Adapter<CollectionsAdapter.CollectionViewHolder>() {

    private var collectionList: MutableList<LocationData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_collection_list, parent, false))
    }

    override fun getItemCount(): Int {
        return collectionList.size
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.onBind(position, collectionList, onRecyclerViewOnClickListener)

    }

    fun setList(notificationList: MutableList<LocationData>?) {
        this.collectionList = notificationList ?: mutableListOf()
        notifyDataSetChanged()
    }

    class CollectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val container: ConstraintLayout = view.findViewById(R.id.collection_list_container)
        private val title: TextView = view.findViewById(R.id.collection_title)
        private val description: TextView = view.findViewById(R.id.collection_description)
        private val time: TextView = view.findViewById(R.id.collection_created_time)
        private val icon: ImageView = view.findViewById(R.id.collection_icon)

        fun onBind(position: Int, collectionList: List<LocationData>, listener: RecyclerViewListener) {
            val locationData = collectionList[position]
            container.setOnClickListener {
                listener.onRecyclerViewItemClickListener(locationData, null, null)
                val clickAnimation =  AlphaAnimation(0.3f, 1.0f)
                clickAnimation.duration = 150
                container.startAnimation(clickAnimation)
                listener.onRecyclerViewItemClickListener(locationData)
            }

            title.text = locationData.title
            description.text = locationData.description

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            locationData.createdTime?.let {
                val nowTime = dateFormat.format(Date(locationData.createdTime.toLong()))
                time.text = nowTime
            }

            Picasso.get().load("https://i.imgur.com/ky5h49Z.png").placeholder(R.drawable.gray_background).into(icon)
        }
    }
}