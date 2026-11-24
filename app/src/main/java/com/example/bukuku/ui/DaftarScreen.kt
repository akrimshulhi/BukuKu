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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bukuku.data.Buku

/**
 * Layar Daftar Buku (Layar Utama):
 * - Menampilkan daftar buku menggunakan LazyColumn dengan desain Card modern.
 * - Dilengkapi Switch di TopAppBar untuk mengaktifkan / menonaktifkan "Mode tampilan ringkas".
 * - FloatingActionButton untuk menambah buku baru.
 * - Tampilan visual informatif jika daftar buku masih kosong.
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
                title = {
                    Text(
                        text = "BukuKu",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Switch mode tampilan ringkas (tersimpan permanen di DataStore)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Ringkas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 8.dp)
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
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Buku"
                )
            }
        }
    ) { innerPadding ->
        // Jika data kosong, tampilkan pesan informatif
        if (uiState.daftarBuku.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "📚",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Belum ada buku",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tekan tombol + di pojok kanan bawah untuk mencatat buku bacaan pertamamu.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            // Tampilan daftar buku menggunakan LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
 * Komponen Card individual untuk setiap buku dengan desain modern:
 * - Avatar huruf inisial judul buku di sisi kiri.
 * - Judul buku dan status bacaan berbentuk pill badge.
 * - Mode Normal: Menampilkan juga nama Penulis dan badge Kategori.
 * - Mode Ringkas: Hanya menampilkan Judul dan Status bacaan.
 */
@Composable
fun BukuItemCard(
    buku: Buku,
    isModeRingkas: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar inisial buku di sisi kiri
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (buku.sudahDibaca) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = buku.judul.firstOrNull()?.uppercase() ?: "B",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (buku.sudahDibaca) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Kolom rincian informasi buku
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Judul Buku
                    Text(
                        text = buku.judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Pill Badge Status Bacaan
                    StatusBadge(sudahDibaca = buku.sudahDibaca)
                }

                // Pada Mode Normal: Tampilkan juga Penulis dan Kategori
                if (!isModeRingkas) {
                    Spacer(modifier = Modifier.height(4.dp))

                    if (buku.penulis.isNotBlank()) {
                        Text(
                            text = "Penulis: ${buku.penulis}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (buku.kategori.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        // Pill badge kecil untuk kategori
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = buku.kategori,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Komponen pill badge warna untuk status bacaan buku.
 */
@Composable
fun StatusBadge(
    sudahDibaca: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (sudahDibaca) {
        Color(0xFF2E7D32).copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    }

    val textColor = if (sudahDibaca) {
        Color(0xFF2E7D32)
    } else {
        MaterialTheme.colorScheme.outline
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = if (sudahDibaca) "Sudah dibaca" else "Belum dibaca",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
