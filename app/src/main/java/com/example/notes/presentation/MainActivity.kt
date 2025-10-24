package com.example.notes.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.notes.presentation.editing.EditNoteScreen
import com.example.notes.presentation.navigation.NavGraph
import com.example.notes.presentation.ui.theme.NotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val context: Context = this.applicationContext
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NavGraph()
            }
//            NotesTheme {
//                NotesScreen(onNoteClick = {
//                    Log.d("MainActivity", "$it")
//                },
//                    onAddClick = {
//                        Log.d("MainActivity", "Add Noteer")
//                    })
//            }
        }
    }
}
