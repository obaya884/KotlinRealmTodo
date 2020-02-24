package app.takumi.obayashi.realmtodoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private var taskId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // intentの情報取得
        taskId = intent.getStringExtra("taskId")

        val task = realm.where<Task>().equalTo("id", taskId).findFirst()!!

        titleText.text = task.title
        contentText.text = task.content

        editFloatingActionButton.setOnClickListener {
            val create = Intent(this, CreateActivity::class.java)
            create.putExtra("taskId", taskId)
            startActivityForResult(create, 1)
        }

        // 戻るボタンつける
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            finish()
        }
    }

    // メニュー表示の為の関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        // メニューのリソース選択
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    // メニューアイテム選択時の動作
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.detail_menu_delete -> {
                AlertDialog.Builder(this)
                    .setTitle("削除")
                    .setMessage("${titleText.text}を削除しますか？")
                    .setPositiveButton("OK", { dialog, which ->
                        delete(taskId)
                    })
                    .setNegativeButton("Cancel", { dialog, which ->
                    })
                    .show()
            }
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun delete(id: String) {
        realm.executeTransaction {
            val task = realm.where(Task::class.java).equalTo("id", id).findFirst()
                ?: return@executeTransaction
            task.deleteFromRealm()
        }
        finish()
    }

}
