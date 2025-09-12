package com.example.civicsnap

import android.content.Context
import android.net.Uri
import android.view.KeyEvent.DispatcherState
import io.github.jan.supabase.postgrest.PostgrestClient
import io.github.jan.supabase.storage.StorageClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class ReportRepoBackend(
    supabaseUrl: String,
    supabaseKey: String,
    private val context: Context
) {
    private val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(PostgrestClient)
        install(StorageClient)
    }

    private val storage = supabase.storage
    private val database = supabase.postgrest

    suspend fun uploadImageToSupabase(
        imageUri: Uri,
        fileName: String,
        bucket: String = "report-images"
    ): String? {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(imageUri)?.readBytes()
        } ?: return null

        val response = storage.from(bucket).upload(fileName, bytes, upsert = true)
        return if (response.error == null) {
            storage.from(bucket).publicUrl(fileName)
        } else null
    }

    suspend fun saveReportToSupabase(report: com.example.civicsnap.models.ReportData): Boolean {
        val response = database["reports"].insert(report)
        return response.error == null
    }
}
