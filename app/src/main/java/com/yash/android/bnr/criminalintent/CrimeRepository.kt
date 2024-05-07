package com.yash.android.bnr.criminalintent

import android.content.Context
import androidx.room.Room
import com.yash.android.bnr.criminalintent.database.CrimeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
){

    private val crimeDatabase: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    fun getCrimes(): Flow<List<Crime>> = crimeDatabase.crimeDao().getCrimes()

    fun getCrime(crimeId: UUID): Flow<Crime> = crimeDatabase.crimeDao().getCrime(crimeId)

    fun updateCrime(crime: Crime) {
        coroutineScope.launch {
            crimeDatabase.crimeDao().updateCrime(crime)
        }
    }

    suspend fun addCrime(crime: Crime) {
        crimeDatabase.crimeDao().addCrime(crime)
    }

    companion object {
        private var INSTANCE: CrimeRepository ?= null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized!")
        }
    }
}