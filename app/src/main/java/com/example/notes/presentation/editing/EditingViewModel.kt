package com.example.notes.presentation.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import com.example.notes.domain.ContentItem.*
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.UpdateNoteUseCase
import com.example.notes.presentation.creation.CreateNoteCommand
import com.example.notes.presentation.creation.CreateNoteState.Creation
import com.example.notes.presentation.editing.EditNoteState.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = EditingViewModel.Factory::class)
class EditingViewModel @AssistedInject constructor(
    private val getNoteUseCase : GetNoteUseCase,
    private val deleteNoteUseCase : DeleteNoteUseCase,
    private val editNoteUseCase : UpdateNoteUseCase,
    @Assisted("noteId") private val noteId: Int,
) : ViewModel() {
    val _state = MutableStateFlow<EditNoteState>(Initial)
    val state = _state.asStateFlow()
    init{
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                Editing(note)
            }
        }
    }
    fun processCommand(command: EditNoteCommand){
        when(command){
            EditNoteCommand.Back -> {
                _state.update {
                    Finished
                }
            }
            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is Editing){
                        val newNote = previousState.note.content.mapIndexed { index, item ->
                            if(index==command.index && item is ContentItem.Text){
                                item.copy(text = command.content)
                            }
                            else{
                                item
                            }
                        }
                        previousState.copy(note = previousState.note.copy(content = newNote))
                    }
                    else{
                        previousState
                    }
                }
            }
            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if(previousState is Editing){
                        previousState.copy(
                            note = previousState.note.copy(title = command.title)
                        )
                    }
                    else{
                        previousState
                    }
                }
            }
            // ТУТ Я САМ СДЕЛАЛ ТУТ МОЖЕТ БЫТЬ ОШИБКА
            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is Editing) {
                            val contentFilter = previousState.note.content.filter {
                                it is ContentItem.Text && it.text.isNotBlank()
                            }
                            val note = previousState.note.copy(content = contentFilter)
                            editNoteUseCase(note)
                        }
                        Finished
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                        }
                        Finished
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is Editing){
                        previousState.note.content.toMutableList().apply {
                            val lastItem = last()
                            if(lastItem is ContentItem.Text && lastItem.text.isBlank()){
                                removeAt(lastIndex)
                            }
                            add(Image(url = command.uri.toString()))
                            add(ContentItem.Text(text = ""))
                        }.let {
                            previousState.copy(note = previousState.note.copy(content = it))
                        }
                    }
                    else{
                        previousState
                    }
                }

            }
            is EditNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is Editing){
                        previousState.note.content.toMutableList().apply  {
                            removeAt(index = command.index)
                        }.let {
                            previousState.copy(note = previousState.note.copy(content = it))
                        }
                    }
                    else{
                        previousState
                    }
                }
            }
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: Int) : EditingViewModel
    }
}

sealed interface EditNoteCommand{
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val content: String, val index: Int) : EditNoteCommand
    data class AddImage(val uri: String) : EditNoteCommand
    data class DeleteImage(val index: Int) : EditNoteCommand
    data object Delete : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand

}
sealed interface EditNoteState{
    data object Initial : EditNoteState
    data class Editing(
        val note : Note
    ) : EditNoteState {
        val isSaveEnabled : Boolean
            get() {
                return when{
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                    }
                }
            }
    }
    data object Finished : EditNoteState
}