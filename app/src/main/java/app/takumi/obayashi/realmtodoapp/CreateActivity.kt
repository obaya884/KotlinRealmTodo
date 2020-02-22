package app.takumi.obayashi.realmtodoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create.*
import java.util.*

class CreateActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        updateFloatingActionButton.setOnClickListener {
            createTask(
                titleEditText.text.toString(),
                contentEditText.text.toString()
            )
            finish()
        }
    }

    fun createTask(title: String, content: String) {
        realm.executeTransaction {
            val task = it.createObject(Task::class.java, UUID.randomUUID().toString())
            task.title = title
            task.content = content
        }
    }

}
