package com.example.mybookkeeper

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.serialization.Serializable
import java.net.URL
import java.util.concurrent.Executors


class AddBookActivity : AppCompatActivity() {

    // access the widgets
    private lateinit var titleText: TextView
    private lateinit var authorText: TextView
    private lateinit var publishInfo: TextView
    private lateinit var imageView: ImageView
    private lateinit var addButton: Button
    private lateinit var searchBox: EditText
    private lateinit var searchButton: Button

    // book info to save
    private var isbn = ""
    private var title = ""
    private var subtitle = ""
    private var author = ""
    private var translator = ""
    private var publisher = ""
    private var year = ""
    private var pages = 0
    private var coverURl = ""
    private var price = ""
    private var authorIntro = ""
    private var description = ""

    private val bookListViewModel: BookListViewModel by lazy {
        BookListViewModelFactory().create(BookListViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        titleText = findViewById(R.id.textView3)
        authorText = findViewById(R.id.textView4)
        publishInfo = findViewById(R.id.textView5)
        imageView = findViewById(R.id.imageView)
        addButton = findViewById(R.id.button2)
        searchBox = findViewById(R.id.isbnSearchBox)
        searchButton = findViewById(R.id.button)

        initAddBookButton()
        initSearchButton()
    }

    private fun initAddBookButton() {
        addButton.isEnabled = false
        addButton.setOnClickListener {
            val book = Book(
                isbn,
                title,
                subtitle,
                author,
                translator,
                publisher,
                year,
                pages,
                coverURl,
                price,
                authorIntro,
                description
            )
            if(!bookListViewModel.addBook(book)) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Book already exists")
                alertDialog.setMessage("This book already exists in your book list")
                alertDialog.setPositiveButton("OK") { _, _ ->
                    // do nothing
                }
                alertDialog.show()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Book added")
                alertDialog.setMessage("This book has been added to your book list")
                alertDialog.setPositiveButton("OK") { _, _ ->
                    // do nothing
                }
                alertDialog.show()
                bookListViewModel.saveBooks(this.applicationContext)
            }
        }
    }

    private fun initSearchButton() {
        searchButton.setOnClickListener{
            searchBook()
        }
    }

    private fun searchBook() {
        val isbn = searchBox.text.toString()
        val apikey = "your-api-key"  // Visit https://jike.xyz/jiekou/isbn.html to get your own API key
        val url = "https://api.jike.xyz/situ/book/isbn/$isbn?apikey=$apikey"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.isNull("data")) { // not found
                    // clear past result
                    clearResult()
                    val errorString = "Book not found."
                    MsgDialogFragment(errorString).show(supportFragmentManager, "NoResultDialogFragment")
                } else {
                    val data = response.getJSONObject("data")
                    this.isbn = data.getString("code")
                    title = data.getString("name")
                    subtitle = if (data.isNull("subname")) "" else data.getString("subname")
                    author = data.getString("author")
                    translator = if (data.isNull("translator")) "" else data.getString("translator")
                    publisher = data.getString("publishing")
                    year = data.getString("published")
                    price = data.getString("price")
                    coverURl = data.getString("photoUrl")
                    authorIntro = data.getString("authorIntro")
                    description = data.getString("description")
                    pages = data.getInt("pages")
                    titleText.text = title
                    authorText.text = author
                    val publishInfoText = "$publisher · $year"
                    publishInfo.text = publishInfoText

                    if (coverURl.isNotEmpty()) {
                        var image: Bitmap?
                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        executor.execute {
                            try {
                                val imageUrl = URL(coverURl)
                                image = BitmapFactory.decodeStream(
                                    imageUrl.openConnection().getInputStream()
                                )
                                handler.post {
                                    imageView.setImageBitmap(image)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    addButton.isEnabled = true
                }
            },
            { error ->
                val errorString = error.toString()
                MsgDialogFragment(errorString).show(supportFragmentManager, "NoResultDialogFragment")
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    private fun clearResult() {
        titleText.text = ""
        authorText.text = ""
        publishInfo.text = ""
        searchBox.text.clear()
        imageView.setImageResource(R.drawable.download)
        addButton.isEnabled = false
    }

    class MsgDialogFragment(private val msg: String) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("QwQ")
            builder.setMessage(msg)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            return builder.create()
        }
    }
}

@Serializable
data class Book(
    val isbn: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val translator: String,
    val publisher: String,
    val year: String,
    val pages: Int,
    val coverURl: String,
    val price: String,
    val authorIntro: String,
    val description: String
)