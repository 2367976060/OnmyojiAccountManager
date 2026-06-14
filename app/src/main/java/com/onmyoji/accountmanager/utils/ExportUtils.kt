package com.onmyoji.accountmanager.utils

import android.content.Context
import android.os.Environment
import com.onmyoji.accountmanager.data.Account
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    fun exportToCSV(context: Context, accounts: List<Account>): Boolean {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "OnmyojiAccounts_$timeStamp.csv"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileWriter(file).use { writer ->
                // Write header
                writer.append("ID,账号类型,区服,手机号,主要式神,等级,状态,备注,创建时间,更新时间\n")
                
                // Write data
                accounts.forEach { account ->
                    val createdDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(account.createdAt))
                    val updatedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(account.updatedAt))
                    
                    writer.append("${account.id},")
                        .append("${escapeCSV(account.accountType)},")
                        .append("${escapeCSV(account.server)},")
                        .append("${escapeCSV(account.phoneNumber)},")
                        .append("${escapeCSV(account.shikigami)},")
                        .append("${account.level},")
                        .append("${escapeCSV(account.status)},")
                        .append("${escapeCSV(account.notes)},")
                        .append("$createdDate,")
                        .append("$updatedDate\n")
                }
                writer.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportToJSON(context: Context, accounts: List<Account>): Boolean {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "OnmyojiAccounts_$timeStamp.json"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            val json = buildString {
                append("[\n")
                accounts.forEachIndexed { index, account ->
                    append("  {\n")
                    append("    \"id\": ${account.id},\n")
                    append("    \"accountType\": \"${escapeJSON(account.accountType)}\",\n")
                    append("    \"server\": \"${escapeJSON(account.server)}\",\n")
                    append("    \"phoneNumber\": \"${escapeJSON(account.phoneNumber)}\",\n")
                    append("    \"shikigami\": \"${escapeJSON(account.shikigami)}\",\n")
                    append("    \"level\": ${account.level},\n")
                    append("    \"status\": \"${escapeJSON(account.status)}\",\n")
                    append("    \"notes\": \"${escapeJSON(account.notes)}\",\n")
                    append("    \"createdAt\": ${account.createdAt},\n")
                    append("    \"updatedAt\": ${account.updatedAt}\n")
                    append("  }")
                    if (index < accounts.size - 1) append(",")
                    append("\n")
                }
                append("]")
            }
            
            FileWriter(file).use { writer ->
                writer.write(json)
                writer.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun escapeJSON(value: String): String {
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
