package repository.room

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

val MIGRATION_1_2 = Migration(1, 2) { connection ->
    connection.execSQL(
        "DELETE FROM `DayCoffee` WHERE `id` NOT IN " +
            "(SELECT MIN(`id`) FROM `DayCoffee` GROUP BY `date`, `coffeeName`)"
    )
    connection.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_DayCoffee_date_coffeeName` ON `DayCoffee` (`date`, `coffeeName`)"
    )
}
