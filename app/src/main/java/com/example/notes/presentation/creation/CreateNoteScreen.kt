@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.notes.presentation.creation

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.notes.domain.ContentItem
import com.example.notes.presentation.ui.theme.CustomIcons
import com.example.notes.presentation.utils.DataFormatter


@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewModel: CreationViewModel = hiltViewModel(),
    onFinished: () -> Unit,
){
    val state = viewModel.state.collectAsState()
    val currentState = state.value
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {url ->
            url?.let {
                viewModel.processCommand(CreateNoteCommand.AddImage(uri = it.toString()))
            }
        }
    )
    when(currentState){
        is CreateNoteState.Creation -> {
            LaunchedEffect(key1 = currentState.isSaveEnabled){
                Log.d("MainAc", "LOL")
            }
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Create Note",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            Icon(
                                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        },
                        actions = {
                            Icon(
                                modifier = Modifier.padding(end = 24.dp).clickable {
                                    imagePicker.launch("image/*")
                                },
                                imageVector = CustomIcons.Add_a_photo,
                                contentDescription = "Add Photo",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ){
                    TextField(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currentState.title,
                        onValueChange = {
                            viewModel.processCommand(CreateNoteCommand.InputTitle(it))
                        },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DataFormatter.formatCurrentDate(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Content(
                        modifier = Modifier.weight(1f),
                        content = currentState.content,
                        onValueChanged = { index, content ->
                            viewModel.processCommand(CreateNoteCommand.InputContent(content,index))
                        },
                        onDeleteChanged = {index ->
                            Log.d("Deleted","DELETE $index")
                            viewModel.processCommand(CreateNoteCommand.DeleteImage(index))
                        }
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        onClick = {
                            viewModel.processCommand(CreateNoteCommand.Save)
                        },
                        enabled = currentState.isSaveEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ){
                        Text(
                            text = "Save Note"
                        )
                    }
                }
            }
        }
        CreateNoteState.Finished -> {
            LaunchedEffect(key1 = Unit){
                onFinished()
            }
        }
    }

}
@Composable
fun Content(
    modifier : Modifier = Modifier,
    content: List<ContentItem>,
    onValueChanged: (Int, String) -> Unit,
    onDeleteChanged: (Int) -> Unit
){
    LazyColumn(
        modifier = modifier
    ) {
        content.forEachIndexed { index,contentItem ->
            item(key = index){
                when(contentItem){
                    is ContentItem.Image -> {
                        Log.d("Example", "$content")
                        val isLastImage = index > 0 && content[index-1] is ContentItem.Image
                        content.takeIf {
                            !isLastImage
                        }?.drop(index)
                            ?.takeWhile {
                                it is ContentItem.Image
                            }
                            ?.map {
                                (it as ContentItem.Image).url
                            }
                            ?.let { urls ->
                                ImageGroupContent(
                                    modifier = modifier,
                                    urls = urls,
                                    deleteImage = {imageIndex ->
                                        onDeleteChanged(index+imageIndex)
                                    }
                                )
                            }
                    }
                    is ContentItem.Text -> {
                        TextContent(
                            modifier = modifier,
                            value = contentItem.text,
                            onValueChanged = {
                                onValueChanged(index,it)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    uri: String,
    deleteImage: () -> Unit
){
    Box(
        modifier = modifier,

    ){
        AsyncImage(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            model = uri,
            contentDescription = "Image",
            contentScale = ContentScale.FillWidth
        )
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clickable {
                    deleteImage()
                }
            ,
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Image"
        )
    }
}
@Composable
fun ImageGroupContent(
    modifier: Modifier = Modifier,
    urls: List<String>,
    deleteImage: (Int) -> Unit
){
    Row {
        urls.forEachIndexed{ index, img ->
            ImageContent(
                modifier = modifier,
                uri = img,
                deleteImage = {
                    deleteImage(index)
                }
            )
        }
    }
}
@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit
){
    TextField(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 8.dp),
        value = value,
        onValueChange = onValueChanged,
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Note something aby dabi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}