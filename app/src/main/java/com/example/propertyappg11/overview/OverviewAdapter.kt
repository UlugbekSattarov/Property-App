package com.example.propertyappg11.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.propertyappg11.R
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.databinding.LayoutItemOverviewBinding
import com.example.propertyappg11.util.helpers.SharedElementTransitionHelper
import java.util.*

fun interface OnLastItemDisplayedListener { fun onLastItemDisplayed() }

class OverviewAdapter(private val onClickListener: OnClickListener,
                      private val onLastItemDisplayed : OnLastItemDisplayedListener? = null) :
        ListAdapter<PropProperty,
                OverviewAdapter.MarsPropertyViewHolder>(DiffCallback) {


    override fun onViewAttachedToWindow(holder: MarsPropertyViewHolder) {
        onLastItemDisplayed?.let {
            if (holder.absoluteAdapterPosition == (currentList.count() - 1))
                it.onLastItemDisplayed()
        }
        super.onViewAttachedToWindow(holder)
    }

    class MarsPropertyViewHolder(private val binding: LayoutItemOverviewBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(propProperty: PropProperty) {

            val sharedElement = itemView
            val transitionName = SharedElementTransitionHelper.getTransitionName(propProperty)
            ViewCompat.setTransitionName(sharedElement, transitionName)
            binding.property = propProperty

            val cal = Calendar.getInstance().apply {
                time = Date()

                add(Calendar.HOUR,-Random().nextInt(24*95))
            }

            binding.caption.text = binding.root.resources.getString(R.string.available_since, cal.time)
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PropProperty>() {
        override fun areItemsTheSame(oldItem: PropProperty, newItem: PropProperty): Boolean {
            return oldItem.id === newItem.id
        }

        override fun areContentsTheSame(oldItem: PropProperty, newItem: PropProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MarsPropertyViewHolder {

        return MarsPropertyViewHolder(LayoutItemOverviewBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }

    class OnClickListener(val clickListener: (propProperty : PropProperty) -> Unit) {
        fun onClick(propProperty:PropProperty) = clickListener(propProperty)
    }
}
