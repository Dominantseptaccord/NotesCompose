@file:OptIn(ExperimentalFoundationApi::class)

package com.example.notes.presentation.screens.notes

import android.R.attr.onClick
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.notes.R
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.Green
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.ui.theme.White
import com.example.notes.presentation.ui.theme.Yellow100
import com.example.notes.presentation.utils.DataFormatter

@Composable
fun NotesScreen (
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onAddClick: () -> Unit
){
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddClick()
                },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ){
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Note Add Floating Button",
                )
            }

        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(innerPadding)
        ) {
            item {
                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.all_notes)
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onQueryChange = {
                        viewModel.processCommand(NotesCommand.SearchNote(it))
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item{
                SubTitle(modifier = Modifier.padding(horizontal = 24.dp), text = stringResource(R.string.pinned))
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    itemsIndexed(items = state.pinnedNotes, key = {index, note -> note.id}){index, note ->
                        NoteCard(
                            modifier = Modifier.widthIn(max = 160.dp),
                            note = note, backgroundColo = PinnedNotesColors[index%PinnedNotesColors.size],
                            onNoteClick = onNoteClick,
                            onLongClick = {
                                viewModel.processCommand(notesCommand = NotesCommand.SwitchPinnedStatus(note.id))
                            },
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item{
                SubTitle(modifier = Modifier.padding(horizontal = 24.dp), text = stringResource(R.string.others))
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(items = state.otherNotes, key = {index, note-> note.id} ){ index, note ->
                val lastImage = note.content.filterIsInstance<ContentItem.Image>().map{it.url }.firstOrNull()
                if(lastImage!=null){
                    NoteCardImage(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        note = note,
                        backgroundColo = OtherNotesColors[index%OtherNotesColors.size],
                        onNoteClick = onNoteClick,
                        url = lastImage,
                        onLongClick = {
                            viewModel.processCommand(NotesCommand.SwitchPinnedStatus(note.id))
                        },
                    )
                }
                else{
                    NoteCard(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        note = note,
                        backgroundColo = OtherNotesColors[index%OtherNotesColors.size],
                        onNoteClick = onNoteClick,
                        onLongClick = {
                            viewModel.processCommand(NotesCommand.SwitchPinnedStatus(note.id))
                        },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}

@Composable
private fun Title(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String
){
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
){
    TextField(
        modifier = modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Notes",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun SubTitle(
    modifier: Modifier = Modifier,
    text: String
){
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}


@Composable
fun NoteCardImage(
    modifier: Modifier = Modifier,
    note: Note,
    url: String,
    backgroundColo: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
){
    Column(
        modifier = modifier.fillMaxSize().
        clip(RoundedCornerShape(12.dp))
            .background(backgroundColo)
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                }
            )
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).aspectRatio(2/1f),
                model = url,
                contentDescription = "Image",
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                ).padding(24.dp).align(Alignment.BottomStart)
            ) {
                Text(
                    text = note.title,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = White,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = DataFormatter.formatDateToString(note.updatedAt),
                    fontSize = 12.sp,
                    color = White,
                )
            }
        }
        note.content.filterIsInstance<ContentItem.Text>()
            .filter {
                it.text.isNotBlank()
            }
            .joinToString("\n") { it.text }.takeIf { it.isNotBlank() }?.let {
            Log.d("Exper", "${note.content}")
            Text(
                modifier = Modifier.padding(16.dp),
                text = it,
                maxLines = 3,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
        }


    }
}
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColo: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
){
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .fillMaxSize()
            .background(backgroundColo)
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                }
            )
            .padding(24.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = DataFormatter.formatDateToString(note.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(24.dp))
        note.content.filterIsInstance<ContentItem.Text>().joinToString("\n") { it.text }.let {
            Text(
                text = it,
                maxLines = 3,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

