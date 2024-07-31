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
import broz.tito.xrenovation.databinding.FragmentRoutingBinding


class RoutingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentRoutingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if ((requireActivity().application as App).networkStatus == NetworkStatus.NO_NETWORK) {
            findNavController().navigate(R.id.action_routingFragment_to_noNetworkAccountFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.UNDEFINED) {
            findNavController().navigate(R.id.action_routingFragment_to_accountInfoFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_IN) {
            findNavController().navigate(R.id.action_routingFragment_to_accountInfoFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_OUT) {
            findNavController().navigate(R.id.action_routingFragment_to_accountFragment)
        }
        else {
            findNavController().navigate(R.id.action_routingFragment_to_accountFragment)
        }
    }


}