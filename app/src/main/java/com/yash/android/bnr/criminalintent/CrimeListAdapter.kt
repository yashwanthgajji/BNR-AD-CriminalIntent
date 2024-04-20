package com.yash.android.bnr.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yash.android.bnr.criminalintent.databinding.ListItemCrimeBinding
import com.yash.android.bnr.criminalintent.databinding.ListItemSeriousCrimeBinding

class CrimeListAdapter(private val crimes: List<Crime>) : RecyclerView.Adapter<ViewHolder>() {
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
            crimeHolder.bind(crime)
        } else {
            val seriousCrimeHolder: SeriousCrimeHolder = holder as SeriousCrimeHolder
            seriousCrimeHolder.bind(crime)
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
    fun bind(crime: Crime) {
        binding.apply {
            listItemCrimeTitle.text = crime.title
            listItemCrimeDate.text = crime.date.toString()
            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crime.title} clicked!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class SeriousCrimeHolder(private val binding: ListItemSeriousCrimeBinding) : ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            listItemCrimeTitle.text = crime.title
            listItemCrimeDate.text = crime.date.toString()
            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crime.title} clicked!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            listItemContactPoliceButton.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "Police on the way to address ${crime.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}