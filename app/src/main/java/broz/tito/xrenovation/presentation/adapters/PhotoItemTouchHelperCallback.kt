package broz.tito.xrenovation.presentation.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class PhotoItemTouchHelperCallback : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val startPosition = viewHolder.adapterPosition
        val targetPosition = target.adapterPosition
        val adapter = recyclerView.adapter as PhotoRecyclerViewAdapter
        val updatedList = adapter.list
        Collections.swap(updatedList,startPosition,targetPosition)
        adapter.list = updatedList
        adapter.notifyItemMoved(startPosition,targetPosition)
        return true
    }



    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // ON SWIPE
    }


}