package broz.tito.xrenovation.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.House
import broz.tito.xrenovation.databinding.FragmentMapBottomBinding
import broz.tito.xrenovation.presentation.adapters.PhotoRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapBottomFragment : BottomSheetDialogFragment() {

    private val TAG = "MapBottomFragment"

    private lateinit var binding : FragmentMapBottomBinding

    private lateinit var recyclerViewAdapter : PhotoRecyclerViewAdapter

    private var house : House? = null

    private var houseId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerViewAdapter = PhotoRecyclerViewAdapter(PhotoRecyclerViewAdapter.DISPLAY_PHOTO_VIEWHOLDER) {
            if (house != null) {
                val directions = MapFragmentDirections.actionMapFragment2ToHouseFragment2(house!!,houseId!!)
                findNavController().navigate(directions)
            }
            this.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBottomBinding.bind(inflater.inflate(R.layout.fragment_map_bottom,container,false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomPhotoRecyclerView.adapter = recyclerViewAdapter
        binding.bottomPhotoRecyclerView.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        if (arguments != null) {
            recyclerViewAdapter.list = arguments?.getStringArrayList(PHOTO_LIST) as ArrayList<String>
            binding.textViewBottomHouseAddress.text = arguments?.getString(ADDRESS)
            house = arguments?.getSerializable(HOUSE) as House
            houseId = arguments?.getString("ID")
        }
    }

    companion object {
        const val PHOTO_LIST = "PHOTO_LIST"
        const val ADDRESS = "ADDRESS"
        const val HOUSE = "HOUSE"
        const val ID = "ID"
    }

}