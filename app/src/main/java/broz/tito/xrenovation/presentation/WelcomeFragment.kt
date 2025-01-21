package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.FragmentWelcomeBinding
import broz.tito.xrenovation.presentation.models.WelcomeFragmentViewModel
import broz.tito.xrenovation.presentation.models.WelcomeFragmentViewModelFactory
import javax.inject.Inject


class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    @Inject
    lateinit var viewModelFactory: WelcomeFragmentViewModelFactory
    private lateinit var viewModel: WelcomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,viewModelFactory)[WelcomeFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonContinueToXrenovation.setOnClickListener {
            viewModel.setFirstStartCompleted(requireContext())
            findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
        }
    }

}