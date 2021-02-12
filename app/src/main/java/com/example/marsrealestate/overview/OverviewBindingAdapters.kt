package com.example.marsrealestate.overview

import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.AdapterViewBindingAdapter
import com.example.marsrealestate.R
import com.example.marsrealestate.data.network.MarsApiFilter
import com.example.marsrealestate.data.network.MarsApiPropertySorting
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


@BindingAdapter("app:marsApiFilterAttrChanged","app:onMarsFilterChanged",requireAll = false)
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
fun AutoCompleteTextView.setMarsApiSorting(oldValue: MarsApiPropertySorting?, newValue: MarsApiPropertySorting?) {
    if (newValue != null && newValue != oldValue) {

        val a = if (newValue == MarsApiPropertySorting.PriceAscending) R.string.priceAscending else R.string.priceDescending
        this.setText(context.getString(a),false)

    }

    val sorting = listOf<String>(context.getString(R.string.priceAscending),context.getString(R.string.priceDescending))
    val adapterSorting = ArrayAdapter(context, R.layout.view_picker_item, sorting)
    setAdapter(adapterSorting)
}


@InverseBindingAdapter(attribute = "marsApiSorting")
fun AutoCompleteTextView.getMarsApiSorting(): MarsApiPropertySorting {
    return when (text.toString()) {
        context.getString(R.string.priceAscending) -> MarsApiPropertySorting.PriceAscending
        context.getString(R.string.priceDescending) -> MarsApiPropertySorting.PriceDescending
        else -> MarsApiPropertySorting.PriceAscending
    }
}

@BindingAdapter("app:marsApiSortingAttrChanged")
fun AutoCompleteTextView.setListeners(
    attrChange: InverseBindingListener
) {
    onItemClickListener =
        AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
//            setText(p0.adapter.getItem(p2).toString())
            attrChange.onChange()
        }

//    onItemSelectedListener


}