package com.example.marsrealestate.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.FragmentPurchaseBinding

class PurchaseFragment : Fragment() {



    private lateinit var viewModel: PurchaseViewModel

    private lateinit var viewDataBinding: FragmentPurchaseBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentPurchaseBinding.inflate(inflater)

        viewDataBinding.viewModel = PurchaseViewModel()
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        return viewDataBinding.root
    }



}
