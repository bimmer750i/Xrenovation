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
import broz.tito.xrenovation.presentation.safe_navigation.safeNavigate


class RoutingFragment : Fragment() {

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
            safeNavigate(this,R.id.routingFragment,R.id.action_routingFragment_to_noNetworkAccountFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.UNDEFINED) {
            safeNavigate(this,R.id.routingFragment,R.id.action_routingFragment_to_accountInfoFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_IN) {
            safeNavigate(this,R.id.routingFragment,R.id.action_routingFragment_to_accountInfoFragment)
        }
        else if ((requireActivity().application as App).loggedStatus == LoggedStatus.LOGGED_OUT) {
            safeNavigate(this,R.id.routingFragment,R.id.action_routingFragment_to_accountFragment)
        }
        else {
            safeNavigate(this,R.id.routingFragment,R.id.action_routingFragment_to_accountFragment)

        }
    }


}