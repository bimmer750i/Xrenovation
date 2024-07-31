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


class AddHouseRoutingFragment : Fragment() {

    private lateinit var binding : FragmentAddHouseRoutingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            findNavController().navigate(R.id.action_addHouseRoutingFragment_to_noNetworkAddHouseFragmentFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.UNDEFINED) {
            findNavController().navigate(R.id.action_addHouseRoutingFragment_to_addHouseFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_IN) {
            findNavController().navigate(R.id.action_addHouseRoutingFragment_to_addHouseFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_OUT) {
            findNavController().navigate(R.id.action_addHouseRoutingFragment_to_addHouseFragment)
        }
        else {
            //findNavController().navigate(R.id.action_routingFragment_to_accountFragment)
        }
    }

}