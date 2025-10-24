package com.example.notes.domain

sealed interface ContentItem {
    data class Image(val url: String) : ContentItem
    data class Text(val text: String) : ContentItem
}