package com.example.notes.presentation.screens.notes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNoteUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import com.example.notes.domain.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val addNoteUseCase : AddNoteUseCase,
    private val getAllNoteUseCase : GetAllNotesUseCase,
    private val searchNoteUseCase : SearchNoteUseCase,
    private val switchNotesUseCase : SwitchPinnedStatusUseCase
) : ViewModel() {

    val query = MutableStateFlow("")
    val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()
    init {
        query.onEach {text ->
            _state.update {
                it.copy(query = text)
            }
        }.
        flatMapLatest {
            if(it.isBlank()){
                getAllNoteUseCase()
            }
            else{
                searchNoteUseCase(it)
            }
        }.onEach {notes ->
            val pinnedNotes = notes.filter { it.isPinned }
            val otherNotes = notes.filter {!it.isPinned}
            _state.update {
                it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes)
            }
            }.
        launchIn(viewModelScope)
    }
    fun processCommand(notesCommand: NotesCommand){
        viewModelScope.launch {
            when(notesCommand){
                is NotesCommand.AddNote -> {
                    val noteContent = ContentItem.Text(notesCommand.content)
                    addNoteUseCase(notesCommand.title,listOf(noteContent))
                }
                is NotesCommand.SearchNote -> {
                    query.update {
                        notesCommand.query.trim()
                    }
                }
                is NotesCommand.SwitchPinnedStatus -> {
                    switchNotesUseCase(notesCommand.noteId)
                }
            }
        }
    }

}
sealed interface NotesCommand{
    data class AddNote(val title: String, val content: String) : NotesCommand
    data class SearchNote(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

}


data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)