package com.example.metafade

import android.annotation.SuppressLint
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
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
                        onMultipleButtonClicked = { multiplePhotoPickerLauncher.launch(
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
                .padding(horizontal = 5.dp, vertical = 8.dp),
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

        if(selectedImageUri != null)
            item {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.padding(3.dp),
                    contentScale = ContentScale.Fit
                )

                val uriPath = selectedImageUri.path
                val exif = ExifInterface(uriPath!!)

                Text(
                    text = showExif(exif),
                    modifier = Modifier.padding(3.dp)
                )
            }

        else
            items(selectedImageUris) { uri->
                ShowImageMeta(uri = uri)
            }
    }
}

@SuppressLint("Recycle")
@Composable
private fun ShowImageMeta(uri: Uri) {
    
    var showMeta by rememberSaveable() {
        mutableStateOf(false)
    }

    var defaultConfigurationSave by rememberSaveable() {
        mutableStateOf(true)
    }

    val exif = ExifInterface(LocalContext.current.applicationContext.contentResolver.openFileDescriptor(uri, "rw")?.fileDescriptor!!)

    AsyncImage(
        model = uri,
        contentDescription = null,
        modifier = Modifier.padding(3.dp),
        contentScale = ContentScale.Fit
    )

    if (showMeta)
        Text(
            text = showExif(exif),
            modifier = Modifier.padding(3.dp)
        )
    else
        Button(onClick = { showMeta = true}) {
            Text(text = "Show MetaData")
        }

    Button(onClick = {defaultConfigurationSave = !defaultConfigurationSave}) {
        Text(text = if (defaultConfigurationSave) "Save on Device" else "Save on Device & Cloud" )
    }
}

private fun defaultConfiguration(exif: ExifInterface) {
    exif.setAttribute(ExifInterface.TAG_ARTIST, null)
    exif.setAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME, null)
    exif.setAttribute(ExifInterface.TAG_COPYRIGHT, null)
    exif.setAttribute(ExifInterface.TAG_EXIF_VERSION, null)
    exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, null)
    exif.setAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID, null)
    exif.setAttribute(ExifInterface.TAG_LENS_MAKE, null)
    exif.setAttribute(ExifInterface.TAG_LENS_MODEL, null)
    exif.setAttribute(ExifInterface.TAG_LENS_SERIAL_NUMBER, null)
    exif.setAttribute(ExifInterface.TAG_LENS_SPECIFICATION, null)
    exif.setAttribute(ExifInterface.TAG_MAKE, null)
    exif.setAttribute(ExifInterface.TAG_MAKER_NOTE, null)
    exif.setAttribute(ExifInterface.TAG_MODEL, null)
    exif.setAttribute(ExifInterface.TAG_SOFTWARE, null)
    exif.setAttribute(ExifInterface.TAG_USER_COMMENT, null)
    exif.setAttribute(ExifInterface.TAG_BODY_SERIAL_NUMBER, null)
    exif.setAttribute(ExifInterface.TAG_DATETIME, null)
    exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, null)
    exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, null)
    exif.setAttribute(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION, null)
    exif.setAttribute(ExifInterface.TAG_RELATED_SOUND_FILE, null)
    exif.setLatLong(0.0, 0.0)
    exif.setGpsInfo(null)
}
private fun showExif(exif: ExifInterface) : String {
    exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "Good Image")

    val metadataString = StringBuilder()
    metadataString.append("Latitude Longitude : ").append(exif.latLong).append(" \n")
    metadataString.append("Image Length : ").append(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)).append(" \n")
    metadataString.append("Image Width : ").append(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)).append(" \n")
    metadataString.append("Image Description : ").append(exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)).append(" \n")
    metadataString.append("Image Length : ").append(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)).append(" \n")
    metadataString.append("Image DateTime : ").append(exif.getAttribute(ExifInterface.TAG_DATETIME)).append(" \n")
    metadataString.append("Image Model : ").append(exif.getAttribute(ExifInterface.TAG_MODEL)).append(" \n")
    metadataString.append("Camera Owner  : ").append(exif.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)).append(" \n")
    metadataString.append("Image CopyRight : ").append(exif.getAttribute(ExifInterface.TAG_COPYRIGHT)).append(" \n")
    metadataString.append("Exif Version : ").append(exif.getAttribute(ExifInterface.TAG_EXIF_VERSION)).append(" \n")
    metadataString.append("Flash : ").append(exif.getAttribute(ExifInterface.TAG_FLASH)).append(" \n")
    metadataString.append("File Source : ").append(exif.getAttribute(ExifInterface.TAG_FILE_SOURCE)).append(" \n")

//    exif.saveAttributes()
    return metadataString.toString()
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