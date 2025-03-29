package repository.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

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
    suspend fun insert(item: DayCoffee)

    @Update
    suspend fun update(item: DayCoffee)

    @Query("SELECT * FROM DayCoffee")
    suspend fun getAll(): List<DayCoffee>
}
