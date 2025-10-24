package com.example.notes.presentation.creation

import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import com.example.notes.domain.ContentItem.*
import com.example.notes.domain.NotesRepository
import com.example.notes.presentation.creation.CreateNoteState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {
    val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()
    fun processCommand(command: CreateNoteCommand){
        when(command){
            CreateNoteCommand.Back -> {
                _state.update {
                    Finished
                }
            }
            is CreateNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is Creation) {
                        val newNotes = previousState.content
                            .mapIndexed { index, item ->
                            if (command.index == index && item is ContentItem.Text) {
                                item.copy(text = command.content)
                            } else {
                                item
                            }
                        }
                        previousState.copy(content = newNotes)
                    }
                    else{
                        previousState
                    }
                }
            }
            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if(previousState is Creation){
                        previousState.copy(
                            title = command.title,
                        )
                    }
                    else{
                        Creation(title = command.title)
                    }
                }
            }
            // ТУТ Я САМ СДЕЛАЛ ТУТ МОЖЕТ БЫТЬ ОШИБКА
            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is Creation) {
                            val title = previousState.title
                            val newNotes = mutableListOf<ContentItem>()
                            previousState.content.mapIndexed {index, con ->
                                when(con){
                                    is Image -> {
                                        newNotes.add(con)
                                    }
                                    is Text -> {
                                        newNotes.add(con)
                                    }
                                }
                            }
                            val content = newNotes
                            addNoteUseCase(
                                title = title,
                                content = content,
                            )
                            Finished
                        } else{
                            previousState
                        }
                    }
                }
            }

            is CreateNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is Creation){
                        previousState.content.toMutableList().apply {
                            val lastItem = last()
                            if(lastItem is ContentItem.Text && lastItem.text.isBlank()){
                                removeAt(lastIndex)
                            }
                            add(Image(url = command.uri.toString()))
                            add(ContentItem.Text(text = ""))
                        }.let {
                            previousState.copy(
                                content = it,
                            )
                        }
                    }
                    else{
                        previousState
                    }
                }
            }

            is CreateNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is Creation){
                        previousState.content.toMutableList().apply {
                            removeAt(index = command.index)
                        }.let {
                            previousState.copy(
                                content = it,
                            )
                        }
                    }
                    else{
                        previousState
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand{
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String, val index: Int) : CreateNoteCommand
    data class AddImage(val uri: String) : CreateNoteCommand
    data class DeleteImage(val index: Int) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand

}
sealed interface CreateNoteState{
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
    ) : CreateNoteState{
        val isSaveEnabled: Boolean
            get() {
                return when{
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.text.isNotBlank()
                        }
                    }
                }
            }
    }
    data object Finished : CreateNoteState
}