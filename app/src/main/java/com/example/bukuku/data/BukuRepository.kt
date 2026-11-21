package com.example.bukuku.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository sebagai perantara (abstraksi) antara sumber data (Room DAO) dan ViewModel.
 * Memisahkan logika akses data dari UI layer.
 */
class BukuRepository(private val bukuDao: BukuDao) {

    // Mendapatkan aliran data (Flow) seluruh buku
    val allBuku: Flow<List<Buku>> = bukuDao.getAllBuku()

    // Mendapatkan aliran data satu buku berdasarkan id
    fun getBukuById(id: Long): Flow<Buku?> = bukuDao.getBukuById(id)

    // Menyimpan data buku baru
    suspend fun insert(buku: Buku): Long = bukuDao.insertBuku(buku)

    // Memperbarui data buku yang ada
    suspend fun update(buku: Buku) = bukuDao.updateBuku(buku)

    // Menghapus data buku
    suspend fun delete(buku: Buku) = bukuDao.deleteBuku(buku)
}
