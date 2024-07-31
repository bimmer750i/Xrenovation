package broz.tito.xrenovation.presentation.interfaces

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

interface SnackBarAble {

    fun showSnackBarShort(fragment : Fragment, view : View, text : String) {
        Snackbar.make(fragment.requireContext(),view,text, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackBarLong(fragment : Fragment, view : View, text : String) {
        Snackbar.make(fragment.requireContext(),view,text, Snackbar.LENGTH_LONG).show()
    }

}