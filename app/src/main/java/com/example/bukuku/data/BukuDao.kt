package com.example.bukuku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) menyediakan metode untuk berinteraksi dengan tabel "buku".
 * Mengembalikan Flow agar UI otomatis mendapatkan update data terbaru secara realtime.
 */
@Dao
interface BukuDao {

    // Mengambil semua buku, diurutkan dari yang terbaru (id terbesar)
    @Query("SELECT * FROM buku ORDER BY id DESC")
    fun getAllBuku(): Flow<List<Buku>>

    // Mengambil satu buku berdasarkan id
    @Query("SELECT * FROM buku WHERE id = :id")
    fun getBukuById(id: Long): Flow<Buku?>

    // Menambahkan data buku baru ke database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuku(buku: Buku): Long

    // Memperbarui data buku yang sudah ada
    @Update
    suspend fun updateBuku(buku: Buku)

    // Menghapus data buku dari database
    @Delete
    suspend fun deleteBuku(buku: Buku)
}
