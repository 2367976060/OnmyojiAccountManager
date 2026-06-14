package com.onmyoji.accountmanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.onmyoji.accountmanager.R
import com.onmyoji.accountmanager.data.Account
import com.onmyoji.accountmanager.data.DatabaseHelper
import com.onmyoji.accountmanager.databinding.ActivityAddEditAccountBinding
import com.onmyoji.accountmanager.utils.EncryptionUtils

class AddEditAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditAccountBinding
    private lateinit var dbHelper: DatabaseHelper
    private var accountId: Long = -1L
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        setupSpinners()

        accountId = intent.getLongExtra("ACCOUNT_ID", -1L)
        isEditMode = accountId != -1L

        if (isEditMode) {
            title = getString(R.string.edit_account)
            loadAccountData()
        } else {
            title = getString(R.string.add_account)
        }
    }

    private fun setupSpinners() {
        // Account type spinner
        val accountTypes = listOf("官服", "B服", "其他渠道")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAccountType.adapter = typeAdapter

        // Status spinner
        val statuses = listOf("正常", "冻结", "封禁")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter
    }

    private fun loadAccountData() {
        val account = dbHelper.getAccountById(accountId)
        account?.let {
            binding.apply {
                spinnerAccountType.setSelection(getAccountTypePosition(it.accountType))
                etServer.setText(it.server)
                etPhoneNumber.setText(it.phoneNumber)
                etPassword.setText(EncryptionUtils.decrypt(it.password))
                etShikigami.setText(it.shikigami)
                etLevel.setText(it.level.toString())
                spinnerStatus.setSelection(getStatusPosition(it.status))
                etNotes.setText(it.notes)
            }
        }
    }

    private fun getAccountTypePosition(type: String): Int {
        return when (type) {
            "官服" -> 0
            "B服" -> 1
            "其他渠道" -> 2
            else -> 0
        }
    }

    private fun getStatusPosition(status: String): Int {
        return when (status) {
            "正常" -> 0
            "冻结" -> 1
            "封禁" -> 2
            else -> 0
        }
    }

    private fun saveAccount() {
        val accountType = binding.spinnerAccountType.selectedItem.toString()
        val server = binding.etServer.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val shikigami = binding.etShikigami.text.toString().trim()
        val levelText = binding.etLevel.text.toString().trim()
        val status = binding.spinnerStatus.selectedItem.toString()
        val notes = binding.etNotes.text.toString().trim()

        if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "请输入手机号"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "请输入密码"
            return
        }

        val level = levelText.toIntOrNull() ?: 0
        val encryptedPassword = EncryptionUtils.encrypt(password)

        val account = Account(
            id = if (isEditMode) accountId else 0,
            accountType = accountType,
            server = server,
            phoneNumber = phoneNumber,
            password = encryptedPassword,
            shikigami = shikigami,
            level = level,
            status = status,
            notes = notes
        )

        if (isEditMode) {
            dbHelper.updateAccount(account)
        } else {
            dbHelper.insertAccount(account)
        }

        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_edit_menu, menu)
        if (isEditMode) {
            menu.findItem(R.id.action_delete).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                saveAccount()
                true
            }
            R.id.action_delete -> {
                dbHelper.deleteAccount(accountId)
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
