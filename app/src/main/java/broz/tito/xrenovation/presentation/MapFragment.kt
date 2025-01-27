package broz.tito.xrenovation.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.House
import broz.tito.xrenovation.data.add_house.entities.SuccessGetHouseResult
import broz.tito.xrenovation.data.get_houses.entities.FailureGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.PendingGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.SuccessGetPointResult
import broz.tito.xrenovation.databinding.FragmentMapBinding
import broz.tito.xrenovation.presentation.adapters.PhotoRecyclerViewAdapter
import broz.tito.xrenovation.presentation.models.MapFragmentViewModel
import broz.tito.xrenovation.presentation.models.MapFragmentViewModelFactory
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject

class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private var isShown = false

    private var house : House = House()

    private var houseId : String = ""

    private lateinit var binding: FragmentMapBinding

    private lateinit var recyclerViewAdapter : PhotoRecyclerViewAdapter

    @Inject
    lateinit var mapFragmentViewModelFactory: MapFragmentViewModelFactory

    private lateinit var viewModel: MapFragmentViewModel

    private val markerList = ArrayList<PlacemarkMapObject>()

    private val listenerList = ArrayList<MapObjectTapListener>()

    private val cameraListener = CameraListener { p0, p1, p2, p3 -> if (isShown && p2 == CameraUpdateReason.GESTURES) {
        Log.d(TAG, "HIDDEN BECAUSE OF GESTURE")
        hideBottomView()
    } }

    private var startLocation = Point(55.755821, 37.617635)
    private var zoom = 9.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,mapFragmentViewModelFactory)[MapFragmentViewModel::class.java]
        MapKitFactory.initialize(activity)
        recyclerViewAdapter = PhotoRecyclerViewAdapter(PhotoRecyclerViewAdapter.DISPLAY_PHOTO_VIEWHOLDER) {
            if (!house.isEmpty()) {
                val directions = MapFragmentDirections.actionMapFragment2ToHouseFragment2(house,houseId)
                findNavController().navigate(directions)
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewBottom.adapter = recyclerViewAdapter
        binding.recyclerviewBottom.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        parentFragmentManager.setFragmentResultListener(SHOULD_OPEN,this) { result, data ->
            val house = data.getHouse(HOUSE)
            showBottomView(house.photos,house.address)
        }
        /*if (viewModel.getHouseResult.value is SuccessGetHouseResult) {
            val house = (viewModel.getHouseResult.value as SuccessGetHouseResult).house
            val houseAddress = house.address
            showBottomView(house.photos,houseAddress)
        }*/
        binding.mapview.mapWindow.map.addCameraListener(cameraListener)
        viewModel.getPointResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingGetPointResult -> {
                }
                is SuccessGetPointResult -> {
                    val resultArrayList = it.points
                    val myLogo = getDrawable(requireContext(), R.drawable.home_vector_solid)?.toBitmap()
                    resultArrayList.forEach {housePoint ->
                        val listener = MapObjectTapListener { p0, p1 ->
                            Log.d(TAG, "placemark clicked")
                            viewModel.getHouse(housePoint.houseId)
                            true
                        }
                        val placemark = binding.mapview.mapWindow.map.mapObjects.addPlacemark().apply {
                            geometry = Point(housePoint.latLon.latitude,housePoint.latLon.longitude)
                            setIcon(ImageProvider.fromBitmap(myLogo))
                            addTapListener(listener)
                        }
                        listenerList.add(listener)
                        markerList.add(placemark)
                    }

                }
                is FailureGetPointResult -> {

                }
            }
        })
        viewModel.getHouseResult.observe(viewLifecycleOwner) {
            when (it) {
                is SuccessGetHouseResult -> {
                    house = it.house
                    houseId = it.houseId
                    hideBottomView()
                    showBottomView(it.house.photos,it.house.address)
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.mapWindow.map.move(CameraPosition(startLocation,zoom,0f,0f))
        binding.mapview.onStart()
        viewModel.getPoints()
    }

    override fun onStop() {
        super.onStop()
        zoom = binding.mapview.mapWindow.map.cameraPosition.zoom
        startLocation = binding.mapview.mapWindow.map.cameraPosition.target
        MapKitFactory.getInstance().onStop()
        binding.mapview.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    fun showBottomView(photoList : ArrayList<String>,address : String) {
        binding.layoutMapBottom.apply {
            visibility = View.VISIBLE
            val animate = TranslateAnimation(0F, 0F, height.toFloat(), 0F)
            animate.duration = 150
            animate.fillAfter = true
            startAnimation(animate)
        }
        recyclerViewAdapter.list = photoList
        binding.textViewBottomAddress.text = address
        isShown = true
    }

    fun hideBottomView() {
        binding.layoutMapBottom.apply {
            visibility = View.GONE
            val animate = TranslateAnimation(0F, 0F, 0F,height.toFloat())
            animate.duration = 150
            startAnimation(animate)
        }
        binding.recyclerviewBottom.currentItem = 0
        recyclerViewAdapter.list = arrayListOf()
        binding.textViewBottomAddress.text = ""
        isShown = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isShown) {
            viewModel.resetHouseResult()
        }
    }

    companion object {
        const val HOUSE = "HOUSE"
        const val SHOULD_OPEN = "SHOULD_OPEN"
    }

}

fun Bundle.getHouse(key : String) : House {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) this.getSerializable(key) as House
    else this.getSerializable(key,House::class.java) as House
}



