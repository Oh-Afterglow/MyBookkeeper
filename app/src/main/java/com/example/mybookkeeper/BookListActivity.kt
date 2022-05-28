package com.example.mybookkeeper

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager

class BookListActivity : AppCompatActivity() {
    private val bookListViewModel: BookListViewModel by lazy {
        BookListViewModelFactory().create(BookListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        val bookAdapter = BookAdapter{Book -> onBookItemClicked(Book)}
        val linearLayoutManager = LinearLayoutManager(this)
        val bookListView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.book_list)
        bookListView.layoutManager = linearLayoutManager
        bookListView.adapter = bookAdapter
        bookListViewModel.getBooks().observe(this) {
            it?.let{ bookAdapter.submitList(it as MutableList<Book>) }
        }

        val searchBox = findViewById<EditText>(R.id.author_search_box)
        searchBox.addTextChangedListener {
            bookListViewModel.searchAuthor(it.toString())
        }
    }

    private fun onBookItemClicked(book: Book){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Delete Book")
        alertDialog.setMessage("Are you sure you want to delete \'${book.title}\'?")
        alertDialog.setPositiveButton("OK") { _, _ ->
            bookListViewModel.deleteBook(book)
        }
        alertDialog.setNegativeButton("Cancel") { _, _ ->
            // do nothing
        }
        alertDialog.show()
    }

    override fun onStop() {
        super.onStop()
        bookListViewModel.saveBooks(this)
    }

}


