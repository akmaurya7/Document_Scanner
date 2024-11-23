//package com.example.documentscanner.ui
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.compose.setContent
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//import com.example.documentscanner.ui.theme.DocumentScannerTheme
//import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            DocumentScannerTheme {
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    // Initialize the ViewModel here
//                    val viewModel: ScannerViewModel = hiltViewModel()
//
//                    // Define the scanner launcher inside the composable function
//                    val scannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//                        if (result.resultCode == RESULT_OK) {
//                            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
//                            scanResult?.let {
//                                // Pass the result to viewModel
//                                viewModel.handleScanResult(it)
//                            }
//                        }
//                    }
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .verticalScroll(rememberScrollState()),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        // Display scanned images
//                        viewModel.imageUris.value.forEach { uri ->
//                            AsyncImage(
//                                model = uri,
//                                contentDescription = null,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//
//                        // Button to initiate scanning
//                        Button(onClick = { startScan(viewModel, scannerLauncher) }) {
//                            Text("Scan PDF")
//                        }
//
//                        // Button to save PDF
//                        Button(onClick = { viewModel.savePdfToPublic() }) {
//                            Text("Save to Public")
//                        }
//
//                        // Button to share PDF
//                        Button(onClick = {
//                            // Pass context to sharePdf method
//                            viewModel.sharePdf(this@MainActivity)
//                        }) {
//                            Text("Send PDF")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Function to initiate scanning, now accepting viewModel and scannerLauncher as parameters
//    private fun startScan(viewModel: ScannerViewModel, scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
//        viewModel.getScanner().getStartScanIntent(this)
//            .addOnSuccessListener { intentSender ->
//                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
