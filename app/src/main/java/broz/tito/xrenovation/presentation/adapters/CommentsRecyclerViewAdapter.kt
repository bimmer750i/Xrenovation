package broz.tito.xrenovation.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.CommentItemBinding
import broz.tito.xrenovation.presentation.entities.DisplayComment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentsRecyclerViewAdapter(val commentOptionsClicker : (commentId : String,criminalLocalId : String) -> Boolean) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "CommentsRecyclerViewAdapter"

    var commentItems : ArrayList<DisplayComment> = ArrayList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayComment = commentItems.get(position)
        Log.d(TAG, "onBindViewHolder -- comment -- ${displayComment.comment}")
        val sdf = SimpleDateFormat("dd/MM/YYYY HH:mm")
        sdf.timeZone = TimeZone.getDefault()
        val date = sdf.format(Date(displayComment.comment.timeAdded*1000))
        holder.binding.textViewTimeAdded.text = date
        Glide.with(holder.binding.root)
            .load(Firebase.storage.reference.child("${displayComment.comment.localId}/avatars/avatar.jpg"))
            .placeholder(R.drawable.baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.binding.imageViewUserPhoto)
        holder.binding.textViewUserName.text = displayComment.comment.displayName
        holder.binding.textViewLocalId.text = displayComment.comment.localId
        holder.binding.textViewCommentText.text = displayComment.comment.text
        holder.binding.imageViewReportViolation.setOnClickListener {
            val popupMenu = PopupMenu(holder.binding.root.context, holder.binding.imageViewReportViolation)
            popupMenu.menuInflater.inflate(R.menu.comment_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.report_comment_violation -> {
                        commentOptionsClicker(displayComment.commentId,displayComment.comment.localId)
                        true
                    }
                    else -> {false}
                }
            }
            popupMenu.show()
        }
    }

    inner class ViewHolder(val binding : CommentItemBinding) : RecyclerView.ViewHolder(binding.root)

}