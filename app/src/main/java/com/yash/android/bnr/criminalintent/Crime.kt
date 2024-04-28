package com.yash.android.bnr.criminalintent

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime (
    @PrimaryKey val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean,
    @Ignore val requiresPolice: Boolean
) {
    constructor(id: UUID, title: String, date: Date, isSolved: Boolean) :
            this(id, title, date, isSolved, false)
}