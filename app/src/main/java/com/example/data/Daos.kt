package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AgencyPackage
import com.example.model.AgencySettings
import com.example.model.DeliverableItem
import com.example.model.EmailLog
import com.example.model.InAppNotification
import com.example.model.Order
import com.example.model.OrderMessage
import com.example.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE email != 'sehar.asma30@gmail.com'")
    suspend fun deleteNonAdminUsers()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersForUser(userId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<Order?>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderDirect(orderId: String): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("DELETE FROM orders")
    suspend fun clearAllOrders()

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int
}

@Dao
interface DeliverableDao {
    @Query("SELECT * FROM deliverables WHERE orderId = :orderId ORDER BY deliveredAt DESC")
    fun getDeliverablesForOrder(orderId: String): Flow<List<DeliverableItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliverable(deliverable: DeliverableItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliverables(deliverables: List<DeliverableItem>)

    @Query("DELETE FROM deliverables")
    suspend fun clearAllDeliverables()
}

@Dao
interface OrderMessageDao {
    @Query("SELECT * FROM order_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getMessagesForOrder(orderId: String): Flow<List<OrderMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: OrderMessage)

    @Query("DELETE FROM order_messages")
    suspend fun clearAllMessages()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientRole = :role OR recipientUserId = :userId ORDER BY timestamp DESC")
    fun getNotifications(role: String, userId: String?): Flow<List<InAppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: InAppNotification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientRole = :role")
    suspend fun markAllAsRead(role: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}

@Dao
interface EmailLogDao {
    @Query("SELECT * FROM email_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<EmailLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EmailLog)

    @Query("DELETE FROM email_logs")
    suspend fun clearAllLogs()
}

@Dao
interface AgencyPackageDao {
    @Query("SELECT * FROM agency_packages ORDER BY tierOrder ASC")
    fun getAllPackages(): Flow<List<AgencyPackage>>

    @Query("SELECT * FROM agency_packages ORDER BY tierOrder ASC")
    suspend fun getAllPackagesDirect(): List<AgencyPackage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<AgencyPackage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: AgencyPackage)

    @Query("DELETE FROM agency_packages")
    suspend fun clearAllPackages()
}

@Dao
interface AgencySettingsDao {
    @Query("SELECT * FROM agency_settings WHERE id = 'agency_config_main' LIMIT 1")
    fun getSettings(): Flow<AgencySettings?>

    @Query("SELECT * FROM agency_settings WHERE id = 'agency_config_main' LIMIT 1")
    suspend fun getSettingsDirect(): AgencySettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AgencySettings)
}
