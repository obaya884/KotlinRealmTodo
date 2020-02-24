package app.takumi.obayashi.realmtodoapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
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

        // 戻るボタンつける
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val callingActivity = callingActivity?.className
        Log.d("TAG", callingActivity)

        if (callingActivity == "app.takumi.obayashi.realmtodoapp.DetailActivity") {
            val taskId = intent.getStringExtra("taskId")
            val task = realm.where<Task>().equalTo("id", taskId).findFirst()!!

            titleEditText.setText(task.title)
            contentEditText.setText(task.content)
        }

        updateFloatingActionButton.setOnClickListener {
            if (titleEditText.text.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Lack of Information")
                    .setMessage("please input title")
                    .setPositiveButton("OK", { dialog, which ->
                    })
                    .show()
            } else {
                if (callingActivity == "app.takumi.obayashi.realmtodoapp.MainActivity") {
                    createTask(
                        titleEditText.text.toString(),
                        contentEditText.text.toString()
                    )
                    finish()
                } else if (callingActivity == "app.takumi.obayashi.realmtodoapp.DetailActivity") {
                    val taskId = intent.getStringExtra("taskId")
                    updateTask(
                        taskId,
                        titleEditText.text.toString(),
                        contentEditText.text.toString()
                    )
                    finish()
                }
            }
        }
    }

    // メニューアイテム選択時の動作
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
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
