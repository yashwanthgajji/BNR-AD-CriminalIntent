package com.yash.android.bnr.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yash.android.bnr.criminalintent.databinding.ListItemCrimeBinding
import com.yash.android.bnr.criminalintent.databinding.ListItemSeriousCrimeBinding
import java.util.UUID

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (crimeId: UUID) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == R.layout.list_item_crime) {
            val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
            CrimeHolder(binding)
        } else {
            val binding = ListItemSeriousCrimeBinding.inflate(inflater, parent, false)
            SeriousCrimeHolder(binding)
        }
    }

    override fun getItemCount(): Int  = crimes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crime = crimes[position]
        if (holder.itemViewType == R.layout.list_item_crime) {
            val crimeHolder: CrimeHolder = holder as CrimeHolder
            crimeHolder.bind(crime, onCrimeClicked)
        } else {
            val seriousCrimeHolder: SeriousCrimeHolder = holder as SeriousCrimeHolder
            seriousCrimeHolder.bind(crime, onCrimeClicked)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val crime = crimes[position]
        return if (crime.requiresPolice) {
            R.layout.list_item_serious_crime
        } else {
            R.layout.list_item_crime
        }
    }
}

class CrimeHolder(private val binding: ListItemCrimeBinding) : ViewHolder(binding.root) {
    fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
        binding.apply {
            listItemCrimeTitle.text = crime.title
            listItemCrimeDate.text = crime.date.toString()
            listItemCrimeSolved.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
            root.setOnClickListener {
                onCrimeClicked(crime.id)
            }
            root.contentDescription = if (crime.isSolved) {
                 root.context.getString(
                    R.string.crime_list_item_solved_description,
                    crime.title,
                    crime.date.toString()
                )
            } else {
                root.context.getString(
                    R.string.crime_list_item_unsolved_description,
                    crime.title,
                    crime.date.toString()
                )
            }
        }
    }
}

class SeriousCrimeHolder(private val binding: ListItemSeriousCrimeBinding) : ViewHolder(binding.root) {
    fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
        binding.apply {
            listItemCrimeTitle.text = crime.title
            listItemCrimeDate.text = crime.date.toString()
            if (crime.isSolved) {
                listItemCrimeSolved.visibility = View.VISIBLE
                listItemContactPoliceButton.isEnabled = false
            } else {
                listItemCrimeSolved.visibility = View.INVISIBLE
                listItemContactPoliceButton.isEnabled = true
            }
            root.setOnClickListener {
                onCrimeClicked(crime.id)
            }
            root.contentDescription = if (crime.isSolved) {
                root.context.getString(
                    R.string.crime_list_item_solved_description,
                    crime.title,
                    crime.date.toString()
                )
            } else {
                root.context.getString(
                    R.string.crime_list_item_unsolved_description,
                    crime.title,
                    crime.date.toString()
                )
            }
            listItemContactPoliceButton.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "Police on the way to address ${crime.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (crime.isSolved) {
                listItemContactPoliceButton.contentDescription = root.context.getString(
                    R.string.contact_police_button_solved_description,
                    crime.title
                )
            } else {
                listItemContactPoliceButton.contentDescription = root.context.getString(
                    R.string.contact_police_button_unsolved_description,
                    crime.title
                )
            }
        }
    }
}