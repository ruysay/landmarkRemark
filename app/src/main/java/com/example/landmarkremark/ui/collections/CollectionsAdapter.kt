package com.example.landmarkremark.ui.collections

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
        val container: ConstraintLayout = view.findViewById(R.id.collection_list_container)
        val title: TextView = view.findViewById(R.id.collection_title)
        val description: TextView = view.findViewById(R.id.collection_description)
        val icon: ImageView = view.findViewById(R.id.collection_icon)

        fun onBind(position: Int, collectionList: List<LocationData>, listener: RecyclerViewListener) {
            val locationData = collectionList[position]
            container.setOnClickListener {
                listener.onRecyclerViewItemClickListener(locationData, null, null)
                val clickAnimation =  AlphaAnimation(0.3f, 1.0f)
                clickAnimation.duration = 150
                container.startAnimation(clickAnimation)
                listener.onRecyclerViewItemClickListener(locationData)
            }

            val imageId = R.drawable.ic_collections//getImageIdByType(NotificationEnum.fromValue(locationData.alarmType))

            title.text = locationData.title
            Timber.d("checkLocationData: $locationData")
            description.text = locationData.description
            Picasso.get().load(imageId).placeholder(R.drawable.gray_background)
                .into(icon)
        }

//        private fun getImageIdByType(info: NotificationEnum?): Int {
//            if (info == null) {
//                return R.drawable.ic_alarm_info
//            }
//            return when (info) {
//                NotificationEnum.VideoLoss -> R.drawable.ic_normal_record
//                NotificationEnum.Motion -> R.drawable.ic_motion_record
//                NotificationEnum.Person -> R.drawable.ic_person_record
//                else -> {
//                    R.drawable.ic_alarm_info
//                }
//            }
//        }
    }
}