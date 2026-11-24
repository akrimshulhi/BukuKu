package com.example.bukuku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.bukuku.data.Buku

/**
 * Layar Form Tambah / Ubah Buku:
 * - Digunakan untuk menambah buku baru (jika bukuId == null atau 0).
 * - Digunakan untuk mengubah buku yang ada (jika bukuId > 0).
 * - Validasi input: Judul wajib diisi. Menampilkan pesan error jika judul kosong.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    bukuId: Long?,
    viewModel: BukuViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mode edit jika targetId > 0
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
                title = { Text(if (isEditMode) "Ubah Buku" else "Tambah Buku") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Field Input Judul (Wajib diisi, ada validasi pesan error)
            OutlinedTextField(
                value = judul,
                onValueChange = {
                    judul = it
                    // Hapus pesan error jika pengguna mulai mengetik
                    if (errorMessage != null) {
                        errorMessage = null
                    }
                },
                label = { Text("Judul Buku *") },
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Field Input Penulis
            OutlinedTextField(
                value = penulis,
                onValueChange = { penulis = it },
                label = { Text("Penulis") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Field Input Kategori
            OutlinedTextField(
                value = kategori,
                onValueChange = { kategori = it },
                label = { Text("Kategori (misal: Novel, Teknologi, Biografi)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Pilihan Status Bacaan (Sudah Dibaca)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = sudahDibaca,
                    onCheckedChange = { sudahDibaca = it }
                )
                Text(
                    text = "Sudah selesai dibaca",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Simpan
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Perbarui Buku" else "Simpan Buku")
            }
        }
    }
}
