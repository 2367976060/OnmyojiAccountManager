package com.onmyoji.accountmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onmyoji.accountmanager.R
import com.onmyoji.accountmanager.data.Account
import com.onmyoji.accountmanager.databinding.ItemAccountBinding

class AccountAdapter(
    private val onItemClick: (Account) -> Unit,
    private val onItemLongClick: (Account) -> Boolean
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(DiffCallback()) {

    class AccountViewHolder(
        private val binding: ItemAccountBinding,
        private val onItemClick: (Account) -> Unit,
        private val onItemLongClick: (Account) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account) {
            binding.apply {
                tvPhoneNumber.text = account.phoneNumber
                tvServer.text = account.server
                tvShikigami.text = account.shikigami
                tvLevel.text = "Lv.${account.level}"
                tvAccountType.text = account.accountType
                
                // Status badge
                tvStatus.text = account.status
                val statusColor = when (account.status) {
                    "正常" -> R.color.status_active
                    "冻结" -> R.color.status_frozen
                    "封禁" -> R.color.status_banned
                    else -> R.color.text_secondary
                }
                tvStatus.setBackgroundColor(ContextCompat.getColor(root.context, statusColor))
                
                root.setOnClickListener { onItemClick(account) }
                root.setOnLongClickListener { onItemLongClick(account) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Account, newItem: Account) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
