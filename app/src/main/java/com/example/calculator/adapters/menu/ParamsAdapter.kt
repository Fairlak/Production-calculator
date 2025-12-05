// File: ParamsAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R

// 1. Определяем интерфейс для обработки кликов
interface OnParamClickListener {
    fun onParamClick(param: String)
}

class ParamsAdapter(
    private val paramsList: List<String>,
    private val listener: OnParamClickListener // 2. Передаем listener в конструктор
) : RecyclerView.Adapter<ParamsAdapter.ParamViewHolder>() {

    // Этот класс описывает View одного элемента списка
    class ParamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.param_text)
    }

    // Создает новый ViewHolder (вызывается для первых элементов на экране)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.param_item, parent, false)
        return ParamViewHolder(view)
    }

    // Возвращает общее количество элементов в списке
    override fun getItemCount(): Int = paramsList.size

    // Заполняет ViewHolder данными для конкретной позиции
    override fun onBindViewHolder(holder: ParamViewHolder, position: Int) {
        val currentParam = paramsList[position]
        holder.textView.text = currentParam

        // 3. Устанавливаем слушатель кликов на элемент
        holder.itemView.setOnClickListener {
            listener.onParamClick(currentParam)
        }
    }
}
