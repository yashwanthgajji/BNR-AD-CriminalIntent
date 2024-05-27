package com.yash.android.bnr.criminalintent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yash.android.bnr.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

private const val DATE_FORMAT = "EEEE, MMMM dd, yyyy"
private const val TIME_FORMAT = "hh:mm a"

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }
    private var photoName: String? = null
    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri -> uri?.let { parseContactSelection(it) } }
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        if (didTakePhoto && photoName != null) {
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    private fun parseContactSelection(contactsUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor =
            requireActivity().contentResolver.query(contactsUri, queryFields, null, null, null)
        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
//        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.crimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = PictureUtils.getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.crimePhoto.apply {
                        setImageBitmap(scaledBitmap)
                        tag = photoFileName
                        contentDescription = getString(R.string.crime_photo_image_description)
                    }
                }
            } else {
                binding.crimePhoto.apply {
                    setImageBitmap(null)
                    tag = null
                    contentDescription = getString(R.string.crime_photo_no_image_description)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback {
            if (crimeDetailViewModel.crime.value?.title?.isBlank() == true) {
                binding.crimeTitle.hint = "Add crime title!!!"
                binding.crimeTitle.setHintTextColor(Color.RED)
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
            crimeSerious.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(requiresPolice = isChecked)
                }
            }
            crimeSuspect.setOnClickListener { selectSuspect.launch(null) }
            val selectSuspectIntent = selectSuspect.contract.createIntent(requireContext(), null)
            crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)
            crimeCamera.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(
                    requireContext().applicationContext.filesDir,
                    photoName
                )
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.yash.android.bnr.criminalintent.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }
            val captureImageIntent = takePhoto.contract.createIntent(requireContext(), Uri.EMPTY)
            crimeCamera.isEnabled = canResolveIntent(captureImageIntent)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }
        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
        setFragmentResultListener(TimePickerFragment.REQUEST_KEY_TIME) { _, bundle ->
            val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
        setFragmentResultListener(ConfirmDialogFragment.REQUEST_KEY_DELETE) { _, bundle ->
            if (bundle.getBoolean(ConfirmDialogFragment.BUNDLE_KEY_DELETE)) {
                crimeDetailViewModel.deleteCrime()
                findNavController().popBackStack()
            }
        }
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = DateFormat.format(DATE_FORMAT, crime.date)
            crimeDate.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
            }
            crimeTime.text = DateFormat.format(TIME_FORMAT, crime.date)
            crimeTime.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectTime(crime.date))
            }
            crimeSolved.isChecked = crime.isSolved
            crimeSerious.isChecked = crime.requiresPolice
            crimeSuspect.text = crime.suspect.ifEmpty { getString(R.string.crime_suspect_text) }
            updatePhoto(crime.photoFileName)
            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject)
                    )
                }
                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                findNavController().navigate(CrimeDetailFragmentDirections.confirmDeletePopup())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val timeString = DateFormat.format(TIME_FORMAT, crime.date).toString()
        val solvedString = when (crime.isSolved) {
            true -> getString(R.string.crime_report_solved)
            false -> getString(R.string.crime_report_unsolved)
        }
        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        val seriousString = if (crime.requiresPolice) {
            getString(R.string.crime_report_serious)
        } else {
            getString(R.string.crime_report_non_serious)
        }
        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            timeString,
            solvedString,
            suspectString,
            seriousString
        )
    }
}