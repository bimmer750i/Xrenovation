package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.LoggedStatus
import broz.tito.xrenovation.data.auth.entities.NetworkStatus
import broz.tito.xrenovation.databinding.FragmentAddHouseRoutingBinding
import broz.tito.xrenovation.presentation.safe_navigation.safeNavigate


class AddHouseRoutingFragment : Fragment() {

    private lateinit var binding : FragmentAddHouseRoutingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddHouseRoutingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if ((requireActivity().application as App).networkStatus == NetworkStatus.NO_NETWORK) {
            safeNavigate(this,R.id.addHouseRoutingFragment,R.id.action_addHouseRoutingFragment_to_noNetworkAddHouseFragmentFragment)
        }
        else {
            safeNavigate(this,R.id.addHouseRoutingFragment,R.id.action_addHouseRoutingFragment_to_addHouseFragment)
        }
    }

}