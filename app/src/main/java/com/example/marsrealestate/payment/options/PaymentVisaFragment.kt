package com.example.marsrealestate.payment.options

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.marsrealestate.MainActivity
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentPaymentVisaBinding
import com.example.marsrealestate.payment.CartViewModel
import com.example.marsrealestate.payment.CartViewModelFactory
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent


class PaymentVisaFragment : Fragment() {

    private val viewModel: PaymentVisaViewModel by viewModels {
        PaymentVisaViewModelFactory()
    }

    private val cartViewModel: CartViewModel by navGraphViewModels(R.id.graph_payment) {
        CartViewModelFactory((activity as MainActivity).marsRepository)
    }

    private lateinit var viewDataBinding: FragmentPaymentVisaBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentPaymentVisaBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        setupAdapters()
        setupNavigation()

        return viewDataBinding.root
    }




    private fun setupNavigation() {
        viewModel.onCardValidated.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {card ->
                val direction = PaymentVisaFragmentDirections
                    .actionDestPaymentVisaToRecapPaymentFragment(cartViewModel.propertyToBuyId.value ?: return@let,card)
                findNavController().navigate(direction)
            }
        }
    }


    private fun setupAdapters() {
        val months = List(12) { String.format("%02d",it + 1)}
        val adapterMonths = ArrayAdapter(requireContext(),R.layout.view_picker_item,months)
        viewDataBinding.cardExpirationMonthValue.setAdapter(adapterMonths)


        val years = List(8) { it + 2020 }
        val adapterYears = ArrayAdapter(requireContext(), R.layout.view_picker_item, years)
        viewDataBinding.cardExpirationYearValue.setAdapter(adapterYears)
    }


}

class MonthAdapter(ctx : Context,months : List<Int> ) : ArrayAdapter<Int>(ctx,R.layout.view_picker_item,months) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        (v as TextView).text = String.format("%02d",getItem(position) ?: 0)
        return v
    }
}