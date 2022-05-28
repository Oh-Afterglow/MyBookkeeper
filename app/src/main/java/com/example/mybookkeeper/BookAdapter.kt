package com.example.mybookkeeper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BookAdapter : ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isbn = ""
        val title = view.findViewById<View>(R.id.title_list) as TextView
        val author = view.findViewById<View>(R.id.author_list) as TextView
        val publisher = view.findViewById<View>(R.id.publisher_list) as TextView
        val year = view.findViewById<View>(R.id.year_list) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)
        holder.isbn = book.isbn
        holder.title.text = book.title
        holder.author.text = book.author
        holder.publisher.text = book.publisher
        holder.year.text = book.year
    }


}

object BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.isbn == newItem.isbn
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}