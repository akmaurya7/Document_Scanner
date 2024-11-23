package com.example.documentscanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.documentscanner.ui.theme.DocumentScannerTheme
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure the Document Scanner
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(10)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        setContent {
            DocumentScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
                    var pdfUri by remember { mutableStateOf<Uri?>(null) }


                    val scannerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                                imageUris = scanResult?.pages?.map { it.imageUri } ?: emptyList()

                                scanResult?.pdf?.let { pdf ->
                                    pdfUri = pdf.uri

                                    val internalPdfFile = File(filesDir, "scan_internal.pdf")
                                    try {
                                        contentResolver.openInputStream(pdf.uri)?.use { inputStream ->
                                            FileOutputStream(internalPdfFile).use { fos ->
                                                inputStream.copyTo(fos)
                                            }
                                        }
                                        Toast.makeText(
                                            this@MainActivity,
                                            "PDF saved internally.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to save internal PDF: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        imageUris.forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.padding(16.dp)
                            )
                        }


                        Button(
                            onClick = {
                                scanner.getStartScanIntent(this@MainActivity)
                                    .addOnSuccessListener { intentSender ->
                                        scannerLauncher.launch(
                                            IntentSenderRequest.Builder(intentSender).build()
                                        )
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        ) {
                            Text(text = "Scan PDF")
                        }


                        var latestPdfPath by remember { mutableStateOf<String?>(null) }


                        Button(
                            onClick = {
                                pdfUri?.let { uri ->
                                    try {

                                        val downloadsDir = getExternalFilesDir(null)
                                        if (downloadsDir != null && downloadsDir.exists()) {

                                            val uniqueFileName = "scan_${System.currentTimeMillis()}.pdf"
                                            val publicPdfFile = File(downloadsDir, uniqueFileName)


                                            contentResolver.openInputStream(uri)?.use { inputStream ->
                                                FileOutputStream(publicPdfFile).use { fos ->
                                                    inputStream.copyTo(fos)
                                                }
                                            }


                                            latestPdfPath = publicPdfFile.absolutePath

                                            Toast.makeText(
                                                this@MainActivity,
                                                "PDF saved to ${publicPdfFile.absolutePath}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Failed to access public directory.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to save PDF: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } ?: run {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "No PDF to save. Scan first!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Save to Public")
                        }


                        Button(
                            onClick = {
                                latestPdfPath?.let { path ->
                                    val file = File(path)
                                    if (file.exists()) {
                                        val fileUri: Uri = FileProvider.getUriForFile(
                                            this@MainActivity,
                                            "${packageName}.fileprovider",
                                            file
                                        )
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, fileUri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        startActivity(Intent.createChooser(shareIntent, "Send PDF"))
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "PDF not found. Save it first!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } ?: run {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "No PDF to send. Save it first!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Send PDF")
                        }


                    }
                }
            }
        }
    }
}