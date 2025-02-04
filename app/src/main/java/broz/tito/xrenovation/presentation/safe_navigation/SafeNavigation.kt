package broz.tito.xrenovation.presentation.safe_navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

// HELPS TO AVOID DestinationNotFoundException
fun safeNavigate(fragment : Fragment, currentDestination : Int, destinationAction : Int) {
    if(fragment.findNavController().currentDestination?.id == currentDestination) {
        fragment.findNavController().navigate(destinationAction)
    }
}

/*fun safeNavigate(fragment : Fragment, currentDestination : Int,navOptions: NavOptions) {
    if(fragment.findNavController().currentDestination?.id == currentDestination) {
        fragment.findNavController().navigate(navOptions)
    }
}*/

fun safeNavigate(fragment : Fragment, currentDestination : Int, navDirections: NavDirections) {
    if(fragment.findNavController().currentDestination?.id == currentDestination) {
        fragment.findNavController().navigate(navDirections)
    }
}

