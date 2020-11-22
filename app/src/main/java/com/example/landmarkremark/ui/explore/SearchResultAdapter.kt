package com.example.landmarkremark.ui.explore

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

class SearchResultAdapter(val onRecyclerViewOnClickListener: RecyclerViewListener) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    private var searchResultList: MutableList<LocationData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_search_result_list, parent, false))
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.onBind(position, searchResultList, onRecyclerViewOnClickListener)

    }

    fun setList(dataList: MutableList<LocationData>?) {
        this.searchResultList = dataList ?: mutableListOf()
        notifyDataSetChanged()
    }

    class SearchResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val container: ConstraintLayout = view.findViewById(R.id.search_result_list_container)
        private val title: TextView = view.findViewById(R.id.search_result_title)
        private val description: TextView = view.findViewById(R.id.search_result_description)
        private val icon: ImageView = view.findViewById(R.id.search_result_icon)

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
    }
}