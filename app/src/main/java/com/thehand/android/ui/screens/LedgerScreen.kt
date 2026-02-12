package com.thehand.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import com.thehand.android.ui.viewmodel.LedgerViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    navController: NavController,
    viewModel: LedgerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The Hand") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("new_entry") }
            ) {
                Icon(Icons.Default.Add, "New Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter pills
            FilterChips(
                currentFilter = uiState.filter,
                onFilterChange = { viewModel.setFilter(it) }
            )

            // Entries list
            if (uiState.entries.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.entries, key = { it.id }) { entry ->
                        EntryCard(
                            entry = entry,
                            onClick = { navController.navigate("entry/${entry.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChips(
    currentFilter: EntryType?,
    onFilterChange: (EntryType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterChange(null) },
            label = { Text("All") }
        )
        FilterChip(
            selected = currentFilter == EntryType.BUILT,
            onClick = { onFilterChange(EntryType.BUILT) },
            label = { Text("Built") }
        )
        FilterChip(
            selected = currentFilter == EntryType.HELPED,
            onClick = { onFilterChange(EntryType.HELPED) },
            label = { Text("Helped") }
        )
        FilterChip(
            selected = currentFilter == EntryType.LEARNED,
            onClick = { onFilterChange(EntryType.LEARNED) },
            label = { Text("Learned") }
        )
    }
}

@Composable
fun EntryCard(
    entry: Entry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.type.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = when (entry.type) {
                        EntryType.BUILT -> MaterialTheme.colorScheme.primary
                        EntryType.HELPED -> MaterialTheme.colorScheme.secondary
                        EntryType.LEARNED -> MaterialTheme.colorScheme.tertiary
                    }
                )
                Text(
                    text = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        .format(entry.createdAt.atZone(java.time.ZoneId.systemDefault())),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.whoWhat,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No entries yet.\n\nYou built something. You helped someone.\nYou learned a hard truth.\n\nWrite it down. Move on.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
    }
}
