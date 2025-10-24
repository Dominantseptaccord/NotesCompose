package com.example.notes.domain

import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(note: Note){
        repository.updateNote(note)
    }
}