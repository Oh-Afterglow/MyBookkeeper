package com.example.mybookkeeper

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

class BookListViewModel(private val bookRepository: BookRepository): ViewModel() {


    fun loadBooks(context: Context) {
        bookRepository.loadBooksFromFile(context)
    }

    fun saveBooks(context: Context) {
        bookRepository.saveBooksToFile(context)
    }

    fun addBook(book: Book) : Boolean {
        return bookRepository.addBook(book)
    }

    fun getBookByISBN(isbn: String): Book? {
        return bookRepository.getBookByISBN(isbn)
    }

    fun getBooks(): MutableLiveData<List<Book>> {
        return bookRepository.getBooks()
    }

}

class BookRepository {
    private val booksLiveData = MutableLiveData<List<Book>>()


    fun addBook(book: Book) : Boolean {
        val currentBooks = (booksLiveData.value ?: emptyList()).toMutableList()
        for (b in currentBooks) {
            if (b.isbn == book.isbn) {
                return false
            }
        }
        currentBooks.add(book)
        booksLiveData.postValue(currentBooks)
        return true
    }

    fun deleteBook(book: Book) {
        val currentBooks = (booksLiveData.value ?: emptyList()).toMutableList()
        currentBooks.remove(book)
        booksLiveData.postValue(currentBooks)
    }

    fun getBookByISBN(isbn: String): Book? {
        return booksLiveData.value?.find { it.isbn == isbn }
    }

    fun loadBooksFromFile(context: Context) {
        val file = File(context.filesDir, "books.json")
        if (file.exists()) {
            val json = file.readText()
            Log.d("BookRepository", "loadBooksFromFile()")
            Log.d("BookRepository", json)
            val books = Json.decodeFromString(ListSerializer(Book.serializer()), json)
            booksLiveData.postValue(books)
        }
        else {
            // create a new file
            Log.d("BookRepository", "loadBooksFromFile() - file does not exist")
            file.createNewFile()
            file.writeText("[]")
        }
    }

    fun saveBooksToFile(context: Context) {
        val file = File(context.filesDir, "books.json")
        val json = Json.encodeToString(ListSerializer(Book.serializer()), booksLiveData.value ?: emptyList())
        file.writeText(json)
    }

    fun getBooks(): MutableLiveData<List<Book>> {
        return booksLiveData
    }

    companion object {
        private var instance: BookRepository? = null

        fun getInstance(): BookRepository {
            return synchronized(BookRepository::class.java) {
                if (instance == null) {
                    instance = BookRepository()
                }
                instance!!
            }
        }
    }
}

class BookListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookListViewModel(BookRepository.getInstance()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

