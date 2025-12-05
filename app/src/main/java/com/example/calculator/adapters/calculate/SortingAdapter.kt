
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import com.example.calculator.storage.SortType
import com.example.calculator.storage.SortingOption

class SortingAdapter(
    private val sortingOptions: List<SortingOption>,
    private val onItemClicked: (SortType) -> Unit
) : RecyclerView.Adapter<SortingAdapter.SortingViewHolder>() {


    class SortingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.sorting_tipe)
        val iconTextView: TextView = itemView.findViewById(R.id.sorting_up_down)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sorting_item, parent, false)
        return SortingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sortingOptions.size
    }

    override fun onBindViewHolder(holder: SortingViewHolder, position: Int) {
        val currentOption = sortingOptions[position]

        holder.titleTextView.text = currentOption.title
        holder.iconTextView.text = currentOption.icon


        holder.itemView.setOnClickListener {
            onItemClicked(currentOption.sortType)
        }
    }
}
