package app.takumi.obayashi.realmtodoapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Task(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var state: Boolean = false,
    open var title: String = "",
    open var content: String = "",
    open var createdAt: Date = Date(System.currentTimeMillis())
) : RealmObject()