package broz.tito.xrenovation.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentHouseBinding
import broz.tito.xrenovation.presentation.adapters.CommentsRecyclerViewAdapter
import broz.tito.xrenovation.presentation.adapters.PhotoRecyclerViewAdapter
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.HouseFragmentViewModel
import broz.tito.xrenovation.presentation.models.HouseFragmentViewModelFactory
import com.google.android.material.chip.Chip
import javax.inject.Inject


class HouseFragment : Fragment(),SnackBarAble,Disablable {

    private val TAG = "HouseFragment"

    private lateinit var binding : FragmentHouseBinding

    private val args : HouseFragmentArgs by navArgs()

    private lateinit var adapter : PhotoRecyclerViewAdapter

    private lateinit var commentsAdapter : CommentsRecyclerViewAdapter

    private var houseId : String? = null

    @Inject
    lateinit var houseFragmentViewModelFactory: HouseFragmentViewModelFactory

    private lateinit var viewModel: HouseFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,houseFragmentViewModelFactory)[HouseFragmentViewModel::class.java]
        adapter = PhotoRecyclerViewAdapter(PhotoRecyclerViewAdapter.DISPLAY_PHOTO_VIEWHOLDER,{})
        commentsAdapter = CommentsRecyclerViewAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseBinding.inflate(layoutInflater)
        binding.recyclerviewHousePhoto.adapter = adapter
        binding.recyclerviewHousePhoto.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.recyclerviewComments.adapter = commentsAdapter
        binding.recyclerviewComments.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        args.let {
            binding.textViewHouseAddress.text = it.house.address
            binding.textviewConstructionYearYear.text = it.house.year
            binding.textViewNumberOfFlatsNumber.text = it.house.flats
            binding.textViewNumberOfFloorsNumber.text = it.house.floors
            binding.textViewDescriptionText.text = it.house.description
            if (it.house.links.size == 0) {
                binding.textViewLinks.visibility = View.GONE
            }
            else {
                it.house.links.forEach {
                    addChip(it)
                }
            }
            adapter.list = it.house.photos
            houseId = it.houseId
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonComment.setOnClickListener {
            if (!binding.buttonComment.isIndeterminateProgressMode) {
                viewModel.getAccountInfo(requireContext())
            }
        }
        binding.imageViewSuggestHouseCorrection.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(),binding.imageViewSuggestHouseCorrection)
            popupMenu.menuInflater.inflate(R.menu.house_fragment_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                    val directions = HouseFragmentDirections.actionHouseFragmentToHouseCorrectionFragment(houseId!!)
                    findNavController().navigate(directions)
                true }
            popupMenu.show()
        }
        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingGetAccountInfoResult -> {
                    disableViews()
                }
                is SuccessGetAccountInfoResult -> {
                    it.user.emailVerified?.let {verified ->
                        if (verified && binding.editTextComment.checkCommentLength()) {
                            viewModel.addComment(requireContext(),houseId!!,binding.editTextComment.text.toString(),it.user.displayName,it.user.localId,it.user.photoUrl)
                        }
                        else if (!binding.editTextComment.checkCommentLength()) {
                            enableViews()
                        }
                        else {
                            enableViews()
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
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                            enableViews()
                        }
                        "USER_DISABLED" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                            enableViews()
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
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
                    enableViews()
                    when(it.errorMessage) {
                        "TOKEN_EXPIRED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                            //findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        "USER_DISABLED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                            //findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        "USER_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                            //findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        "MISSING_REFRESH_TOKEN" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_logged_in))
                            //findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                }
            }
        }
        viewModel.addCommentResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingAddCommentResult -> {
                    binding.buttonComment.isIndeterminateProgressMode = true
                    binding.buttonComment.progress = 66
                }
                is SuccessAddCommentResult -> {
                    enableViews()
                    binding.editTextComment.text.clear()
                    binding.buttonComment.progress = 0
                    showSnackBarShort(this,binding.root,getString(R.string.comment_under_moderation))
                    viewModel.resetState()
                }
                is FailureAddCommentResult -> {
                    enableViews()
                    binding.buttonComment.progress = 0
                    if (it.errorMessage == "POST_TIMEOUT") {
                        showSnackBarShort(this,binding.root,getString(R.string.post_timeout))
                    }
                    else {
                        showSnackBarShort(this,binding.root,getString(R.string.failed_to_send_comment))
                    }
                }
            }
        })
        viewModel.getCommentsResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingGetCommentsResult -> {

                }
                is SuccessGetCommentsResult -> {
                    if (it.commentsList.size > 0) {
                        binding.recyclerviewComments.visibility = View.VISIBLE
                        commentsAdapter.commentItems = it.commentsList
                    }
                    else {
                        binding.textViewNoComments.visibility = View.VISIBLE
                    }
                }
                is FailureGetCommentsResult -> {

                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.getComments(houseId!!)
    }

    private fun addChip(text : String) {
        val chip = Chip(requireContext())
        chip.tag = text
        chip.text = text.removePrefix("https://").take(13) + ".."
        chip.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(chip.tag.toString()))
            startActivity(browserIntent)
        }
        binding.chipGroupLinks.addView(chip)
    }

    override fun enableViews() {
        binding.editTextComment.isEnabled = true
        binding.buttonComment.isIndeterminateProgressMode = false
    }

    override fun disableViews() {
        binding.editTextComment.isEnabled = false
    }

    fun EditText.checkCommentLength() : Boolean {
        val text = this.text.toString()
        if (text.length < 10) {
            showSnackBarShort(this@HouseFragment,binding.root,getString(R.string.short_comment))
            return false
        }
        else if (text.length > 80) {
            showSnackBarShort(this@HouseFragment,binding.root,getString(R.string.long_comment))
            return false
        }
        else {
            return true
        }
    }

}