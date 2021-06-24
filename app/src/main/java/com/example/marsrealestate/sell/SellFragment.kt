package com.example.marsrealestate.sell

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
import com.example.marsrealestate.R
import com.example.marsrealestate.ServiceLocator
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.databinding.FragmentSellBinding
import com.example.marsrealestate.util.helpers.FileHelper
import com.example.marsrealestate.util.setupFadeThroughTransition
import com.example.marsrealestate.util.setupToolbarIfDrawerLayoutPresent
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

    // After android P, we do not need permission to store the photo in the Media Store
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

        return viewDataBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }



    private fun registerOnExternalImageReceived() {
        //Register a callback when an image is chosen from the system file picker
        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            onDocumentChosen(uri)
        }

        //Register a callback when an image is taken with the camera app
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
            //This is very important to access the same file after an app restart, if it is not done
            //the app will crash
            FileHelper.markMarkAsPermanentlyAvailable(requireActivity().contentResolver,documentURI)
        }
    }

    /**
     * Request the [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] permission and take a photo.
     * This permission is needed because the photo will be stored in the [android.provider.MediaStore],
     * however it is not needed anymore after android P.
     */
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

        //Requesting the permission,
        //takePhoto() is called in the callback defined in onPermissionResult(isGranted)
        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    private fun onPermissionResult(isGranted : Boolean) {
        if (isGranted) {
            takePhoto()
        }
    }


    private fun takePhoto() {
        //Create a new empty file in the MediaStore
        takePhotoFileURI  = FileHelper
            .addEmptyImageToMediaStore(requireActivity().contentResolver) ?: return

        //Open the camera app
        //The camera app needs a uri pointing to a file to save the image
        takePhotoLauncher.launch(takePhotoFileURI)

    }

    private fun onPhotoTaken(success : Boolean) {
        if (success)
            viewModel.imgSrcUrl.value = takePhotoFileURI.toString()
        else
//          We have to destroy the temp empty file since the photo has not been taken
            FileHelper.deleteFile(requireActivity().contentResolver,takePhotoFileURI)
    }




    /**
     * Scroll to the Button putOnSale after the surface are has been given by the user.
     * This is useful to make sure the button is seen.
     */
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
            MarsProperty.TYPE_BUY -> R.id.sell_or_rent_input_sell
            MarsProperty.TYPE_RENT -> R.id.sell_or_rent_input_rent
            else -> R.id.sell_or_rent_input_rent
        }
        check(toCheck)
    }
}


@InverseBindingAdapter(attribute = "propertyType")
fun RadioGroup.getPropertyType(): String {
    return when (checkedRadioButtonId) {
        R.id.sell_or_rent_input_sell -> MarsProperty.TYPE_BUY
        R.id.sell_or_rent_input_rent -> MarsProperty.TYPE_RENT
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