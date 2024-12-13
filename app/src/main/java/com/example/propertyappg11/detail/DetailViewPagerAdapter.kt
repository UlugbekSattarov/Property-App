package com.example.propertyappg11.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.databinding.ViewMiniCardviewBinding
import com.example.propertyappg11.util.setImageUrl

class DetailViewPagerAdapter(private val property : PropProperty) : RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewPagerViewHolder>() {

    class DetailViewPagerViewHolder(private val v : ViewMiniCardviewBinding) : RecyclerView.ViewHolder(v.root) {

        fun bind(propProperty: PropProperty) {
            v.miniImg.setImageUrl(propProperty.imgSrcUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewPagerViewHolder {

        return DetailViewPagerViewHolder(ViewMiniCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: DetailViewPagerViewHolder, position: Int) {
        holder.bind(this.property)

    }


}
