package com.example.bukuku.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bukuku.data.Buku

/**
 * Layar Daftar Buku (Layar Utama):
 * - Menampilkan daftar koleksi buku menggunakan LazyColumn.
 * - Memiliki Switch di TopAppBar untuk mengubah "Mode tampilan ringkas" (DataStore).
 * - Menampilkan FloatingActionButton untuk berpindah ke form penambahan buku.
 * - Menampilkan pesan "Belum ada buku" jika daftar kosong.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarScreen(
    viewModel: BukuViewModel,
    onTambahClick: () -> Unit,
    onBukuClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mengamati StateFlow dari ViewModel secara reaktif
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("BukuKu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Switch untuk mengaktifkan / menonaktifkan mode ringkas (tersimpan di DataStore)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Ringkas",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Switch(
                            checked = uiState.isModeRingkas,
                            onCheckedChange = { viewModel.setModeRingkas(it) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTambahClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Buku"
                )
            }
        }
    ) { innerPadding ->
        // Jika daftar buku kosong, tampilkan pesan sesuai instruksi soal
        if (uiState.daftarBuku.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada buku",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            // Menampilkan daftar buku menggunakan LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.daftarBuku, key = { it.id }) { buku ->
                    BukuItemCard(
                        buku = buku,
                        isModeRingkas = uiState.isModeRingkas,
                        onClick = { onBukuClick(buku.id) }
                    )
                }
            }
        }
    }
}

/**
 * Komponen Card item individual untuk setiap buku.
 * Menyesuaikan tampilan berdasarkan status Mode Ringkas:
 * - Mode Normal: Judul, Penulis, Kategori, Status.
 * - Mode Ringkas: Hanya Judul dan Status.
 */
@Composable
fun BukuItemCard(
    buku: Buku,
    isModeRingkas: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Baris Judul dan Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buku.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Label status bacaan
                val statusText = if (buku.sudahDibaca) "Sudah dibaca" else "Belum dibaca"
                val statusColor = if (buku.sudahDibaca) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            // Mode normal: tampilkan juga Penulis dan Kategori
            if (!isModeRingkas) {
                Spacer(modifier = Modifier.height(6.dp))
                if (buku.penulis.isNotBlank()) {
                    Text(
                        text = "Penulis: ${buku.penulis}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (buku.kategori.isNotBlank()) {
                    Text(
                        text = "Kategori: ${buku.kategori}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
