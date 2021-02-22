package com.example.marsrealestate.detail

import android.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.ViewMiniCardviewBinding
import kotlin.math.abs
import kotlin.math.max
/**
 Oversimplified class, not to be used as a reference for coding.
 See [OverviewAdapter] to see a proper implementation of a [ListAdapter]
 **/
class DetailViewPagerAdapter(private val property : MarsProperty) : RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewPagerViewHolder>() {

    class DetailViewPagerViewHolder(private val v : ViewMiniCardviewBinding) : RecyclerView.ViewHolder(v.root) {

        fun bind(marsProperty: MarsProperty) {
            marsProperty.imgSrcUrl.toIntOrNull()?.let { id ->
                v.miniImg.setImageDrawable(ResourcesCompat.getDrawable(v.root.resources, id, v.root.context.theme))
            }
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