package com.example.metafade

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.metafade.ui.theme.MetaFadeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetaFadeTheme {
                var selectedImageUri by remember {
                    mutableStateOf<Uri?>(null)
                }
                var selectedImageUris by remember {
                    mutableStateOf<List<Uri>>(emptyList())
                }

                val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = {uri -> selectedImageUri = uri }
                )
                
                val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia(),
                    onResult = {uris -> selectedImageUris = uris}
                )

                // Changing composable for UI
                if(selectedImageUri == null && selectedImageUris == emptyList<Uri>())
                    MyApp(
                        modifier = Modifier.fillMaxSize(),
                        // Passing callbacks(functions) down for state hoisting
                        onSingleButtonClicked = { singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )},
                        onMultipleButtonClicked = {multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )}
                    )
                else
                    ShowSelectedImages(
                        modifier = Modifier.fillMaxSize(),
                        selectedImageUri = selectedImageUri,
                        selectedImageUris = selectedImageUris,
                        onClearButtonClicked = {
                            selectedImageUri = null
                            selectedImageUris = emptyList()
                        }
                    )
            }
        }
    }
}

// Complete UI of App
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    onSingleButtonClicked: ()->Unit,
    onMultipleButtonClicked: ()->Unit
    ) {
    Column(modifier
        .padding(horizontal = 2.dp, vertical = 2.dp)) {

        Text("MetaFade",
            fontSize = 40.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 60.dp)
        )

        Text("Select Image(s)",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 300.dp)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 7.dp)
        )

        // For buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onSingleButtonClicked) {
                Text(text = "Single")
            }

            Button(onClick = onMultipleButtonClicked) {
                Text(text = "Multiple")
            }
        }
    }
}

@Composable
fun ShowSelectedImages(
    modifier: Modifier = Modifier,
    onClearButtonClicked: ()->Unit,
    selectedImageUri: Uri?,
    selectedImageUris: List<Uri>
    ) {
    LazyColumn(modifier.padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        item {
            Button(onClick = onClearButtonClicked,
            modifier.padding(vertical = 5.dp)) {
                Text("Clear")
            }
        }

        item {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = null,
                modifier = Modifier.padding(3.dp),
                contentScale = ContentScale.Fit
            )
        }

        items(selectedImageUris) { uri->
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.padding(3.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ShowSelectedImagePreview() {
    MetaFadeTheme {
        ShowSelectedImages(
            onClearButtonClicked = {},
            selectedImageUri = null,
            selectedImageUris = emptyList()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MetaFadeTheme {
        MyApp(
            onSingleButtonClicked = {}, // empty function as this is preview
            onMultipleButtonClicked = {}
        )
    }
}