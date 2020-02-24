package app.takumi.obayashi.realmtodoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

    var taskList: RealmResults<Task>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskList = readAllTasks()
        setUpRecyclerView()

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

    // メニューアイテム選択時の動作
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_select_all -> {
                taskList = readAllTasks()
                setUpRecyclerView()
            }
            R.id.main_menu_select_active -> {
                taskList = readActiveTasks()
                setUpRecyclerView()
            }
            R.id.main_menu_select_archived -> {
                taskList = readArchivedTasks()
                setUpRecyclerView()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    fun readAllTasks(): RealmResults<Task> {
        return realm.where<Task>().findAll().sort("createdAt", Sort.ASCENDING)
    }

    fun readActiveTasks(): RealmResults<Task> {
        return realm.where<Task>().equalTo("state", false).findAll()
            .sort("createdAt", Sort.ASCENDING)
    }

    fun readArchivedTasks(): RealmResults<Task> {
        return realm.where<Task>().equalTo("state", true).findAll()
            .sort("createdAt", Sort.ASCENDING)
    }

    fun setUpRecyclerView() {
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
                        if (task.state != state) {
                            task.state = state
                        }
                        Log.d("state", "${task.state}")
                    }
                }
            },
            true
        )

        recyclerView.adapter = adapter

    }
}
