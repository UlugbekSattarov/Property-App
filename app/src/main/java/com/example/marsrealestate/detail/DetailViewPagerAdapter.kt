package com.example.marsrealestate.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.marsrealestate.R
import kotlin.math.abs
import kotlin.math.max

class DetailViewPagerAdapter() : RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewPagerViewHolder>() {

    class DetailViewPagerViewHolder(val v : View) : RecyclerView.ViewHolder(v) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewPagerViewHolder {
        return DetailViewPagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_mini_cardview,parent,false))
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: DetailViewPagerViewHolder, position: Int) {
    }
}