package com.example.propertyappg11.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.propertyappg11.data.FavoriteProperty
import com.example.propertyappg11.data.MarsProperty
import com.example.propertyappg11.databinding.LayoutItemFavoritesBinding
import com.example.propertyappg11.util.helpers.SharedElementTransitionHelper

class FavoritesAdapter(private val onClickListener: OnClickListener) :
        ListAdapter<FavoriteProperty,
                FavoritesAdapter.FavoriteViewHolder>(DiffCallback) {


    class FavoriteViewHolder(private var binding: LayoutItemFavoritesBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteProperty) {
            val sharedElement = itemView
            val transitionName = SharedElementTransitionHelper.getTransitionName(favorite.property)
            ViewCompat.setTransitionName(sharedElement, transitionName)

            binding.favorite = favorite
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }


    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<FavoriteProperty>() {
        override fun areItemsTheSame(oldItem: FavoriteProperty, newItem: FavoriteProperty): Boolean {
//            return oldItem === newItem
            return oldItem.favorite.favoriteId == newItem.favorite.favoriteId

        }

        override fun areContentsTheSame(oldItem: FavoriteProperty, newItem: FavoriteProperty): Boolean {
            return oldItem.favorite.favoriteId == newItem.favorite.favoriteId
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FavoriteViewHolder =

         FavoriteViewHolder(LayoutItemFavoritesBinding.inflate(LayoutInflater.from(parent.context)))



    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(favorite)
        }
        holder.bind(favorite)


    }


    class OnClickListener(private val clickListener: (favorite : FavoriteProperty) -> Unit) {
        fun onClick(favorite : FavoriteProperty) = clickListener(favorite)
    }
}
