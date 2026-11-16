package repository.room

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Update

@Entity
data class DayCoffee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val coffeeName: String,
    val count: Int
)

@Dao
interface DayCoffeeDao {
    @Insert
    suspend fun insertAll(items: List<DayCoffee>)

    @Update
    suspend fun updateAll(items: List<DayCoffee>)

    @Delete
    suspend fun deleteAll(items: List<DayCoffee>)

    @Query("SELECT * FROM DayCoffee ORDER BY id")
    suspend fun getAll(): List<DayCoffee>
}
