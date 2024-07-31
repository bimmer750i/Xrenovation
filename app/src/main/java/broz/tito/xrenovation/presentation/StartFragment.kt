package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.FailureRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.LoggedStatus
import broz.tito.xrenovation.data.auth.entities.NetworkStatus
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.databinding.FragmentStartBinding
import broz.tito.xrenovation.presentation.models.StartFragmentViewModel
import broz.tito.xrenovation.presentation.models.StartFragmentViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    @Inject
    lateinit var startFragmentViewModelFactory : StartFragmentViewModelFactory

    private lateinit var viewModel: StartFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,startFragmentViewModelFactory)[StartFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshTokenResult.observe(viewLifecycleOwner) {
            when(it) {
                is SuccessRefreshTokenResult -> {
                    (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_IN
                    (requireActivity().application as App).networkStatus = NetworkStatus.NETWORK_OK
                    findNavController().navigate(R.id.action_startFragment_to_mainFragment)
                }
                is FailureRefreshTokenResult -> {
                    when(it.errorMessage) {
                        "TOKEN_EXPIRED" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                        }
                        "USER_DISABLED" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                        }
                        "USER_NOT_FOUND" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                        }
                        "NO NETWORK" -> {
                            (requireActivity().application as App).networkStatus = NetworkStatus.NO_NETWORK
                        }
                    }
                    findNavController().navigate(R.id.action_startFragment_to_mainFragment)
                }
            }
        }
        viewModel.refreshToken(requireContext())
    }

}