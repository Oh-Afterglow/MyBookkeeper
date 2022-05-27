package com.example.mybookkeeper

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class AddBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        initAddBookButton()
        initSearchButton()
    }

    private fun initAddBookButton() {
        val addBookButton: Button = findViewById(R.id.button2)
        addBookButton.isEnabled = false
    }

    private fun initSearchButton() {
        val searchButton = findViewById<Button>(R.id.button)
        searchButton.setOnClickListener{
            searchBook()
        }
    }

    private fun searchBook() {
        val searchBox = findViewById<EditText>(R.id.isbnSearchBox)
        val isbn = searchBox.text.toString()
        val apikey = "your_api_key"  // Visit https://jike.xyz/jiekou/isbn.html to get your own API key
        val url = "https://api.jike.xyz/situ/book/isbn/$isbn?apikey=$apikey"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val data = response.getJSONObject("data")
                if (data.isNull("name")) {
                    NoResultDialogFragment().show(supportFragmentManager, "NoResultDialogFragment")
                } else {
                    val title = data.getString("name")
                    val subname = if (data.isNull("subname")) "" else data.getString("subname")
                    val author = data.getString("author")
                    val translator = if (data.isNull("translator")) "" else data.getString("translator")
                    val publisher = data.getString("publishing")
                    val publishDate = data.getString("published")
                    val price = data.getString("price")
                    val photoUrl = data.getString("photoUrl")
                    val authorIntro = data.getString("authorIntro")
                    val description = data.getString("description")
                    val pages = data.getString("pages")
                    val titleText = findViewById<TextView>(R.id.textView3)
                    titleText.text = title
                    val authorText = findViewById<TextView>(R.id.textView4)
                    authorText.text = author
                    val publishInfo = findViewById<TextView>(R.id.textView5)
                    val publishInfoText = "$publisher · $publishDate"
                    publishInfo.text = publishInfoText
                }
            },
            { error ->
                println(error)
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }
}

class NoResultDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("QwQ")
        builder.setMessage("Book not found.")
        builder.setPositiveButton("OK") { dialog, _ ->
            val searchBox = activity?.findViewById<EditText>(R.id.isbnSearchBox)
            searchBox?.text?.clear()
            dialog.dismiss()
        }
        return builder.create()
    }
}