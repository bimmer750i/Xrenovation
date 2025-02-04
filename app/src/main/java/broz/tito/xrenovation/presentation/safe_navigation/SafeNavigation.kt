package broz.tito.xrenovation.presentation.safe_navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R

// HELPS TO AVOID DestinationNotFoundException
fun safeNavigate(fragment : Fragment, currentDestination : Int, destinationAction : Int) {
    if(fragment.findNavController().currentDestination?.id == currentDestination) {
        fragment.findNavController().navigate(destinationAction)
    }
}

fun safeNavigate(fragment : Fragment, currentDestination : Int,navOptions: NavOptions) {
    if(fragment.findNavController().currentDestination?.id == currentDestination) {
        fragment.findNavController().navigate(navOptions)
    }
}

