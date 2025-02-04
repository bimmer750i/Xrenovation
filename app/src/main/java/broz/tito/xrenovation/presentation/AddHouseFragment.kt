package broz.tito.xrenovation.presentation

import android.app.AlertDialog
import android.content.Context
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
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.HouseModel
import broz.tito.xrenovation.data.add_house.POST_TIMEOUT
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
import broz.tito.xrenovation.presentation.models.ADDRESS_NOT_MOSCOW
import broz.tito.xrenovation.presentation.models.ADDRESS_NO_HOUSE
import broz.tito.xrenovation.presentation.models.AddHouseViewModel
import broz.tito.xrenovation.presentation.models.AddHouseViewModelFactory
import broz.tito.xrenovation.presentation.safe_navigation.safeNavigate
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

     private var houseAddress = ""

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
            savedInstanceState.getBoolean(CHECKED).let {
                isChecked = it
            }
            savedInstanceState.getString(FLOORS)?.let {
                floors = it
            }
        }
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,addHouseViewModelFactory)[AddHouseViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
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
        binding.recyclerviewChosenPhoto.adapter = recyclerViewAdapter
        binding.recyclerviewChosenPhoto.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val itemTouchHelperCallback = PhotoItemTouchHelperCallback()
        val photoItemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        photoItemTouchHelper.attachToRecyclerView(binding.recyclerviewChosenPhoto)
        binding.editTextDescription.setOnTouchListener { view, motionEvent ->
            binding.editTextDescription.parent.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }
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
                val directions = AddHouseFragmentDirections.actionAddHouseFragmentToFindHouseOnMapFragment(LatLon((housePoint?.latitude ?: 0.0),(housePoint?.longitude ?: 0.0)))
                safeNavigate(this,R.id.addHouseFragment,directions)

            }
            else {
                safeNavigate(this,R.id.addHouseFragment,R.id.action_addHouseFragment_to_findHouseOnMapFragment)
            }
        }
        binding.checkBoxVariableFloors.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                binding.editTextNumberOfFloors.inputType = InputType.TYPE_CLASS_PHONE
                binding.editTextNumberOfFloors.keyListener = DigitsKeyListener.getInstance("0123456789-")
            }
            else {
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
            val suggestItem = suggestArrayAdapter.items[position]
            suggestItem.center?.let {
                housePoint = it
            }
            houseAddress = suggestItem.title.text
        }
        binding.buttonAddHouse.setOnClickListener {
            if (!binding.buttonAddHouse.isIndeterminateProgressMode) {
                addHouse(requireContext(),housePoint,photoList,"house",House(
                    LatLon(housePoint?.latitude ?: 0.0,housePoint?.longitude ?: 0.0),
                    binding.autoCompleteTextView.text.toString(),
                    binding.editTextNumberOfFloors.text.toString(),
                    binding.editTextNumberOfFlats.text.toString(),
                    binding.editTextConstructionYear.text.toString(),
                    binding.editTextDescription.text.toString(),
                    ArrayList(),urlList
                ), HousePoint("",LatLon(housePoint?.latitude ?: 0.0,housePoint?.longitude  ?: 0.0)))
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

        viewModel.addHouseResult.observe(viewLifecycleOwner) { addHouseResult ->
            when (addHouseResult) {
                is PendingAddHouseResult -> {
                    hideProgressBar()
                    showProgressBar()
                    disableViews()
                }
                is SuccessAddHouseResult -> {
                    showSnackBarShort(this,binding.root,getString(R.string.house_added))
                    viewModel.resetState()
                    enableViews()
                    hideProgressBar()
                }
                is FailureAddHouseResult -> {
                    viewModel.resetState()
                    enableViews()
                    hideProgressBar()
                    when (addHouseResult.errorMessage) {
                        TOKEN_EXPIRED -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.missing_refresh_token_error))
                        }
                        USER_DISABLED -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        USER_NOT_FOUND -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        MISSING_REFRESH_TOKEN -> showSnackBarShort(this,binding.root,getString(R.string.missing_refresh_token_error))
                        POST_TIMEOUT -> showSnackBarShort(this,binding.root,getString(R.string.post_timeout_house))
                        EMAIL_NOT_VERIFIED -> showSnackBarShort(this,binding.root,getString(R.string.email_not_verified))
                        ADDRESS_NO_HOUSE -> showSnackBarLong(this,binding.root,getString(R.string.no_house_in_address))
                        ADDRESS_NOT_MOSCOW -> showSnackBarLong(this,binding.root,getString(R.string.not_moscow))
                        NO_NETWORK -> showSnackBarLong(this,binding.root,getString(R.string.no_network_try_again))
                        else -> showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                    }

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
                text?.let {
                    houseAddress = it
                }
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
            autoCompleteTextView.isEnabled = true
            editTextNumberOfFlats.isEnabled = true
            editTextConstructionYear.isEnabled = true
            editTextNumberOfFloors.isEnabled = true
            editTextDescription.isEnabled = true
            checkBoxVariableFloors.isEnabled = true
            imageViewAddressLocation.isEnabled = true
            chipGroupLinks.forEach {
                it.isEnabled = true
            }
            recyclerviewChosenPhoto.visibility = View.VISIBLE
        }
    }

    override fun disableViews() {
        binding.apply {
            autoCompleteTextView.isEnabled = false
            editTextNumberOfFlats.isEnabled = false
            editTextConstructionYear.isEnabled = false
            editTextNumberOfFloors.isEnabled = false
            editTextDescription.isEnabled = false
            checkBoxVariableFloors.isEnabled = false
            imageViewAddressLocation.isEnabled = false
            chipGroupLinks.forEach {
                it.isEnabled = false
            }
            recyclerviewChosenPhoto.visibility = View.GONE
        }
    }

    private fun addHouse(context: Context, point: Point?, list : ArrayList<String>, name : String, house: House, housePoint: HousePoint?) {
        if (housePoint == null || binding.autoCompleteTextView.text.toString().isEmpty() ) {
            showSnackBarLong(this,binding.root,getString(R.string.no_address_selected))
        }
        else if (houseAddress != binding.autoCompleteTextView.text.toString()) {
            Log.d(TAG, "addHouse: houseAddress -- $houseAddress autoCompleteText -- ${binding.autoCompleteTextView.text.toString()}")
            showSnackBarLong(this,binding.root,getString(R.string.incorrect_address))
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
            viewModel.addHouse(context, point, list, name, house, housePoint)
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