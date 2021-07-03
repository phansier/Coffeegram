package ru.beryukhov.repository.model

import io.realm.RealmObject

class DayCoffee: RealmObject {
    var coffeeName: String = ""
    var count: Int = 0
}