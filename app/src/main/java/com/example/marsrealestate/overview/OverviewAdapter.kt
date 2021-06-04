package com.example.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.LayoutItemOverviewBinding
import com.example.marsrealestate.util.helpers.SharedElementTransitionHelper
import java.util.*

fun interface OnLastItemDisplayedListener { fun onLastItemDisplayed() }

class OverviewAdapter(private val onClickListener: OnClickListener,
                      private val onLastItemDisplayed : OnLastItemDisplayedListener? = null) :
        ListAdapter<MarsProperty,
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
        fun bind(marsProperty: MarsProperty) {

            val sharedElement = itemView
            val transitionName = SharedElementTransitionHelper.getTransitionName(marsProperty)
            ViewCompat.setTransitionName(sharedElement, transitionName)
            binding.property = marsProperty

            val cal = Calendar.getInstance().apply {
                time = Date()

                add(Calendar.HOUR,-Random().nextInt(24*95))
            }

            binding.caption.text = binding.root.resources.getString(R.string.available_since, cal.time)
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id === newItem.id
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MarsPropertyViewHolder {

        return MarsPropertyViewHolder(LayoutItemOverviewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [MarsProperty]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [MarsProperty]
     */
    class OnClickListener(val clickListener: (marsProperty : MarsProperty) -> Unit) {
        fun onClick(marsProperty:MarsProperty) = clickListener(marsProperty)
    }
}
