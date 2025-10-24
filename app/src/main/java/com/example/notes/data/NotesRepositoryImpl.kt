package com.example.notes.data

import android.content.Context
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl @Inject constructor(
    val notesDao: NotesDao,
    val imageFileManager: ImageFileManager
) : NotesRepository {


    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long,
    ) {

        val contents = content.processInternalStorage()
        val note = Note(
            id = 0,
            title = title,
            content = contents,
            updatedAt = updatedAt,
            isPinned = isPinned
        ).toDbModel()
        val noteId = notesDao.addNote(note).toInt()
        notesDao.addNoteWithContent(note,contents.toContentItemDbModel(noteId))
    }

    override suspend fun deleteNote(noteId: Int) {
        val note = getNote(noteId)
        notesDao.deleteNote(noteId)
        note.content.filterIsInstance<ContentItem.Image>().forEach {
            imageFileManager.deleteImageInternalStorage(it.url)
        }
    }

    override suspend fun updateNote(note: Note) {
        val oldNote = notesDao.getNote(note.id).toEntity()
        val oldUrls = oldNote.content.filterIsInstance<ContentItem.Image>()
        val newUrls = note.content.filterIsInstance<ContentItem.Image>()
        val removedUrls = oldUrls - newUrls
        removedUrls.map { im ->
            imageFileManager.deleteImageInternalStorage(im.toString())
        }
        val newNoteContent = note.content.processInternalStorage()
        val newNote = note.copy(content = newNoteContent)
        notesDao.updateNoteWithContent(newNote.toDbModel(),newNoteContent.toContentItemDbModel(note.id))
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map {
            it.toEntities()
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map {
            it.toEntities()
        }
    }

    override suspend fun switchPinnedStatus(nodeId: Int) {
        notesDao.switchPinnedNote(nodeId)
    }

    private suspend fun List<ContentItem>.processInternalStorage() : List<ContentItem>{
        return map{item ->
            when(item){
                is ContentItem.Image -> {
                    if(imageFileManager.isInternal(item.url)){
                        item
                    } else{
                        val img = imageFileManager.copyImageToInternalStorage(item.url)
                        ContentItem.Image(img)
                    }
                }
                is ContentItem.Text -> {
                    item
                }
            }
        }
    }
}