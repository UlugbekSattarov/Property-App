package com.example.propertyappg11.overview

import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.propertyappg11.R
import com.example.propertyappg11.data.query.PropApiFilter
import com.example.propertyappg11.data.query.PropApiSorting
import com.google.android.material.chip.ChipGroup


@BindingAdapter("marsApiFilter")
fun ChipGroup.setMarsApiFilter(oldValue: PropApiFilter.PropPropertyType?, newValue: PropApiFilter.PropPropertyType?) {
    if (newValue != null && newValue != oldValue) {
        val chipId = when (newValue) {
            PropApiFilter.PropPropertyType.ALL -> R.id.chip_all
            PropApiFilter.PropPropertyType.BUY -> R.id.chip_buy
            PropApiFilter.PropPropertyType.RENT -> R.id.chip_rent
        }
        if (checkedChipId != chipId)
            check(chipId)
    }
}


@InverseBindingAdapter(attribute = "marsApiFilter")
fun ChipGroup.getMarsApiFilter(): PropApiFilter.PropPropertyType {
    return when (checkedChipId) {
        R.id.chip_all -> PropApiFilter.PropPropertyType.ALL
        R.id.chip_buy -> PropApiFilter.PropPropertyType.BUY
        R.id.chip_rent -> PropApiFilter.PropPropertyType.RENT
        else -> PropApiFilter.PropPropertyType.ALL
    }
}


@BindingAdapter("marsApiFilterAttrChanged","onMarsFilterChanged",requireAll = false)
fun ChipGroup.setMarsApiFilterChangedListener(attrChange: InverseBindingListener,
                                              listener : ChipGroup.OnCheckedChangeListener?) {
    val transition = TransitionInflater.from(context).inflateTransition(R.transition.chip_resize)

    setOnCheckedChangeListener { group , checkedId ->
        if (checkedId != View.NO_ID) {
            TransitionManager.beginDelayedTransition(group,transition)
            attrChange.onChange()
            listener?.onCheckedChanged(group,checkedId)
        }
    }

}




@BindingAdapter("marsApiSorting")
fun AutoCompleteTextView.setMarsApiSorting(oldValue: PropApiSorting?, newValue: PropApiSorting?) {
    if (newValue != null && newValue != oldValue) {
        val toDisplay = when (newValue) {
            PropApiSorting.PriceAscending -> R.string.priceAscending
            PropApiSorting.PriceDescending -> R.string.priceDescending
            PropApiSorting.Default -> R.string.defaultSorting
        }
        this.setText(context.getString(toDisplay),false)
    }

    val sortingOptions = listOf(context.getString(R.string.defaultSorting),context.getString(R.string.priceAscending),context.getString(R.string.priceDescending))
    val adapterSorting = ArrayAdapter(context, R.layout.view_sorting_option_item, sortingOptions)
    setAdapter(adapterSorting)
}


@InverseBindingAdapter(attribute = "marsApiSorting")
fun AutoCompleteTextView.getMarsApiSorting(): PropApiSorting {
    return when (text.toString()) {
        context.getString(R.string.priceAscending) -> PropApiSorting.PriceAscending
        context.getString(R.string.priceDescending) -> PropApiSorting.PriceDescending
        else -> PropApiSorting.Default
    }
}

@BindingAdapter("app:marsApiSortingAttrChanged")
fun AutoCompleteTextView.setMarsApiSortingListeners(
    attrChange: InverseBindingListener
) {
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, _, _ ->
            attrChange.onChange()
        }


}
