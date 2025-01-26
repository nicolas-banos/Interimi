package com.interimi.interimi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Interfaz DAO para interactuar con la base de datos Room.
@Dao
interface UserDao {

    // Inserta un usuario en la tabla y devuelve el ID generado.
    @Insert
    fun insertUser(user: User): Long

    // Obtiene todos los usuarios de la tabla "users".
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    // Busca un usuario por su ID. Si no existe, devuelve null.
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): User?

    // Actualiza un usuario existente en la tabla.
    @Update
    fun updateUser(user: User)

    // Borra un usuario específico por su ID.
    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteUserById(userId: Int)
}


