package com.yash.android.bnr.criminalintent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yash.android.bnr.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): Flow<Crime>
    @Update
    fun updateCrime(crime: Crime)
    @Insert
    suspend fun addCrime(crime: Crime)
    @Delete
    fun deleteCrime(crime:Crime)
}