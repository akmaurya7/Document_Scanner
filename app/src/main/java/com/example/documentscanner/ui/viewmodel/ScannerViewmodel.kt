//package com.example.documentscanner.ui
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.runtime.mutableStateOf
//import androidx.core.content.FileProvider
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.documentscanner.data.ScannerRepository
//import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import java.io.File
//import javax.inject.Inject
//
//@HiltViewModel
//class ScannerViewModel @Inject constructor(
//    private val scannerRepository: ScannerRepository
//) : ViewModel() {
//
//    // State to store image URIs and the PDF URI
//    var imageUris = mutableStateOf<List<Uri>>(emptyList())
//    var pdfUri = mutableStateOf<Uri?>(null)
//    var latestPdfPath = mutableStateOf<String?>(null)
//
//    // Function to get the scanner from the repository
//    fun getScanner() = scannerRepository.getScanner()
//
//    // Save the PDF to a public location
//    fun savePdfToPublic() {
//        pdfUri.value?.let { uri ->
//            val fileName = "scan_${System.currentTimeMillis()}.pdf"
//            // Assuming savePdfToPublic in the repository saves the PDF and returns its path
//            latestPdfPath.value = scannerRepository.savePdfToPublic(uri, fileName)
//        }
//    }
//
//    // Function to share the PDF via an Intent
//    fun sharePdf(context: Context) {
//        latestPdfPath.value?.let { path ->
//            // Convert the path to a File
//            val file = File(path)
//
//            // Check if the file exists before sharing
//            if (file.exists()) {
//                // Get a content URI using FileProvider
//                val contentUri: Uri = FileProvider.getUriForFile(
//                    context,
//                    "com.example.documentscanner.provider",  // Make sure this matches the authority in AndroidManifest
//                    file
//                )
//
//                // Create an intent to share the PDF
//                val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                    putExtra(Intent.EXTRA_STREAM, contentUri) // Pass the content URI
//                    type = "application/pdf"  // Set the type as PDF
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to read the URI
//                }
//
//                // Start the activity to share the PDF
//                context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
//            } else {
//                // Handle file not existing (could show a toast or log an error)
//                // For now, let's just print a log message
//                println("File does not exist: $path")
//            }
//        }
//    }
//
//    // Handle the scanning result and update image URIs and PDF URI
//    fun handleScanResult(scanResult: GmsDocumentScanningResult) {
//        // Update image URIs
//        scanResult.pages?.let { pages ->
//            imageUris.value = pages.map { it.imageUri }
//        }
//
//        // Update the PDF URI
//        scanResult.pdf?.let { pdf ->
//            pdfUri.value = pdf.uri
//        }
//    }
//}
