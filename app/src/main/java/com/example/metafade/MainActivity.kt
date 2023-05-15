package com.example.metafade

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { selectedImageUri = it }
                )

                val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenMultipleDocuments(),
                    onResult = { selectedImageUris = it }
                )

                // Changing composable for UI
                if(selectedImageUri == null && selectedImageUris == emptyList<Uri>())
                    MyApp(
                        modifier = Modifier.fillMaxSize(),
                        // Passing callbacks(functions) down for state hoisting
                        onSingleButtonClicked = { singlePhotoPickerLauncher.launch(arrayOf("image/*"))},
                        onMultipleButtonClicked = {multiplePhotoPickerLauncher.launch(arrayOf("image/*"))}
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

    var onProceedClicked by remember {
        mutableStateOf(false)
    }

    Column {
        Row(
            Modifier
                .padding(horizontal = 15.dp, vertical = 4.dp)
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            Button(onClick = onClearButtonClicked,
                modifier
                    .padding(vertical = 2.dp)
                    .weight(1f)) {
                Text("Clear")
            }

            val context = LocalContext.current
            Button(onClick = { onProceedClicked = true
                Toast.makeText(context, "The MetaData of Images is removed", Toast.LENGTH_SHORT).show()
                             },
                modifier
                    .padding(vertical = 2.dp)
                    .weight(1f)) {
                Text(text = "Proceed")
            }
        }

        LazyColumn(
            modifier.padding(vertical = 2.dp)
        ) {
            if(selectedImageUri != null)
                item {
                    ShowImageMeta(uri = selectedImageUri, onProceedClicked, onClearButtonClicked)
                }

            else
                items(selectedImageUris) { uri->
                    ShowImageMeta(uri = uri, onProceedClicked, onClearButtonClicked)
                }
        }
    }

}


@Composable
private fun ShowImageMeta(uri: Uri,
                          proceedSave: Boolean,
                          onClearButtonClicked: ()->Unit) {

    val parceableFileDes = LocalContext.current.applicationContext.contentResolver.openFileDescriptor(uri, "rw")
    val exif = ExifInterface(parceableFileDes?.fileDescriptor!!)

    var showMeta by rememberSaveable {
        mutableStateOf(false)
    }

//    var defaultConfigurationSave by rememberSaveable {
//        mutableStateOf(true)
//    }

    Card(modifier = Modifier.padding(horizontal = 1.dp, vertical = 10.dp)) {

        Spacer(Modifier.height(4.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.padding(3.dp),
                contentScale = ContentScale.Fit
            )

//            Button(onClick = { defaultConfigurationSave = !defaultConfigurationSave },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp)) {
//                Text(text = if (defaultConfigurationSave) "Save on Device" else "Save on Device & Cloud" )
//            }

            Button(onClick = { showMeta = !showMeta },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)) {
                Text(text = if(showMeta) "Hide MetaData" else "Show MetaData")
            }

            if (showMeta)
                Text(
                    text = showExif(exif),
                    modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.lightGreen))
                )

            Spacer(Modifier.height(4.dp))
        }
    }

    if (proceedSave) {
        defaultConfiguration(exif)
        parceableFileDes.close()
        onClearButtonClicked()
    }

    parceableFileDes.close()
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

    exif.saveAttributes()
}
private fun showExif(exif: ExifInterface) : String {

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