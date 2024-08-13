package com.example.criminalmove

import android.app.DatePickerDialog
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.criminalmove.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_CRIME_ID = "crime_id"
private const val DATE_FORMAT = "EEEE, MMM dd, yyyy"
private const val REQUEST_CONTACT = 1
private const val REQUEST_READ_CONTACTS = 2

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callSuspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: String? = arguments?.getString(ARG_CRIME_ID)
        if (crimeId != null) {
            fetchCrimeFromFirestore(crimeId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        reportButton = view.findViewById(R.id.crime_report)
        suspectButton = view.findViewById(R.id.crime_suspect)
        photoButton = view.findViewById(R.id.crime_camera)
        photoView = view.findViewById(R.id.crime_photo)
        callSuspectButton = view.findViewById(R.id.crime_call_suspect)

        dateButton.apply {
            text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(crime.date)
            setOnClickListener { showDatePickerDialog() }
        }

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.setOnClickListener {
            requestContactsPermission()
        }

        callSuspectButton.setOnClickListener {
            callSuspect()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolvedActivity == null) {
            suspectButton.isEnabled = false
        }

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {}
        }
        titleField.addTextChangedListener(titleWatcher)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.time = crime.date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val newDate = Calendar.getInstance()
                newDate.set(selectedYear, selectedMonth, selectedDay)
                crime.date = newDate.time
                dateButton.text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(crime.date)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            pickContact()
        }
    }

    private fun pickContact() {
        val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(pickContactIntent, REQUEST_CONTACT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    pickContact()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = requireActivity().contentResolver.query(contactUri!!, queryFields, null, null, null)
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    suspectButton.text = suspect
                    // Сохранение ID контакта для последующего использования
                    crime.contactId = contactUri.lastPathSegment ?: ""
                }
            }
        }
    }

    private fun callSuspect() {
        if (crime.contactId.isNotEmpty()) {
            val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val queryFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val whereClause = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
            val whereArgs = arrayOf(crime.contactId)
            val cursor = requireActivity().contentResolver.query(phoneUri, queryFields, whereClause, whereArgs, null)
            cursor?.use {
                if (it.count == 0) {
                    return
                }
                it.moveToFirst()
                val phoneNumber = it.getString(0)
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(callIntent)
            }
        }
    }

    private fun fetchCrimeFromFirestore(crimeId: String) {
        db.collection("Crimes").document(crimeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    crime = document.toObject(Crime::class.java)!!
                    crime.id = document.id
                    updateUI()
                }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(crime.date)
        solvedCheckBox.isChecked = crime.isSolved
        suspectButton.text = crime.suspect ?: getString(R.string.crime_suspect_text)
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(crime.date)
        val suspect = crime.suspect?.let {
            getString(R.string.crime_report_suspect, it)
        } ?: getString(R.string.crime_report_no_suspect)

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    companion object {
        fun newInstance(crimeId: String): CrimeFragment {
            val args = Bundle().apply {
                putString(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}
