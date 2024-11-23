//package com.example.documentscanner.data
//
//import android.content.ContentResolver
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.widget.Toast
//import androidx.core.content.FileProvider
//import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
//import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
//import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
//import dagger.hilt.android.qualifiers.ApplicationContext
//import java.io.File
//import java.io.FileOutputStream
//import javax.inject.Inject
//
//class ScannerRepository @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val contentResolver: ContentResolver
//) {
//    private val scanner = GmsDocumentScanning.getClient(
//        GmsDocumentScannerOptions.Builder()
//            .setGalleryImportAllowed(true)
//            .setPageLimit(10)
//            .setResultFormats(
//                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
//                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
//            )
//            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
//            .build()
//    )
//
//    fun getScanner() = scanner
//
//    fun savePdfToPublic(uri: Uri, fileName: String): String? {
//        val downloadsDir = context.getExternalFilesDir(null)
//        if (downloadsDir == null || !downloadsDir.exists()) {
//            Toast.makeText(context, "Failed to access public directory.", Toast.LENGTH_LONG).show()
//            return null
//        }
//        val file = File(downloadsDir, fileName)
//        return try {
//            contentResolver.openInputStream(uri)?.use { input ->
//                FileOutputStream(file).use { output ->
//                    input.copyTo(output)
//                }
//            }
//            file.absolutePath
//        } catch (e: Exception) {
//            Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_LONG).show()
//            null
//        }
//    }
//
//    fun sharePdf(uri: Uri): Intent {
//        val file = File(context.filesDir, uri.lastPathSegment ?: "shared_file.pdf")
//        contentResolver.openInputStream(uri)?.use { input ->
//            FileOutputStream(file).use { output ->
//                input.copyTo(output)
//            }
//        }
//        val fileUri = FileProvider.getUriForFile(
//            context,
//            "${context.packageName}.fileprovider",
//            file
//        )
//
//        return Intent(Intent.ACTION_SEND).apply {
//            type = "application/pdf"
//            putExtra(Intent.EXTRA_STREAM, fileUri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//    }
//}
