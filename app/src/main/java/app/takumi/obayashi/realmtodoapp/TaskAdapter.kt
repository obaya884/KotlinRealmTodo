package app.takumi.obayashi.realmtodoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val context: Context,
    private var taskList: OrderedRealmCollection<Task>?,
    private var listener: OnItemClickListener,
    private var checkBoxListener: OnCheckedChangeListener,
    private val autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Task, TaskAdapter.TaskViewHolder>(taskList, autoUpdate) {

    override fun getItemCount(): Int = taskList?.size ?: 0

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task: Task = taskList?.get(position) ?: return

        holder.titleTextView.text = task.content
        holder.dateTextView.text =
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(task.createdAt)

        holder.checkBox.isChecked = task.state

        holder.container.setOnClickListener {
            listener.onItemClick(task)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkBoxListener.changeState(task, isChecked)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return TaskViewHolder(v)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.container
        val titleTextView: TextView = view.titleTextView
        val dateTextView: TextView = view.dateTextView
        val checkBox: MaterialCheckBox = view.checkBox
    }

    interface OnItemClickListener {
        fun onItemClick(item: Task)
    }

    interface OnCheckedChangeListener {
        fun changeState(task: Task, state: Boolean)
    }

}