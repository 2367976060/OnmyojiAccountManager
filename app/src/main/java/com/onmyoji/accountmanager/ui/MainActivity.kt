package com.onmyoji.accountmanager.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.onmyoji.accountmanager.R
import com.onmyoji.accountmanager.data.Account
import com.onmyoji.accountmanager.data.DatabaseHelper
import com.onmyoji.accountmanager.databinding.ActivityMainBinding
import com.onmyoji.accountmanager.utils.ExportUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AccountAdapter
    private var currentFilter = "all"
    private var currentSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        setupSearchView()
        setupFilterSpinner()
        setupFab()

        loadAccounts()
    }

    override fun onResume() {
        super.onResume()
        loadAccounts()
    }

    private fun setupRecyclerView() {
        adapter = AccountAdapter(
            onItemClick = { account ->
                val intent = Intent(this, AddEditAccountActivity::class.java)
                intent.putExtra("ACCOUNT_ID", account.id)
                startActivity(intent)
            },
            onItemLongClick = { account ->
                showDeleteDialog(account)
                true
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                loadAccounts()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilterSpinner() {
        val filters = listOf("全部", "正常", "冻结", "封禁")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = spinnerAdapter

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = when (position) {
                    1 -> "正常"
                    2 -> "冻结"
                    3 -> "封禁"
                    else -> "all"
                }
                loadAccounts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAccounts() {
        val accounts = if (currentSearchQuery.isNotEmpty()) {
            dbHelper.searchAccounts(currentSearchQuery, currentFilter)
        } else {
            if (currentFilter == "all") {
                dbHelper.getAllAccounts()
            } else {
                dbHelper.getAccountsByStatus(currentFilter)
            }
        }

        adapter.submitList(accounts)
        binding.emptyView.visibility = if (accounts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDeleteDialog(account: Account) {
        AlertDialog.Builder(this)
            .setTitle("删除账号")
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.confirm) { _, _ ->
                dbHelper.deleteAccount(account.id)
                loadAccounts()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export_csv -> {
                exportToCSV()
                true
            }
            R.id.action_export_json -> {
                exportToJSON()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportToCSV() {
        val accounts = dbHelper.getAllAccounts()
        val success = ExportUtils.exportToCSV(this, accounts)
        if (success) {
            android.widget.Toast.makeText(this, R.string.export_success, android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, R.string.export_failed, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToJSON() {
        val accounts = dbHelper.getAllAccounts()
        val success = ExportUtils.exportToJSON(this, accounts)
        if (success) {
            android.widget.Toast.makeText(this, R.string.export_success, android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, R.string.export_failed, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
