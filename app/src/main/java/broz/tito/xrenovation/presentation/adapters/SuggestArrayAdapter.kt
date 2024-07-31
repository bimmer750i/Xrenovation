package broz.tito.xrenovation.presentation.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import broz.tito.xrenovation.databinding.SimpleDropdownListItemBinding
import com.yandex.mapkit.search.SuggestItem

class SuggestArrayAdapter(context: Context, @LayoutRes layout : Int) : ArrayAdapter<String>(context,layout) {

    private val TAG = "SuggestArrayAdapter"

    var items : List<SuggestItem> = ArrayList<SuggestItem>()
    set(value) {
        field = value
        super.clear()
        super.addAll(value.map {
            it.title.text
        })
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SimpleDropdownListItemBinding.inflate((context as Activity).layoutInflater)
        val suggestItem = items[position]
        binding.simpleDropdownListItem.text = suggestItem.title.text + (", " + suggestItem.subtitle?.text ?: "")
        return binding.root
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults()
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
        return filter
    }
}