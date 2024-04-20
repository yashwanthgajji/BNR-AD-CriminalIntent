package com.yash.android.bnr.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yash.android.bnr.criminalintent.databinding.ListItemCrimeBinding

class CrimeListAdapter(private val crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun getItemCount(): Int  = crimes.size

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.apply {
            binding.listItemCrimeTitle.text = crime.title
            binding.listItemCrimeDate.text = crime.date.toString()
        }
    }
}

class CrimeHolder(val binding: ListItemCrimeBinding) : ViewHolder(binding.root) {

}