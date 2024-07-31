package broz.tito.xrenovation.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.CommentItemBinding
import broz.tito.xrenovation.presentation.entities.DisplayComment
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentsRecyclerViewAdapter : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {

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
            .load(displayComment.comment.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.binding.imageViewUserPhoto)
        holder.binding.textViewUserName.text = displayComment.comment.displayName
        holder.binding.textViewLocalId.text = displayComment.comment.localId
        holder.binding.textViewCommentText.text = displayComment.comment.text
    }

    inner class ViewHolder(val binding : CommentItemBinding) : RecyclerView.ViewHolder(binding.root)

}