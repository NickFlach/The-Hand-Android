package com.thehand.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thehand.android.data.model.Thread
import com.thehand.android.ui.viewmodel.ThreadsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadsScreen(
    navController: NavController,
    viewModel: ThreadsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showCreateDialog.collectAsState()
    val threadName by viewModel.newThreadName.collectAsState()
    val threadDescription by viewModel.newThreadDescription.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threads") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() }
            ) {
                Icon(Icons.Default.Add, "New Thread")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Some work spans months or years. Connect entries to ongoing responsibilities.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.activeThreads.isEmpty() && uiState.closedThreads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No threads yet. Create one to get started.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.activeThreads.isNotEmpty()) {
                        item {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(uiState.activeThreads) { thread ->
                            ThreadCard(
                                thread = thread,
                                onClose = { viewModel.closeThread(thread) },
                                onDelete = { viewModel.deleteThread(thread) }
                            )
                        }
                    }

                    if (uiState.closedThreads.isNotEmpty()) {
                        item {
                            Text(
                                text = "Closed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }

                        items(uiState.closedThreads) { thread ->
                            ThreadCard(
                                thread = thread,
                                onReopen = { viewModel.reopenThread(thread) },
                                onDelete = { viewModel.deleteThread(thread) }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            CreateThreadDialog(
                threadName = threadName,
                threadDescription = threadDescription,
                onThreadNameChange = { viewModel.updateThreadName(it) },
                onThreadDescriptionChange = { viewModel.updateThreadDescription(it) },
                onDismiss = { viewModel.hideCreateDialog() },
                onCreate = { viewModel.createThread() }
            )
        }
    }
}

@Composable
fun ThreadCard(
    thread: Thread,
    onClose: (() -> Unit)? = null,
    onReopen: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Navigate to thread detail */ }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (thread.description.isNotBlank()) {
                    Text(
                        text = thread.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = if (thread.isClosed) "Closed" else "Active",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (thread.isClosed)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (onClose != null) {
                        DropdownMenuItem(
                            text = { Text("Close Thread") },
                            onClick = {
                                onClose()
                                showMenu = false
                            }
                        )
                    }
                    if (onReopen != null) {
                        DropdownMenuItem(
                            text = { Text("Reopen Thread") },
                            onClick = {
                                onReopen()
                                showMenu = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showDeleteDialog = true
                            showMenu = false
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Thread?") },
            text = { Text("This will remove the thread but keep all linked entries.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateThreadDialog(
    threadName: String,
    threadDescription: String,
    onThreadNameChange: (String) -> Unit,
    onThreadDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Thread") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = threadName,
                    onValueChange = onThreadNameChange,
                    label = { Text("Name") },
                    placeholder = { Text("e.g., Project Migration") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = threadDescription,
                    onValueChange = onThreadDescriptionChange,
                    label = { Text("Description (optional)") },
                    placeholder = { Text("What is this thread about?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onCreate,
                enabled = threadName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
