package com.thehand.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thehand.android.data.model.Addendum
import com.thehand.android.data.model.Entry
import com.thehand.android.ui.viewmodel.EntryDetailViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    navController: NavController,
    entryId: Long,
    viewModel: EntryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val addendumText by viewModel.addendumText.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (!uiState.isLoading && uiState.entry != null) {
                        IconButton(onClick = { viewModel.showDeleteDialog() }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.entry != null && uiState.isLocked) {
                FloatingActionButton(
                    onClick = { viewModel.showAddendumDialog() }
                ) {
                    Icon(Icons.Default.Add, "Add Addendum")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.entry?.let { entry ->
                EntryDetailContent(
                    entry = entry,
                    addendums = uiState.addendums,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        if (uiState.showAddendumDialog) {
            AddendumDialog(
                addendumText = addendumText,
                onTextChange = { viewModel.updateAddendumText(it) },
                onDismiss = { viewModel.hideAddendumDialog() },
                onSave = { viewModel.saveAddendum() }
            )
        }

        if (uiState.showDeleteDialog) {
            DeleteEntryDialog(
                onDismiss = { viewModel.hideDeleteDialog() },
                onConfirm = {
                    viewModel.deleteEntry {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun EntryDetailContent(
    entry: Entry,
    addendums: List<Addendum>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Type and date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = entry.type.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = when (entry.type) {
                    com.thehand.android.data.model.EntryType.BUILT -> MaterialTheme.colorScheme.primary
                    com.thehand.android.data.model.EntryType.HELPED -> MaterialTheme.colorScheme.secondary
                    com.thehand.android.data.model.EntryType.LEARNED -> MaterialTheme.colorScheme.tertiary
                }
            )
            Text(
                text = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                    .format(entry.createdAt.atZone(java.time.ZoneId.systemDefault())),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Who or what
        DetailSection(
            title = "Who or what was affected?",
            content = entry.whoWhat
        )

        // What cost
        if (entry.whatCost.isNotBlank()) {
            DetailSection(
                title = "What did it cost you?",
                content = entry.whatCost
            )
        }

        // What differently
        if (entry.whatDifferently.isNotBlank()) {
            DetailSection(
                title = "What would you do differently?",
                content = entry.whatDifferently
            )
        }

        // Edit window status
        if (entry.isLocked) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "24-hour edit window has passed. This entry is now locked. You can add addendums below.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Addendums
        if (addendums.isNotEmpty()) {
            HorizontalDivider()

            Text(
                text = "Addendums",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            addendums.forEach { addendum ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = addendum.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                .format(addendum.createdAt.atZone(java.time.ZoneId.systemDefault())),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AddendumDialog(
    addendumText: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Addendum") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Add new context or update to this locked entry.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = addendumText,
                    onValueChange = onTextChange,
                    label = { Text("Addendum") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = addendumText.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Entry?") },
        text = { Text("This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
