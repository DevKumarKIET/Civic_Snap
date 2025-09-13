package com.example.civicsnap.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.civicsnap.databinding.FragmentReportBinding
import com.google.android.gms.common.util.CollectionUtils.listOf

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityResultLaunchers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        setupPriorityDropdown()
        setupListeners()
        return binding.root
    }

    private fun setupPriorityDropdown() {
        val priorities = listOf("High – Urgent issue", "Medium – Moderate issue", "Low – Minor issue")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorities)
        binding.priorityList.setAdapter(adapter)
        binding.priorityList.setText(priorities[1], false)
    }

    private fun setupListeners() {
        binding.btnTakePhoto.setOnClickListener { openCamera() }
        binding.btnUploadPhoto.setOnClickListener { openGallery() }
        binding.btnSubmit.setOnClickListener { validateAndSubmit() }
        binding.btnCancel.setOnClickListener { clearForm() }
        binding.backBtnReport.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    showToast("Photo captured (not stored)")
                }
            } else {
                showToast("Camera cancelled")
            }
        }
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    showToast("Photo selected (not stored)")
                }
            } else {
                showToast("Gallery cancelled")
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(cameraIntent)
        } else {
            showToast("No camera app available")
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun validateAndSubmit() {
        val title = binding.issueET.text.toString().trim()
        val category = binding.categoryET.text.toString().trim()
        val priority = binding.priorityList.text.toString().trim()
        val description = binding.descriptionET.text.toString().trim()
        val location = binding.locationTextET.text.toString().trim()

        if (title.isEmpty()) {
            showToast("Please enter the issue title")
            return
        }
        if (category.isEmpty()) {
            showToast("Please enter the issue category")
            return
        }
        if (priority.isEmpty()) {
            showToast("Please select priority")
            return
        }
        if (description.isEmpty()) {
            showToast("Please enter issue description")
            return
        }
        if (location.isEmpty()) {
            showToast("Please enter location details")
            return
        }
        showToast("Input is valid. Submit action skipped since no backend.")
    }

    private fun clearForm() {
        binding.issueET.text.clear()
        binding.categoryET.text.clear()
        binding.priorityList.setText("")
        binding.descriptionET.text.clear()
        binding.locationTextET.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
