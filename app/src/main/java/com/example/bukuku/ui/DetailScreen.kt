package com.example.bukuku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Layar Detail Buku:
 * - Menampilkan rincian data buku lengkap.
 * - Tombol untuk mengubah status bacaan (Belum dibaca / Sudah dibaca).
 * - Tombol untuk menuju form ubah data.
 * - Tombol untuk menghapus buku (dengan dialog konfirmasi agar aman).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    bukuId: Long,
    viewModel: BukuViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mengamati data buku spesifik dari Room lewat Flow
    val buku by viewModel.getBukuById(bukuId).collectAsState(initial = null)

    // State untuk kontrol dialog konfirmasi hapus
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detail Buku") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        val currentBuku = buku

        if (currentBuku == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Buku tidak ditemukan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Kartu Informasi Buku
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentBuku.judul,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Penulis:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (currentBuku.penulis.isNotBlank()) currentBuku.penulis else "-",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Kategori:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (currentBuku.kategori.isNotBlank()) currentBuku.kategori else "-",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Status Bacaan:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        val statusLabel = if (currentBuku.sudahDibaca) "Sudah dibaca" else "Belum dibaca"
                        val statusColor = if (currentBuku.sudahDibaca) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }

                // Tombol-tombol Aksi (Ubah Status, Ubah Data, Hapus)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol Toggle Status Sudah/Belum Dibaca
                    Button(
                        onClick = { viewModel.toggleStatus(currentBuku) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentBuku.sudahDibaca) "Tandai Belum Dibaca" else "Tandai Sudah Dibaca"
                        )
                    }

                    // Tombol Edit Buku
                    OutlinedButton(
                        onClick = { onEditClick(currentBuku.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ubah Data Buku")
                    }

                    // Tombol Hapus Buku
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus Buku")
                    }
                }
            }
        }
    }

    // Dialog konfirmasi hapus agar alur aman dari salah klik
    if (showDeleteDialog) {
        val currentBuku = buku
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus buku \"${currentBuku?.judul}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        currentBuku?.let {
                            viewModel.hapusBuku(it) {
                                onNavigateBack()
                            }
                        }
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
