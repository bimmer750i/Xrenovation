package broz.tito.xrenovation.presentation.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import broz.tito.xrenovation.R

fun showEmailNotFoundDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(R.string.email_not_found)
        .setMessage(R.string.email_not_found_text)
        .setPositiveButton(context.getString(R.string.ok)) { dialog,num ->
            dialog.dismiss()
        }
        .create()
        .show()
}