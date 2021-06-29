package com.example.marsrealestate.overview

import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.marsrealestate.R
import com.example.marsrealestate.data.query.MarsApiFilter
import com.example.marsrealestate.data.query.MarsApiSorting
import com.google.android.material.chip.ChipGroup


@BindingAdapter("marsApiFilter")
fun ChipGroup.setMarsApiFilter(oldValue: MarsApiFilter.MarsPropertyType?, newValue: MarsApiFilter.MarsPropertyType?) {
    if (newValue != null && newValue != oldValue) {
        val chipId = when (newValue) {
            MarsApiFilter.MarsPropertyType.ALL -> R.id.chip_all
            MarsApiFilter.MarsPropertyType.BUY -> R.id.chip_buy
            MarsApiFilter.MarsPropertyType.RENT -> R.id.chip_rent
        }
        if (checkedChipId != chipId)
            check(chipId)
    }
}


@InverseBindingAdapter(attribute = "marsApiFilter")
fun ChipGroup.getMarsApiFilter(): MarsApiFilter.MarsPropertyType {
    return when (checkedChipId) {
        R.id.chip_all -> MarsApiFilter.MarsPropertyType.ALL
        R.id.chip_buy -> MarsApiFilter.MarsPropertyType.BUY
        R.id.chip_rent -> MarsApiFilter.MarsPropertyType.RENT
        else -> MarsApiFilter.MarsPropertyType.ALL
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
fun AutoCompleteTextView.setMarsApiSorting(oldValue: MarsApiSorting?, newValue: MarsApiSorting?) {
    if (newValue != null && newValue != oldValue) {
        val toDisplay = when (newValue) {
            MarsApiSorting.PriceAscending -> R.string.priceAscending
            MarsApiSorting.PriceDescending -> R.string.priceDescending
            MarsApiSorting.Default -> R.string.defaultSorting
        }
        this.setText(context.getString(toDisplay),false)
    }

    //We have to reset the adapter each time because only the selected value is kept otherwise,not the whole list (bug)
    val sortingOptions = listOf(context.getString(R.string.defaultSorting),context.getString(R.string.priceAscending),context.getString(R.string.priceDescending))
    val adapterSorting = ArrayAdapter(context, R.layout.view_sorting_option_item, sortingOptions)
    setAdapter(adapterSorting)
}


@InverseBindingAdapter(attribute = "marsApiSorting")
fun AutoCompleteTextView.getMarsApiSorting(): MarsApiSorting {
    return when (text.toString()) {
        context.getString(R.string.priceAscending) -> MarsApiSorting.PriceAscending
        context.getString(R.string.priceDescending) -> MarsApiSorting.PriceDescending
        else -> MarsApiSorting.Default
    }
}

@BindingAdapter("app:marsApiSortingAttrChanged")
fun AutoCompleteTextView.setMarsApiSortingListeners(
    attrChange: InverseBindingListener
) {
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, _, _ ->
//            setText(p0.adapter.getItem(p2).toString())

            attrChange.onChange()
        }

//    onItemSelectedListener

}