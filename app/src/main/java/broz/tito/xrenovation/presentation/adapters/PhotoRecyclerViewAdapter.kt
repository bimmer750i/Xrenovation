package broz.tito.xrenovation.presentation.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.ChosenPhotoViewholderBinding
import broz.tito.xrenovation.databinding.DisplayPhotoViewholderBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import java.security.Key
import java.security.Signature

class PhotoRecyclerViewAdapter(val viewHolderType : Int,val displayPhotoClickListener: OnClickListener) : Adapter<PhotoRecyclerViewAdapter.BaseViewHolder>() {

    private val TAG = "PhotoRecyclerViewAdapter"

    var list : ArrayList<String> = ArrayList<String>()
    set(value) {
        field = value
        notifyDataSetChanged()
        Log.d(TAG, "new adapter list value : $value")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewHolderType == 0) {
            val binding = ChosenPhotoViewholderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return AddPhotoViewHolder(binding)
        }
        else  {
            val binding = DisplayPhotoViewholderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return DisplayPhotoViewHolder(binding,displayPhotoClickListener)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.d(TAG, "image loaded from file: ${list.get(position)}")
        if (holder is AddPhotoViewHolder) {
            Glide.with(holder.binding.root)
                .load(Uri.fromFile(File(list.get(position))))
                .signature(ObjectKey(File(list.get(position)).lastModified()))
                .into(holder.binding.imageViewPhoto)
        }
        else if (holder is DisplayPhotoViewHolder) {
            Glide.with(holder.binding.root)
                .load(list.get(position))
                .into(holder.binding.imageViewDisplayPhoto)
        }

    }

    open inner class BaseViewHolder(view: View) : ViewHolder(view)

    inner class AddPhotoViewHolder(val binding: ChosenPhotoViewholderBinding) : BaseViewHolder(binding.root), OnClickListener {

        init {
            binding.imageViewDeletePhoto.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            list.removeAt(adapterPosition)
            notifyDataSetChanged()
        }
    }

    inner class DisplayPhotoViewHolder(val binding : DisplayPhotoViewholderBinding,clickListener: OnClickListener) : BaseViewHolder(binding.root) {

        init {
            binding.imageViewDisplayPhoto.setOnClickListener(clickListener)
        }

    }

    companion object {
        const val ADD_PHOTO_VIEWHOLDER = 0
        const val DISPLAY_PHOTO_VIEWHOLDER = 1
    }


}