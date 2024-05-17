package com.yash.android.bnr.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController

class ConfirmDialogFragment : DialogFragment() {

    companion object {
        const val REQUEST_KEY_DELETE = "REQUEST_KEY_DELETE"
        const val BUNDLE_KEY_DELETE = "BUNDLE_KEY_DELETE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertBuilder = AlertDialog.Builder(requireContext())
        alertBuilder.setTitle("Delete Crime")
        alertBuilder.setMessage("Are you sure?")
        alertBuilder.setPositiveButton("Yes") { dialog, id ->
            findNavController().popBackStack()
            setFragmentResult(REQUEST_KEY_DELETE, bundleOf(BUNDLE_KEY_DELETE to true))
        }
        alertBuilder.setNegativeButton("No", null)
        return alertBuilder.create()
    }
}