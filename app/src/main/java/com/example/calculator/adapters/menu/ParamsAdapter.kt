import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R


interface OnParamClickListener {
    fun onParamClick(param: String)
}

class ParamsAdapter(
    private val paramsList: List<String>,
    private val listener: OnParamClickListener
) : RecyclerView.Adapter<ParamsAdapter.ParamViewHolder>() {

    class ParamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.param_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.param_item, parent, false)
        return ParamViewHolder(view)
    }

    override fun getItemCount(): Int = paramsList.size

    override fun onBindViewHolder(holder: ParamViewHolder, position: Int) {
        val currentParam = paramsList[position]
        holder.textView.text = currentParam

        holder.itemView.setOnClickListener {
            listener.onParamClick(currentParam)
        }
    }
}
