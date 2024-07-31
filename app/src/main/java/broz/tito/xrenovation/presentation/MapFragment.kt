package broz.tito.xrenovation.presentation

import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.LatLon
import broz.tito.xrenovation.data.add_house.entities.SuccessGetHouseResult
import broz.tito.xrenovation.data.get_houses.entities.FailureGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.PendingGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.SuccessGetPointResult
import broz.tito.xrenovation.databinding.FragmentMapBinding
import broz.tito.xrenovation.presentation.models.MapFragmentViewModel
import broz.tito.xrenovation.presentation.models.MapFragmentViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private val mapBottomFragment = MapBottomFragment()

    private lateinit var binding: FragmentMapBinding

    @Inject
    lateinit var mapFragmentViewModelFactory: MapFragmentViewModelFactory

    private lateinit var viewModel: MapFragmentViewModel

    private val markerList = ArrayList<PlacemarkMapObject>()

    private val listenerList = ArrayList<MapObjectTapListener>()

    private var startLocation = Point(55.755821, 37.617635)
    private var zoom = 9.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,mapFragmentViewModelFactory)[MapFragmentViewModel::class.java]
        MapKitFactory.initialize(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            zoom = savedInstanceState.getFloat(ZOOM)
            val latLon = savedInstanceState.getSerializable(TARGET) as LatLon
            startLocation = Point(latLon.latitude,latLon.longitude)
        }
        viewModel.getPointResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingGetPointResult -> {
                    Log.d(TAG, "pending getHouseResult")
                }
                is SuccessGetPointResult -> {
                    Log.d(TAG, "successfully received array_list: ${it.points}")
                    val resultArrayList = it.points
                    val myLogo = (getDrawable(requireContext(), R.drawable.baseline_location_on_24) as VectorDrawable).toBitmap()
                    resultArrayList.forEach {housePoint ->
                        val listener = MapObjectTapListener { p0, p1 ->
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
                    val argBundle = Bundle()
                    argBundle.putStringArrayList(MapBottomFragment.PHOTO_LIST,it.house.photos)
                    argBundle.putString(MapBottomFragment.ADDRESS,it.house.address)
                    argBundle.putSerializable(MapBottomFragment.HOUSE,it.house)
                    argBundle.putString(MapBottomFragment.ID,it.houseId)
                    mapBottomFragment.arguments = argBundle
                    mapBottomFragment.show(parentFragmentManager,"KFC")
                    viewModel.resetGetHouseResult()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(ZOOM,zoom)
        outState.putSerializable(TARGET,LatLon(startLocation.latitude,startLocation.longitude))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        const val ZOOM = "ZOOM"
        const val TARGET = "TARGET"
    }

}