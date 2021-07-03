package ru.beryukhov.repository.model

import io.realm.RealmObject

class DateCoffees: RealmObject {
    var date: String = ""
    var dayCoffees: List<DayCoffee> = emptyList()
}

