package com.interimi.interimi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// Clase para manejar operaciones con SQLite.
class MySQLiteHelper(context: Context) : SQLiteOpenHelper(context, "InterimiDatabase.db", null, 3) {

    // Método para crear las tablas iniciales de la base de datos.
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("""
                CREATE TABLE Users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    age INTEGER,
                    goals TEXT,
                    history TEXT
                )
            """) // Crea la tabla Users.
            Log.d("SQLite", "Tabla Users creada exitosamente")
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al crear la tabla: ${e.message}")
        }
    }

    // Método para manejar actualizaciones en la estructura de la base de datos.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE Users ADD COLUMN history TEXT DEFAULT ''") // Agrega columna history.
                Log.d("SQLite", "Migración de versión 1 a 2 completada")
            }
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE Users ADD COLUMN new_column TEXT DEFAULT ''") // Ejemplo de migración adicional.
                Log.d("SQLite", "Migración de versión 2 a 3 completada")
            }
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error durante la migración: ${e.message}")
        }
    }

    // Inserta un nuevo usuario en la base de datos.
    fun insertUser(name: String, age: Int?, goals: String?, history: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("age", age)
            put("goals", goals)
            put("history", history)
        }
        try {
            db.insertOrThrow("Users", null, values) // Inserta los datos en la tabla Users.
            Log.d("SQLite", "Usuario insertado exitosamente")
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al insertar usuario: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Obtiene todos los usuarios de la base de datos.
    fun getAllUsers(): List<Map<String, Any>> {
        val db = readableDatabase
        val users = mutableListOf<Map<String, Any>>()
        val cursor = db.rawQuery("SELECT * FROM Users", null)

        try {
            while (cursor.moveToNext()) {
                val user = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    "name" to (cursor.getString(cursor.getColumnIndexOrThrow("name")) ?: ""),
                    "age" to if (!cursor.isNull(cursor.getColumnIndexOrThrow("age"))) {
                        cursor.getInt(cursor.getColumnIndexOrThrow("age"))
                    } else {
                        0 // Valor predeterminado si es nulo.
                    },
                    "goals" to (cursor.getString(cursor.getColumnIndexOrThrow("goals")) ?: ""),
                    "history" to (cursor.getString(cursor.getColumnIndexOrThrow("history")) ?: "")
                )
                users.add(user)
            }
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al obtener usuarios: ${e.message}")
        } finally {
            cursor.close()
            db.close()
        }

        return users
    }

    // Elimina todos los usuarios de la tabla.
    fun deleteAllUsers() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM Users") // Borra todos los registros de la tabla Users.
            Log.d("SQLite", "Todos los usuarios han sido eliminados")
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al borrar usuarios: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Actualiza un usuario existente en la base de datos.
    fun updateUser(userId: Int, name: String, age: Int?, goals: String, history: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("age", age)
            put("goals", goals)
            put("history", history)
        }
        try {
            db.update("Users", values, "id = ?", arrayOf(userId.toString())) // Actualiza el usuario con el ID especificado.
            Log.d("SQLite", "Usuario actualizado exitosamente")
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al actualizar usuario: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Elimina un usuario de la base de datos por su ID.
    fun deleteUserById(userId: Int) {
        val db = writableDatabase
        try {
            db.delete("Users", "id = ?", arrayOf(userId.toString())) // Borra el usuario con el ID especificado.
            Log.d("SQLite", "Usuario eliminado exitosamente")
        } catch (e: Exception) {
            Log.e("SQLiteError", "Error al eliminar usuario: ${e.message}")
        } finally {
            db.close()
        }
    }

    // Obtiene un usuario específico por su ID.
    fun getUserById(userId: Int): Map<String, Any>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Users WHERE id = ?", arrayOf(userId.toString()))
        val user = if (cursor.moveToFirst()) {
            mapOf(
                "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow("name")),
                "age" to cursor.getInt(cursor.getColumnIndexOrThrow("age")),
                "goals" to cursor.getString(cursor.getColumnIndexOrThrow("goals")),
                "history" to cursor.getString(cursor.getColumnIndexOrThrow("history"))
            )
        } else {
            null // Si no se encuentra el usuario.
        }
        cursor.close()
        db.close()
        return user
    }
}
