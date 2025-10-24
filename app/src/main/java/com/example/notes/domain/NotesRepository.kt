package com.example.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun addNote(title: String, content: List<ContentItem>, isPinned: Boolean, updatedAt: Long)
    suspend fun deleteNote(noteId: Int)
    suspend fun updateNote(note: Note)
    fun getAllNotes() : Flow<List<Note>>
    suspend fun getNote(noteId: Int) : Note
    fun searchNote(query: String) : Flow<List<Note>>
    suspend fun switchPinnedStatus(nodeId: Int)
}