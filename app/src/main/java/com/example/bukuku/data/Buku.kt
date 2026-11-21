package com.example.bukuku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Buku merepresentasikan tabel "buku" di dalam database Room SQLite.
 */
@Entity(tableName = "buku")
data class Buku(
    // Primary key unik yang digenerate otomatis oleh Room
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Judul buku (wajib diisi pada form)
    val judul: String,

    // Nama penulis buku
    val penulis: String,

    // Kategori buku (contoh: Novel, Teknologi, Biografi)
    val kategori: String,

    // Status bacaan: false = Belum dibaca, true = Sudah dibaca (default: false)
    val sudahDibaca: Boolean = false
)
