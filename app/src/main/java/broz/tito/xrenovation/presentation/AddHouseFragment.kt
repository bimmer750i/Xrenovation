package broz.tito.xrenovation.presentation

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.AlertDialogAddUrlBinding
import broz.tito.xrenovation.databinding.FragmentAddHouseBinding
import broz.tito.xrenovation.databinding.SimpleDropdownListItemBinding
import broz.tito.xrenovation.presentation.adapters.PhotoItemTouchHelperCallback
import broz.tito.xrenovation.presentation.adapters.PhotoRecyclerViewAdapter
import broz.tito.xrenovation.presentation.adapters.SuggestArrayAdapter
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.AddHouseViewModel
import broz.tito.xrenovation.presentation.models.AddHouseViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.yandex.mapkit.geometry.Point
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class AddHouseFragment : Fragment(), SnackBarAble,ProgressBarAble,Disablable {

     private val TAG = "AddHouseFragment"

     private lateinit var binding : FragmentAddHouseBinding

     private lateinit var dropdownListItemBinding: SimpleDropdownListItemBinding

     private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

     private var photoList : ArrayList<String> = ArrayList()

     private var urlList : ArrayList<String> = ArrayList()

     private var floors : String? = null

     private var isChecked : Boolean = false

     private lateinit var recyclerViewAdapter : PhotoRecyclerViewAdapter

     @Inject
     lateinit var addHouseViewModelFactory : AddHouseViewModelFactory

     private lateinit var viewModel: AddHouseViewModel

     private var housePoint : Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            housePoint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val latlon = savedInstanceState.getSerializable(POINT,LatLon::class.java)
                latlon?.let {
                    Point(it.latitude,it.longitude)
                }
            } else {
                val latlon = savedInstanceState.getSerializable(POINT) as LatLon?
                latlon?.let {
                    Point(it.latitude,it.longitude)
                }
            }
            savedInstanceState.getStringArrayList(PHOTO_LIST)?.let {
                photoList = it
            }
            savedInstanceState.getStringArrayList(URL_lIST)?.let {
                urlList = it
            }
            savedInstanceState.getString(FLOORS)?.let {
                floors = it
            }
            savedInstanceState.getBoolean(CHECKED).let {
                isChecked = it
            }
        }
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,addHouseViewModelFactory)[AddHouseViewModel::class.java]
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // FUCK YOU, STUPID NAVIGATION COMPONENT X2
        }
        recyclerViewAdapter = PhotoRecyclerViewAdapter(PhotoRecyclerViewAdapter.ADD_PHOTO_VIEWHOLDER,{})
        recyclerViewAdapter.list = photoList
        registerForActivityResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dropdownListItemBinding = SimpleDropdownListItemBinding.inflate(layoutInflater)
        binding = FragmentAddHouseBinding.inflate(layoutInflater)
        urlList.forEach {
            addChip(it)
        }
        binding.checkBoxVariableFloors.isChecked = isChecked
        floors?.let {
            binding.editTextNumberOfFloors.setText(it)
            floors = null
        }
        binding.recyclerviewChosenPhoto.adapter = recyclerViewAdapter
        binding.recyclerviewChosenPhoto.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val itemTouchHelperCallback = PhotoItemTouchHelperCallback()
        val photoItemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        photoItemTouchHelper.attachToRecyclerView(binding.recyclerviewChosenPhoto)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.autoCompleteTextView.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.suggestAddress(requireContext(),binding.autoCompleteTextView.text.toString())
            }

        })
        binding.imageViewAddressLocation.setOnClickListener {
            if (housePoint != null) {
                val directions = AddHouseFragmentDirections.actionAddHouseFragmentToFindHouseOnMapFragment(LatLon(housePoint!!.latitude,housePoint!!.longitude))
                findNavController().navigate(directions)
            }
            else {
                findNavController().navigate(R.id.action_addHouseFragment_to_findHouseOnMapFragment)
            }
        }
        binding.checkBoxVariableFloors.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                binding.editTextNumberOfFloors.text.clear()
                binding.editTextNumberOfFloors.inputType = InputType.TYPE_CLASS_PHONE
                binding.editTextNumberOfFloors.keyListener = DigitsKeyListener.getInstance("0123456789-")
            }
            else {
                binding.editTextNumberOfFloors.text.clear()
                binding.editTextNumberOfFloors.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
        binding.imageViewAddButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.chipAddUrl.setOnClickListener {
            showAddUrlAlertDialog("",false,null)
        }
        val suggestArrayAdapter = SuggestArrayAdapter(requireContext(),R.layout.simple_dropdown_list_item)
        binding.autoCompleteTextView.setAdapter(suggestArrayAdapter)
        binding.autoCompleteTextView.threshold = 1
        binding.autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            suggestArrayAdapter.items.get(position).center?.let {
                housePoint = it
            }
        }
        binding.buttonAddHouse.setOnClickListener {
            if (!binding.buttonAddHouse.isIndeterminateProgressMode) {
                addHouse()
            }
        }
        viewModel.suggestAddressResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSuggestAddressResult -> {

                }
                is SuccessSuggestAddressResult -> {
                    val set = it.list
                        .toHashSet()
                        .toList()
                    suggestArrayAdapter.items = set
                    suggestArrayAdapter.filter.filter(binding.autoCompleteTextView.text,null)
                }
                is FailureSuggestAddressResult -> {

                }
            }
        }
        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingGetAccountInfoResult -> {
                    showProgressBar()
                    disableViews()
                }
                is SuccessGetAccountInfoResult -> {
                    it.user.emailVerified?.let {verified ->
                        if (verified) {
                            viewModel.searchPoint(housePoint!!)
                        }
                        else {
                            enableViews()
                            hideProgressBar()
                            showSnackBarShort(this,binding.root,getString(R.string.email_not_verified))
                        }
                    }
                }
                is FailureGetAccountInfoResult -> {
                    when (it.errorMessage) {
                        "INVALID_ID_TOKEN" -> {
                            viewModel.refreshToken(requireContext())
                        }
                        "USER_NOT_FOUND" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                            hideProgressBar()
                            enableViews()
                        }
                        "USER_DISABLED" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                            hideProgressBar()
                            enableViews()
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                            hideProgressBar()
                            enableViews()
                        }
                    }
                }
            }
        }
        viewModel.refreshTokenResult.observe(viewLifecycleOwner) {
            when (it) {
                is SuccessRefreshTokenResult -> {
                    viewModel.getAccountInfo(requireContext())
                }
                is FailureRefreshTokenResult -> {
                    when(it.errorMessage) {
                        "TOKEN_EXPIRED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "USER_DISABLED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "USER_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "MISSING_REFRESH_TOKEN" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                }
            }
        }
        viewModel.searchPointResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSearchPointResult -> {

                }
                is SuccessSearchPointResult -> {
                    if (it.searchPointAddress.isMoscow(requireContext()) && !it.searchPointAddress.house.isNullOrEmpty()) {
                        viewModel.loadPhotosToFireBase(requireContext(),UUID.randomUUID().toString().take(10),photoList)
                    }
                    else if (!it.searchPointAddress.isMoscow(requireContext())) {
                        enableViews()
                        hideProgressBar()
                        showSnackBarLong(this,binding.root,getString(R.string.not_moscow))
                    }
                    else if (it.searchPointAddress.house.isNullOrEmpty()) {
                        enableViews()
                        hideProgressBar()
                        showSnackBarLong(this,binding.root,getString(R.string.no_house_in_address))
                    }
                }
                is FailureSearchPointResult -> {
                    enableViews()
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                }
            }
        }
        viewModel.loadPhotosResult.observe(viewLifecycleOwner) {
            when (it)  {
                is PendingLoadPhotosResult -> {

                }
                is SuccessLoadPhotosResult -> {
                    viewModel.addHouse(requireContext(),"house", House(
                        LatLon(housePoint!!.latitude,housePoint!!.longitude),
                    binding.autoCompleteTextView.text.toString(),
                    binding.editTextNumberOfFloors.text.toString(),
                    binding.editTextNumberOfFlats.text.toString(),
                    binding.editTextConstructionYear.text.toString(),
                    binding.editTextDescription.text.toString(),
                    it.urlList,urlList
                    )
                    )
                    Log.d(TAG, "successLoadPhotosResult: ${it.urlList}")
                }
                is FailureLoadPhotosResult -> {
                    enableViews()
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.failed_to_upload_photos))
                }

            }
        }
        viewModel.addHouseResult.observe(viewLifecycleOwner) { addHouseResult ->
            when (addHouseResult) {
                is PendingAddHouseResult -> {

                }
                is SuccessAddHouseResult -> {
                    addHouseResult.houseResponse.name?.let {name ->
                        viewModel.addHousePoint(requireContext(),name,HousePoint(name,LatLon(housePoint!!.latitude,housePoint!!.longitude)))
                    }
                }
                is FailureAddHouseResult -> {
                    enableViews()
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                }
            }
        }
        viewModel.addHousePointResult.observe(viewLifecycleOwner) { addHousePointResult ->
            when (addHousePointResult) {
                is PendingAddHousePointResult -> {

                }
                is SuccessAddHousePointResult -> {
                    showSnackBarLong(this,binding.root,getString(R.string.house_added))
                    viewModel.resetState()
                    enableViews()
                    hideProgressBar()
                }
                is FailureAddHousePointResult -> {
                    enableViews()
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                }
            }
        }
        parentFragmentManager.setFragmentResultListener(FindHouseOnMapFragment.MAP_RESULT,viewLifecycleOwner) { requestKey, bundle ->
            if (requestKey == FindHouseOnMapFragment.MAP_RESULT) {
                var text : String? = ""
                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(FindHouseOnMapFragment.ADDRESS,SearchPointAddress::class.java)?.toShortAddress()
                } else {
                    (bundle.getSerializable(FindHouseOnMapFragment.ADDRESS) as SearchPointAddress).toShortAddress()
                }
                binding.autoCompleteTextView.setText(text,false)
                val latitude = bundle.getDouble(FindHouseOnMapFragment.LATITUDE)
                val longitude = bundle.getDouble(FindHouseOnMapFragment.LONGITUDE)
                housePoint = Point(latitude,longitude)
            }
        }
    }

    override fun showProgressBar() {
        binding.buttonAddHouse.isIndeterminateProgressMode = true
        binding.buttonAddHouse.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonAddHouse.isIndeterminateProgressMode = false
        binding.buttonAddHouse.progress = 0
    }

    override fun enableViews() {
        binding.apply {
            editTextNumberOfFlats.isEnabled = true
            editTextConstructionYear.isEnabled = true
            editTextNumberOfFloors.isEnabled = true
            editTextDescription.isEnabled = true
            buttonAddHouse.isEnabled = true
            recyclerviewChosenPhoto.visibility = View.VISIBLE
        }
    }

    override fun disableViews() {
        binding.apply {
            editTextNumberOfFlats.isEnabled = false
            editTextConstructionYear.isEnabled = false
            editTextNumberOfFloors.isEnabled = false
            editTextDescription.isEnabled = false
            buttonAddHouse.isEnabled = false
            recyclerviewChosenPhoto.visibility = View.GONE
        }
    }

    private fun addHouse() {
        if (housePoint == null) {
            showSnackBarLong(this,binding.root,getString(R.string.no_address_selected))
        }
        else if (binding.editTextNumberOfFloors.text.isNullOrEmpty()) {
            showSnackBarLong(this,binding.root,getString(R.string.no_floors_entered))
        }
        else if (binding.editTextNumberOfFlats.text.isNullOrEmpty()) {
            showSnackBarLong(this,binding.root,getString(R.string.no_flats_entered))
        }
        else if (binding.editTextConstructionYear.text.isNullOrEmpty()) {
            showSnackBarLong(this,binding.root,getString(R.string.no_year_entered))
        }
        else if (binding.editTextDescription.text.toString().length < 20) {
            showSnackBarLong(this,binding.root,getString(R.string.no_description_added))
        }
        else if (photoList.size == 0) {
            showSnackBarLong(this,binding.root,getString(R.string.no_photos_selected))
        }
        else {
            viewModel.getAccountInfo(requireContext())
        }
    }

    private fun showAddUrlAlertDialog(text : String, isURLChip : Boolean, chip: Chip?) {
        val binding = AlertDialogAddUrlBinding.inflate(layoutInflater)
        if (isURLChip) {
            binding.editTextAddLink.setText(text)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_url)
            .setView(binding.root)
            .setPositiveButton(R.string.add) { dialogInterface, num ->
                if (!isURLChip) {
                    val typedText = binding.editTextAddLink.text.toString()
                    if (!urlList.contains(typedText)) {
                        urlList.add(typedText)
                        addChip(urlList.last())
                    }
                }
                else {
                    val typedText = binding.editTextAddLink.text.toString()
                    if (typedText != text) {
                        val position = urlList.indexOf(text)
                        if (position > -1) {
                            urlList[position] = typedText
                            chip?.tag = typedText
                            chip?.text = typedText.take(15)
                        }
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, num ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
        Log.d(TAG, "url_list : $urlList")
    }

    private fun addChip(text : String) {
        val chip = Chip(requireContext())
            chip.tag = text
            chip.text = text.take(15)
            chip.isCloseIconVisible = true
            chip.setOnClickListener {
                showAddUrlAlertDialog(chip.tag.toString(),true,chip)
            }
            chip.setOnCloseIconClickListener {
                binding.chipGroupLinks.removeView(chip)
                urlList.removeIf { it == chip.tag }
            }
        binding.chipGroupLinks.addView(chip)
    }

    private fun registerForActivityResult() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uri ->
            if (uri.size > 0) {
                if (photoList.size == 0) {
                    requireActivity().externalCacheDir?.deleteRecursively()
                }
                val startNumber = photoList.size
                for (i in uri.indices) {
                    val uri = uri.get(i)
                    val file = File(requireActivity().externalCacheDir,"house-picture-${i+startNumber}.jpg")
                    val outputStream = FileOutputStream(file)
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream?.close()
                    photoList.add(file.absolutePath)
                }
                recyclerViewAdapter.list = photoList
                Log.d(TAG, "photolist : $photoList")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        housePoint?.let {
            outState.putSerializable(POINT,LatLon(it.latitude,it.longitude))
        }
        outState.putString(ADDRESS,binding.autoCompleteTextView.text.toString())
        outState.putStringArrayList(PHOTO_LIST,recyclerViewAdapter.list)
        outState.putStringArrayList(URL_lIST,urlList)
        outState.putString(FLOORS,binding.editTextNumberOfFloors.text.toString())
        outState.putBoolean(CHECKED,binding.checkBoxVariableFloors.isChecked)
    }

    companion object {
        const val POINT = "POINT"
        const val ADDRESS = "ADDRESS"
        const val PHOTO_LIST = "PHOTO_LIST"
        const val URL_lIST = "URL_LIST"
        const val FLOORS = "FLOORS"
        const val CHECKED = "CHECKED"
    }

}