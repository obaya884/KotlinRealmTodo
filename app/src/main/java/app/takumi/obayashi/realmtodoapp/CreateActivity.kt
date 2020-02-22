package app.takumi.obayashi.realmtodoapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_create.*
import java.util.*

class CreateActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val callingActivity = callingActivity?.className
        Log.d("TAG", callingActivity)

        if (callingActivity == "app.takumi.obayashi.realmtodoapp.DetailActivity") {
            val taskId = intent.getStringExtra("taskId")
            val task = realm.where<Task>().equalTo("id", taskId).findFirst()!!

            titleEditText.setText(task.title)
            contentEditText.setText(task.content)
        }

        updateFloatingActionButton.setOnClickListener {
            if (callingActivity == "app.takumi.obayashi.realmtodoapp.MainActivity") {
                createTask(
                    titleEditText.text.toString(),
                    contentEditText.text.toString()
                )
                finish()
            } else if (callingActivity == "app.takumi.obayashi.realmtodoapp.DetailActivity") {
                val taskId = intent.getStringExtra("taskId")
                updateTask(taskId, titleEditText.text.toString(), contentEditText.text.toString())
                finish()
            }
        }
    }

    fun createTask(title: String, content: String) {
        realm.executeTransaction {
            val task = it.createObject(Task::class.java, UUID.randomUUID().toString())
            task.title = title
            task.content = content
        }
    }

    fun updateTask(id: String, title: String, content: String) {
        realm.executeTransaction {
            val task = realm.where(Task::class.java).equalTo("id", id).findFirst()
                ?: return@executeTransaction
            task.title = title
            task.content = content
        }
    }

}
