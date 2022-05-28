package com.example.mybookkeeper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader

class BookListActivity : AppCompatActivity() {
    private var bookList: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        loadBooks(this)
    }

    override fun onStop() {
        super.onStop()
        saveBooks()
    }

    private fun loadBooks(context: Context) {
        // load books from file asynchronously
        Thread {
            val file = File(context.filesDir, "books.json")
            if (file.exists()) {
                val reader = FileReader(file)
                val json = Json.decodeFromString(ListSerializer(Book.serializer()), reader.readText())
                runOnUiThread {
                    bookList.addAll(json)
                }
            }
        }.start()
    }

    private fun saveBooks() {
        // save books to file asynchronously
        Thread {
            this.openFileOutput("books.json", Context.MODE_PRIVATE).use {
                it.write(Json.encodeToString(bookList).toByteArray())
            }
        }.start()
    }
}


