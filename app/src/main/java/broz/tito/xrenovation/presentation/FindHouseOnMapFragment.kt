package broz.tito.xrenovation.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.databinding.FragmentFindHouseOnMapBinding
import broz.tito.xrenovation.databinding.FragmentMapBinding
import broz.tito.xrenovation.presentation.models.FindHouseOnMapViewModel
import broz.tito.xrenovation.presentation.models.FindHouseOnMapViewModelFactory
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import javax.inject.Inject

class FindHouseOnMapFragment : Fragment() {

    private lateinit var binding: FragmentFindHouseOnMapBinding

    private var housePoint : Point? = null

    private var searchPointAddress : SearchPointAddress? = null

    private val args : FindHouseOnMapFragmentArgs by navArgs()

    @Inject
    lateinit var findHouseOnMapViewModelFactory : FindHouseOnMapViewModelFactory

    private lateinit var viewModel : FindHouseOnMapViewModel

    private var isMoscow = false

    private val cameraListener = CameraListener { map : Map, p1, p2, finished : Boolean ->
        if (finished) {
            housePoint = map.cameraPosition.target
            housePoint?.let {
                viewModel.searchPoint(it)
            }
        }
        else {
            binding.textViewSearchPoint.text = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,findHouseOnMapViewModelFactory)[FindHouseOnMapViewModel::class.java]
        args.latLon?.let {
            housePoint = Point(it.latitude,it.longitude)
        }
        MapKitFactory.initialize(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindHouseOnMapBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewGoBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonReady.setOnClickListener {
            if ((searchPointAddress != null && housePoint != null)) {
                if (searchPointAddress!!.isMoscow(requireContext())) {
                    parentFragmentManager.setFragmentResult(MAP_RESULT, bundleOf(LATITUDE to housePoint!!.latitude, LONGITUDE to housePoint!!.longitude,
                        ADDRESS to searchPointAddress ))
                }
                findNavController().navigateUp()
            }
            else {
                findNavController().navigateUp()
            }
        }
        viewModel.searchPointResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSearchPointResult -> {

                }
                is SuccessSearchPointResult -> {
                    if (it.searchPointAddress.isMoscow(requireContext())) {
                        searchPointAddress = it.searchPointAddress
                        binding.textViewSearchPoint.text = it.searchPointAddress.toString()
                    }
                    else {
                        housePoint = null
                        binding.textViewSearchPoint.text = getString(R.string.not_moscow)
                    }
                }
                is FailureSearchPointResult -> {

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        val startLocation = Point(55.755821, 37.617635)
        val zoom = 9.5f
        binding.findHouseMapview.mapWindow.map.move(CameraPosition(startLocation,zoom,0f,0f))
        binding.findHouseMapview.mapWindow.map
            .addCameraListener(cameraListener)
        if (args.latLon != null) {
            binding.findHouseMapview.mapWindow.map.move(CameraPosition(housePoint!!,16.5F,0F,0F))
        }
        binding.findHouseMapview.onStart()

    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        binding.findHouseMapview.onStop()
    }

    companion object {
        const val MAP_RESULT = "MAP_RESULT"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val ADDRESS = "SHORT_ADDRESS"
    }


}