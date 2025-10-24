package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel() : NoteDbModel{
    return NoteDbModel(
        id = id,
        title = title,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}
fun List<ContentItemDbModel>.toContentItems() : List<ContentItem>{
    return map { model ->
        when(model.contentType){
            ContentType.TEXT -> {
                ContentItem.Text(text = model.content)
            }
            ContentType.IMAGE -> {
                ContentItem.Image(url = model.content)
            }
        }
    }
}
fun List<ContentItem>.toContentItemDbModel(noteId: Int) : List<ContentItemDbModel>{
    return mapIndexed {index, content ->
        when(content){
            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = content.url,
                    order = index
                )
            }
            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = content.text,
                    order = index
                )
            }
        }
    }
}
fun NoteWithContentDbModel.toEntity() : Note{
    return Note(
        id = noteDbModel.id,
        title = noteDbModel.title,
        content = contentItemDbModel.toContentItems(),
        updatedAt = noteDbModel.updatedAt,
        isPinned = noteDbModel.isPinned
    )
}
fun List<NoteWithContentDbModel>.toEntities() : List<Note>{
    return map {
        it.toEntity()
    }
}
