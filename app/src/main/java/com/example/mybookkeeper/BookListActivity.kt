package com.example.mybookkeeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BookListActivity : AppCompatActivity() {
    private val bookListViewModel: BookListViewModel by lazy {
        BookListViewModelFactory().create(BookListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        val bookAdapter = BookAdapter()
        val bookListView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.book_list)
        bookListView.adapter = bookAdapter
        bookListViewModel.getBooks().observe(this) {
            bookAdapter.submitList(it ?: emptyList())
        }
    }

    override fun onStop() {
        super.onStop()
        bookListViewModel.saveBooks(this)
    }

}


