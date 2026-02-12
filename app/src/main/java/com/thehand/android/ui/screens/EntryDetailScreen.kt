package com.thehand.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.canEdit
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    navController: NavController,
    entryId: Long
) {
    // Placeholder - would connect to ViewModel
    val entry: Entry? = null // TODO: Load from ViewModel

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
                    entry?.let {
                        if (it.canEdit()) {
                            IconButton(onClick = { /* Edit */ }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        }
                        IconButton(onClick = { /* Delete */ }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        entry?.let {
            EntryDetailContent(
                entry = it,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun EntryDetailContent(entry: Entry, modifier: Modifier = Modifier) {
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
                fontWeight = FontWeight.Bold
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
        if (!entry.canEdit() && entry.isLocked) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "24-hour edit window has passed. This entry is now locked.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
