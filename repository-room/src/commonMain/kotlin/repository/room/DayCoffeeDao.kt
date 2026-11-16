package repository.room

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query

@Entity(indices = [Index(value = ["date", "coffeeName"], unique = true)])
data class DayCoffee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val coffeeName: String,
    val count: Int
)

@Dao
interface DayCoffeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(items: List<DayCoffee>)

    @Query("SELECT * FROM DayCoffee")
    suspend fun getAll(): List<DayCoffee>
}
