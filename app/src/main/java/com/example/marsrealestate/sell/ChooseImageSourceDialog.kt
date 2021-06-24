package com.example.marsrealestate.sell

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.marsrealestate.R
import com.example.marsrealestate.databinding.LayoutBottomSheetChoosePhotoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChooseImageSourceDialog : BottomSheetDialogFragment() {

    var onTakePhotoSelected : (() -> Unit)? = null
    var onOpenDocumentSelected : (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme_BottomSheetColoredTheme)
        else
            setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme_BottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutBottomSheetChoosePhotoBinding.inflate(layoutInflater).apply {

            actionTakePhoto.setOnClickListener { onTakePhotoSelected?.invoke(); dismiss() }
            actionOpenDocument.setOnClickListener { onOpenDocumentSelected?.invoke(); dismiss() }
        }

        return binding.root
    }

}

