package broz.tito.xrenovation.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.FragmentMainBinding
import com.yandex.mapkit.MapKitFactory


open class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.addOnLayoutChangeListener { view, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
            if (bottom < bottomWas) {
                binding.bottomNavigationView.visibility = View.GONE
            }
            else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val navHost = childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

}


