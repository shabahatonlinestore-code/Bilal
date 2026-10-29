package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.AgencyPackage
import com.example.model.AgencySettings
import com.example.model.DeliverableItem
import com.example.model.EmailLog
import com.example.model.InAppNotification
import com.example.model.Order
import com.example.model.OrderMessage
import com.example.model.User

@Database(
    entities = [
        User::class,
        Order::class,
        DeliverableItem::class,
        OrderMessage::class,
        InAppNotification::class,
        EmailLog::class,
        AgencyPackage::class,
        AgencySettings::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VantaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun deliverableDao(): DeliverableDao
    abstract fun orderMessageDao(): OrderMessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun emailLogDao(): EmailLogDao
    abstract fun agencyPackageDao(): AgencyPackageDao
    abstract fun agencySettingsDao(): AgencySettingsDao

    companion object {
        @Volatile
        private var INSTANCE: VantaDatabase? = null

        fun getDatabase(context: Context): VantaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VantaDatabase::class.java,
                    "vanta_digital_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
