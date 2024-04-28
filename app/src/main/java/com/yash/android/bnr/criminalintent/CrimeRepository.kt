package com.yash.android.bnr.criminalintent

import android.content.Context
import androidx.room.Room
import com.yash.android.bnr.criminalintent.database.CrimeDatabase
import java.util.UUID

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(context: Context){

    private val crimeDatabase: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    suspend fun getCrimes(): List<Crime> = crimeDatabase.crimeDao().getCrimes()

    suspend fun getCrime(crimeId: UUID): Crime = crimeDatabase.crimeDao().getCrime(crimeId)

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