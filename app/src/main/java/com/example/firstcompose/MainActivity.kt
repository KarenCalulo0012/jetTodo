package com.example.firstcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstcompose.ui.theme.FirstComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Notes(val title: String, val isCompleted: Boolean = false)

@Preview(showBackground = true)
@Composable
fun TodoListScreen(modifier: Modifier = Modifier) {
    var textFieldState by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf<Notes>() }
    var isError by remember { mutableStateOf(false) }
    var pos by remember { mutableIntStateOf(0) }
    var isUpdating by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    val filteredNotes = remember { mutableStateListOf<Notes>() }

    LaunchedEffect(searchQuery) {
        filteredNotes.clear()
        filteredNotes.addAll(notes.filter { it.title.contains(searchQuery, true) })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "My To Do List",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "List down all of the things you will not do here",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            label = { Text("Search Tasks") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.clickable {

                    }
                )
            }
        )

        OutlinedTextField(
            value = textFieldState,
            onValueChange = { textFieldState = it },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        if(textFieldState.isNotBlank()) {
                            if(isUpdating) {
                                notes[pos] = notes[pos].copy(title = textFieldState)
                            } else {
                                notes.add(Notes(textFieldState))
                            }
                            textFieldState = ""
                            isUpdating = false
                            isError = false
                        } else {
                            isError = true
                            isUpdating = false
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        val someNotes = if(searchQuery.isBlank()) notes else filteredNotes
        ItemNotes(someNotes) { position ->
            textFieldState = someNotes[position].title
            isUpdating = true
            pos = position
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemNotes(
    notes: MutableList<Notes> = remember { mutableStateListOf(Notes("")) },
    onEditClicked: (Int) -> Unit = {}
) {
    LazyColumn {
        itemsIndexed(notes) { index, item ->
            Column {
                Row(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = {
                            notes[index] = item.copy(isCompleted = it)
                        }
                    )
                    Text(
                        text = item.title,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onEditClicked(index)
                            },
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None)
                    IconButton(onClick = {
                        notes.removeAt(index)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color.DarkGray)
                )
            }
        }
    }
}