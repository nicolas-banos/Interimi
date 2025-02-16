package com.interimi.interimi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("UPDATE users SET name = :newName WHERE id = :userId")
    suspend fun updateUserName(userId: Int, newName: String)

    @Query("UPDATE users SET age = :newAge WHERE id = :userId")
    suspend fun updateUserAge(userId: Int, newAge: Int)

    @Query("UPDATE users SET history = :history WHERE id = :userId")
    suspend fun updateUserHistory(userId: Int, history: String)

    @Query("UPDATE users SET goals = :goals WHERE id = :userId")
    suspend fun updateUserGoals(userId: Int, goals: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)

    @Query("UPDATE users SET goals = '' WHERE id = :userId")
    suspend fun deleteGoalsByUserId(userId: Int)


}
