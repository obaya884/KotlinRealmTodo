package app.takumi.obayashi.realmtodoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskList = readAll()

        val adapter = TaskAdapter(
            this,
            taskList,
            object : TaskAdapter.OnItemClickListener {
                override fun onItemClick(item: Task) {
                    val detail = Intent(applicationContext, DetailActivity::class.java)
                    detail.putExtra("taskId", item.id)
                    startActivity(detail)
                }
            },
            object : TaskAdapter.OnCheckboxChangeListener {
                override fun changeState(task: Task, state: Boolean) {
                    realm.executeTransaction {
                        task.state = state
                    }
                }
            },
            true
        )

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // FloatActionButtonの動作
        addFloatingActionButton.setOnClickListener {
            val create = Intent(this, CreateActivity::class.java)
            startActivityForResult(create, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // メニュー表示の為の関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        // メニューのリソース選択
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun readAll(): RealmResults<Task> {
        return realm.where<Task>().findAll().sort("createdAt", Sort.ASCENDING)
    }

}
