package com.thehand.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thehand.android.data.model.EntryType
import com.thehand.android.ui.viewmodel.NewEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(
    navController: NavController,
    viewModel: NewEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveEntry() },
                        enabled = viewModel.canSave() && !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Type selector
            Text(
                text = "What type?",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.type == EntryType.BUILT,
                    onClick = { viewModel.updateType(EntryType.BUILT) },
                    label = { Text("Built") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = uiState.type == EntryType.HELPED,
                    onClick = { viewModel.updateType(EntryType.HELPED) },
                    label = { Text("Helped") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = uiState.type == EntryType.LEARNED,
                    onClick = { viewModel.updateType(EntryType.LEARNED) },
                    label = { Text("Learned") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Who or what was affected?
            OutlinedTextField(
                value = uiState.whoWhat,
                onValueChange = { viewModel.updateWhoWhat(it) },
                label = { Text("Who or what was affected?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // What did it cost you?
            OutlinedTextField(
                value = uiState.whatCost,
                onValueChange = { viewModel.updateWhatCost(it) },
                label = { Text("What did it cost you?") },
                supportingText = { Text("Time. Effort. Risk. Discomfort.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // What would you do differently?
            OutlinedTextField(
                value = uiState.whatDifferently,
                onValueChange = { viewModel.updateWhatDifferently(it) },
                label = { Text("What would you do differently?") },
                supportingText = { Text("Reflection, not regret.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }
}
