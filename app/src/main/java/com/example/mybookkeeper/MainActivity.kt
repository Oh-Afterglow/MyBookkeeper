package com.example.mybookkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private val bookListViewModel: BookListViewModel by lazy {
        BookListViewModelFactory().create(BookListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookListViewModel.loadBooks(this.applicationContext)

        val buttonAdd = findViewById<Button>(R.id.button_add)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        val buttonView = findViewById<Button>(R.id.button_view)
        buttonView.setOnClickListener {
            val intent = Intent(this, BookListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookListViewModel.saveBooks(this.applicationContext)
    }
}