package com.example.bukuku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bukuku.data.Buku
import com.example.bukuku.data.BukuRepository
import com.example.bukuku.data.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Merepresentasikan state antarmuka (UI State) untuk layar BukuKu.
 * Menggabungkan daftar buku dari Room dan preferensi mode ringkas dari DataStore.
 */
data class BukuUiState(
    val daftarBuku: List<Buku> = emptyList(),
    val isModeRingkas: Boolean = false
)

/**
 * ViewModel utama untuk mengelola state dan proses bisnis aplikasi BukuKu.
 * Bertindak sebagai jembatan antara antarmuka (Compose UI) dan sumber data (Repository).
 */
class BukuViewModel(
    private val bukuRepository: BukuRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // StateFlow yang menggabungkan aliran data buku dan preferensi tampilan menjadi satu UiState
    // WhileSubscribed(5000) memastikan langganan Flow tetap aktif saat terjadi rotasi layar
    val uiState: StateFlow<BukuUiState> = combine(
        bukuRepository.allBuku,
        settingsRepository.modeRingkasFlow
    ) { daftar, isRingkas ->
        BukuUiState(daftarBuku = daftar, isModeRingkas = isRingkas)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BukuUiState()
    )

    // Mengambil data satu buku secara reaktif berdasarkan id untuk layar Detail atau Form Edit
    fun getBukuById(id: Long): Flow<Buku?> {
        return bukuRepository.getBukuById(id)
    }

    /**
     * Memvalidasi input dan menyimpan buku (tambah data baru jika id = 0, ubah data jika id > 0).
     * Jika judul kosong, akan memanggil onError dan membatalkan penyimpanan.
     */
    fun simpanBuku(
        id: Long = 0,
        judul: String,
        penulis: String,
        kategori: String,
        sudahDibaca: Boolean = false,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ): Boolean {
        // Validasi: judul tidak boleh kosong
        if (judul.isBlank()) {
            onError("Judul tidak boleh kosong")
            return false
        }

        viewModelScope.launch {
            val buku = Buku(
                id = id,
                judul = judul.trim(),
                penulis = penulis.trim(),
                kategori = kategori.trim(),
                sudahDibaca = sudahDibaca
            )

            if (id == 0L) {
                bukuRepository.insert(buku)
            } else {
                bukuRepository.update(buku)
            }
            onSuccess()
        }
        return true
    }

    // Mengubah status bacaan buku (false -> true atau sebaliknya)
    fun toggleStatus(buku: Buku) {
        viewModelScope.launch {
            val bukuTerbaru = buku.copy(sudahDibaca = !buku.sudahDibaca)
            bukuRepository.update(bukuTerbaru)
        }
    }

    // Menghapus data buku dari database
    fun hapusBuku(buku: Buku, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            bukuRepository.delete(buku)
            onSuccess()
        }
    }

    // Mengubah preferensi mode tampilan ringkas ke DataStore
    fun setModeRingkas(isRingkas: Boolean) {
        viewModelScope.launch {
            settingsRepository.setModeRingkas(isRingkas)
        }
    }
}

/**
 * Factory class untuk menginisialisasi BukuViewModel dengan parameter repository
 * tanpa perlu library dependency injection tambahan seperti Hilt.
 */
class BukuViewModelFactory(
    private val bukuRepository: BukuRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BukuViewModel::class.java)) {
            return BukuViewModel(bukuRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Kelas ViewModel tidak dikenal: ${modelClass.name}")
    }
}
