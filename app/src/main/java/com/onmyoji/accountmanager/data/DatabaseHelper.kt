package com.onmyoji.accountmanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "OnmyojiAccounts.db"
        private const val DATABASE_VERSION = 1

        // Table and columns
        const val TABLE_ACCOUNTS = "accounts"
        const val COLUMN_ID = "id"
        const val COLUMN_ACCOUNT_TYPE = "account_type"
        const val COLUMN_SERVER = "server"
        const val COLUMN_PHONE = "phone_number"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_SHIKIGAMI = "shikigami"
        const val COLUMN_LEVEL = "level"
        const val COLUMN_STATUS = "status"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_ACCOUNTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACCOUNT_TYPE TEXT NOT NULL,
                $COLUMN_SERVER TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_SHIKIGAMI TEXT,
                $COLUMN_LEVEL INTEGER DEFAULT 0,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_NOTES TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        onCreate(db)
    }

    // Insert account
    fun insertAccount(account: Account): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_TYPE, account.accountType)
            put(COLUMN_SERVER, account.server)
            put(COLUMN_PHONE, account.phoneNumber)
            put(COLUMN_PASSWORD, account.password)
            put(COLUMN_SHIKIGAMI, account.shikigami)
            put(COLUMN_LEVEL, account.level)
            put(COLUMN_STATUS, account.status)
            put(COLUMN_NOTES, account.notes)
            put(COLUMN_CREATED_AT, account.createdAt)
            put(COLUMN_UPDATED_AT, account.updatedAt)
        }
        val id = db.insert(TABLE_ACCOUNTS, null, values)
        db.close()
        return id
    }

    // Update account
    fun updateAccount(account: Account): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_TYPE, account.accountType)
            put(COLUMN_SERVER, account.server)
            put(COLUMN_PHONE, account.phoneNumber)
            put(COLUMN_PASSWORD, account.password)
            put(COLUMN_SHIKIGAMI, account.shikigami)
            put(COLUMN_LEVEL, account.level)
            put(COLUMN_STATUS, account.status)
            put(COLUMN_NOTES, account.notes)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        val rows = db.update(TABLE_ACCOUNTS, values, "$COLUMN_ID = ?", arrayOf(account.id.toString()))
        db.close()
        return rows
    }

    // Delete account
    fun deleteAccount(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_ACCOUNTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    // Get all accounts
    fun getAllAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                accounts.add(cursorToAccount(it))
            }
        }
        db.close()
        return accounts
    }

    // Get account by id
    fun getAccountById(id: Long): Account? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var account: Account? = null
        cursor.use {
            if (it.moveToFirst()) {
                account = cursorToAccount(it)
            }
        }
        db.close()
        return account
    }

    // Search accounts
    fun searchAccounts(query: String, statusFilter: String? = null): List<Account> {
        val accounts = mutableListOf<Account>()
        val db = readableDatabase
        
        var selection = "($COLUMN_PHONE LIKE ? OR $COLUMN_SHIKIGAMI LIKE ? OR $COLUMN_SERVER LIKE ? OR $COLUMN_NOTES LIKE ?)"
        val args = mutableListOf("%$query%", "%$query%", "%$query%", "%$query%")
        
        if (!statusFilter.isNullOrEmpty() && statusFilter != "all") {
            selection += " AND $COLUMN_STATUS = ?"
            args.add(statusFilter)
        }
        
        val cursor = db.query(
            TABLE_ACCOUNTS,
            null,
            selection,
            args.toTypedArray(),
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                accounts.add(cursorToAccount(it))
            }
        }
        db.close()
        return accounts
    }

    // Filter by status
    fun getAccountsByStatus(status: String): List<Account> {
        val accounts = mutableListOf<Account>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNTS,
            null,
            if (status == "all") null else "$COLUMN_STATUS = ?",
            if (status == "all") null else arrayOf(status),
            null,
            null,
            "$COLUMN_UPDATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                accounts.add(cursorToAccount(it))
            }
        }
        db.close()
        return accounts
    }

    private fun cursorToAccount(cursor: Cursor): Account {
        return Account(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            accountType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_TYPE)),
            server = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVER)),
            phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
            shikigami = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHIKIGAMI)),
            level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)),
            status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
        )
    }
}
