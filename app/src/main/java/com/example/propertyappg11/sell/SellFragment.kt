package com.example.propertyappg11.sell

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.propertyappg11.R
import com.example.propertyappg11.ServiceLocator
import com.example.propertyappg11.data.PropProperty
import com.example.propertyappg11.databinding.FragmentSellBinding
import com.example.propertyappg11.util.helpers.FileHelper
import com.example.propertyappg11.util.setupFadeThroughTransition
import com.example.propertyappg11.util.setupToolbarIfDrawerLayoutPresent
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SellFragment : Fragment() {

    companion object {
        const val MIME_TYPE_IMAGE_ALL = "image/*"
    }

    private val viewModel : SellViewModel by viewModels {
        SellViewModelFactory(ServiceLocator.getMarsRepository(requireContext()))
    }

    private lateinit var viewDataBinding : FragmentSellBinding


    private lateinit var openDocumentLauncher : ActivityResultLauncher<Array<String>>

    private lateinit var takePhotoLauncher : ActivityResultLauncher<Uri>
    private lateinit var takePhotoFileURI : Uri

    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    private val isWritePermissionNeeded = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerOnExternalImageReceived()

        if (isWritePermissionNeeded)
            registerOnPermissionResult()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =  FragmentSellBinding.inflate(inflater)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        viewDataBinding.viewModel = viewModel
        viewDataBinding.fragment = this

        setupFadeThroughTransition(viewDataBinding.root)
        requireActivity().setupToolbarIfDrawerLayoutPresent(this,viewDataBinding.toolbar)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setupScrollAfterAreaInput()
        bindLatitudeWithViewModel()
        setupNavigation()

        return viewDataBinding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupNavigation() {
        viewModel.navigateToProperty.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                exitTransition = MaterialFadeThrough()
                val direction = SellFragmentDirections.actionDestSellToSellCompletedFragment(it.id)

                findNavController().navigate(direction)

            }
        }
    }


    private fun registerOnExternalImageReceived() {
        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            onDocumentChosen(uri)
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            onPhotoTaken(success)
        }
    }


    private fun registerOnPermissionResult() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            onPermissionResult(isGranted)
        }
    }



    fun askForExternalImage() =
        ChooseImageSourceDialog().apply {
            onOpenDocumentSelected = ::openDocumentPicker
            onTakePhotoSelected = ::requestPermissionsAndTakePhoto

        }.show(parentFragmentManager, null)



    private fun openDocumentPicker() = openDocumentLauncher.launch(arrayOf(MIME_TYPE_IMAGE_ALL))

    private fun onDocumentChosen(documentURI : Uri?) {
        documentURI?.let {
            viewModel.imgSrcUrl.value = documentURI.toString()

            FileHelper.markMarkAsPermanentlyAvailable(requireActivity().contentResolver,documentURI)
        }
    }
    private fun requestPermissionsAndTakePhoto() {

        if ( ! isWritePermissionNeeded) {
            takePhoto()
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
            return
        }
        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    private fun onPermissionResult(isGranted : Boolean) {
        if (isGranted) {
            takePhoto()
        }
    }


    private fun takePhoto() {
        takePhotoFileURI  = FileHelper
            .addEmptyImageToMediaStore(requireActivity().contentResolver) ?: return
        takePhotoLauncher.launch(takePhotoFileURI)

    }

    private fun onPhotoTaken(success : Boolean) {
        if (success)
            viewModel.imgSrcUrl.value = takePhotoFileURI.toString()
        else
            FileHelper.deleteFile(requireActivity().contentResolver,takePhotoFileURI)
    }

    private fun setupScrollAfterAreaInput() =
        viewDataBinding.areaInputValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                scrollTo(viewDataBinding.buttonPutOnSale,600)
            false
        }


    @Suppress("SameParameterValue")
    private fun scrollTo(destination : View,delay : Long) =
        lifecycleScope.launch {
            delay(delay)
            viewDataBinding
                .fragmentSellScrollview
                .smoothScrollTo(0, destination.y.toInt(), 1000)
        }



    private fun getLatitude() : Float {
        val latitude = viewDataBinding.locationLatitudeInputValue.text.toString().toFloatOrNull() ?: 0f
        val orientation = if (viewDataBinding.latitudeNorth.isChecked) 1 else -1

        return latitude * orientation
    }

    private fun setLatitude(latitude : Float) {
        val oldLatitude = getLatitude()

        if (oldLatitude == latitude) return

        viewDataBinding.locationLatitudeInputValue.setText(latitude.toString())

        if (latitude > 0)
            viewDataBinding.latitudeNorth.isChecked = true
        else if (latitude < 0)
            viewDataBinding.latitudeSouth.isChecked = true
    }


    private fun bindLatitudeWithViewModel() {

        viewDataBinding.locationLatitudeInputValue.addTextChangedListener {
            viewModel.latitude.value = getLatitude()
        }

        viewDataBinding.locationLatitudeOrientationInput.setOnCheckedChangeListener { _, _ ->
            viewModel.latitude.value = getLatitude()
        }

        viewModel.latitude.observe(viewLifecycleOwner) { latitude ->
            setLatitude(latitude)
        }

    }

}


@BindingAdapter("propertyType")
fun RadioGroup.setPropertyType(oldType: String?, newType: String?) {
    if (newType != null && newType != oldType) {
        val toCheck = when (newType) {
            PropProperty.TYPE_BUY -> R.id.sell_or_rent_input_sell
            PropProperty.TYPE_RENT -> R.id.sell_or_rent_input_rent
            else -> R.id.sell_or_rent_input_rent
        }
        check(toCheck)
    }
}


@InverseBindingAdapter(attribute = "propertyType")
fun RadioGroup.getPropertyType(): String {
    return when (checkedRadioButtonId) {
        R.id.sell_or_rent_input_sell -> PropProperty.TYPE_BUY
        R.id.sell_or_rent_input_rent -> PropProperty.TYPE_RENT
        else -> ""
    }
}

@BindingAdapter("app:propertyTypeAttrChanged")
fun RadioGroup.setPropertyTypeListeners(
    attrChange: InverseBindingListener
) {
    setOnCheckedChangeListener { _, _ ->
        attrChange.onChange()
    }

}
