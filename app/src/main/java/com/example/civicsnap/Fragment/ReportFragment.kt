package com.example.civicsnap.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.civicsnap.ReportRepoBackend
import com.example.civicsnap.databinding.FragmentReportBinding
import com.example.civicsnap.models.ReportData
import kotlinx.coroutines.launch
import okio.use
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var repository: ReportRepoBackend

    // Launchers for camera and gallery
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repository
        repository = ReportRepoBackend(
            supabaseUrl = "https://wghjijtaydqgxegjctse.supabase.co",
            supabaseKey = "YOUR_SUPABASE_ANON_KEY",
            context = requireContext()
        )

        // Register for camera result
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        selectedImageUri = saveBitmapToCache(it)
                        showToast("Photo captured.")
                    }
                }
            }

        // Register for gallery result
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    selectedImageUri = result.data?.data
                    if (selectedImageUri != null) {
                        showToast("Photo selected.")
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnTakePhoto.setOnClickListener { openCamera() }
        binding.btnUploadPhoto.setOnClickListener { openGallery() }
        binding.btnSubmit.setOnClickListener { submitReport() }
        binding.btnCancel.setOnClickListener { clearForm() }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        } else {
            showToast("No camera app found.")
        }
    }

    private fun openGallery() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickPhotoIntent)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(requireContext().cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "img_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
            }
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun submitReport() {
        val title = binding.issueET.text.toString().trim()
        val category = binding.categoryET.text.toString().trim()
        val priority = binding.priorityList.text.toString().trim()
        val description = binding.descriptionET.text.toString().trim()
        val location = binding.locationTextET.text.toString().trim()
        val userId = "anonymous"

        if (title.isEmpty() || category.isEmpty() || priority.isEmpty() ||
            description.isEmpty() || location.isEmpty()
        ) {
            showToast("Please fill all mandatory fields.")
            return
        }

        lifecycleScope.launch {
            val photoUrl = selectedImageUri?.let {
                repository.uploadImageToSupabase(it, "img_${UUID.randomUUID()}.jpg")
            } ?: ""

            val report = ReportData(
                title = title,
                category = category,
                priority = priority,
                description = description,
                location = location,
                photoUrl = photoUrl,
                timestamp = System.currentTimeMillis(),
                userId = userId
            )

            val success = repository.saveReportToSupabase(report)
            if (success) {
                showToast("Report submitted successfully.")
                clearForm()
            } else {
                showToast("Failed to submit report. Please try again.")
            }
        }
    }

    private fun clearForm() {
        binding.issueET.text.clear()
        binding.categoryET.text.clear()
        binding.priorityList.setText("")
        binding.descriptionET.text.clear()
        binding.locationTextET.text.clear()
        selectedImageUri = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
