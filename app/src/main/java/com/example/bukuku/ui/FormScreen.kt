package com.example.bukuku.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bukuku.data.Buku

/**
 * Layar Form Tambah / Ubah Buku:
 * - Digunakan untuk menambah buku baru atau mengubah buku yang sudah ada.
 * - Dilengkapi tombol chip saran kategori cepat (Novel, Teknologi, Biografi, dll).
 * - Validasi input ketat: Judul wajib diisi dan menampilkan error merah jika kosong.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormScreen(
    bukuId: Long?,
    viewModel: BukuViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mode edit aktif jika targetId > 0
    val targetId = bukuId ?: 0L
    val isEditMode = targetId > 0L

    // Mengambil data buku lama jika dalam mode edit
    val existingBuku by if (isEditMode) {
        viewModel.getBukuById(targetId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf<Buku?>(null) }
    }

    // State field form
    var judul by rememberSaveable { mutableStateOf("") }
    var penulis by rememberSaveable { mutableStateOf("") }
    var kategori by rememberSaveable { mutableStateOf("") }
    var sudahDibaca by rememberSaveable { mutableStateOf(false) }

    // State untuk penanganan pesan validasi error
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isDataLoaded by rememberSaveable { mutableStateOf(false) }

    // Mengisi form dengan data lama saat pertama kali data berhasil dimuat
    LaunchedEffect(existingBuku) {
        val current = existingBuku
        if (isEditMode && current != null && !isDataLoaded) {
            judul = current.judul
            penulis = current.penulis
            kategori = current.kategori
            sudahDibaca = current.sudahDibaca
            isDataLoaded = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Ubah Buku" else "Tambah Buku Baru",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditMode) "Perbarui informasi buku ini" else "Lengkapi informasi buku bacaan di bawah ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            // Field Input Judul (Wajib diisi, ada validasi pesan error)
            OutlinedTextField(
                value = judul,
                onValueChange = {
                    judul = it
                    if (errorMessage != null) {
                        errorMessage = null
                    }
                },
                label = { Text("Judul Buku *") },
                placeholder = { Text("Contoh: Pemrograman Kotlin") },
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Field Input Penulis
            OutlinedTextField(
                value = penulis,
                onValueChange = { penulis = it },
                label = { Text("Penulis") },
                placeholder = { Text("Contoh: Andi, Robert C. Martin") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Field Input Kategori
            OutlinedTextField(
                value = kategori,
                onValueChange = { kategori = it },
                label = { Text("Kategori") },
                placeholder = { Text("Contoh: Novel, Teknologi, Biografi") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Saran Kategori Cepat (Tinggal klik untuk mengisi)
            Column {
                Text(
                    text = "Pilih kategori cepat:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val daftarSaran = listOf("Novel", "Teknologi", "Biografi", "Bisnis", "Pelajaran")
                    daftarSaran.forEach { saran ->
                        SuggestionChip(
                            onClick = { kategori = saran },
                            label = { Text(saran) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Kotak Pilihan Status Bacaan (Sudah Dibaca)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { sudahDibaca = !sudahDibaca }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Checkbox(
                        checked = sudahDibaca,
                        onCheckedChange = { sudahDibaca = it }
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Sudah selesai dibaca",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Centang jika Anda telah menyelesaikan buku ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Simpan / Perbarui
            Button(
                onClick = {
                    // Panggil logika simpan di ViewModel (termasuk validasi judul)
                    viewModel.simpanBuku(
                        id = if (isEditMode) targetId else 0L,
                        judul = judul,
                        penulis = penulis,
                        kategori = kategori,
                        sudahDibaca = sudahDibaca,
                        onError = { pesanError ->
                            errorMessage = pesanError
                        },
                        onSuccess = {
                            onNavigateBack()
                        }
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isEditMode) "Perbarui Buku" else "Simpan Buku",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
