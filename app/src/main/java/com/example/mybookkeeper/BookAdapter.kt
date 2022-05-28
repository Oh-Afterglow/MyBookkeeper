package com.example.mybookkeeper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.net.URL
import java.util.concurrent.Executors

class BookAdapter(private val onClick: (Book) -> Unit) : ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffCallback) {

    class ViewHolder(view: View, val onClick: (Book) -> Unit) : RecyclerView.ViewHolder(view) {
        private var isbn = ""
        private val title = view.findViewById<View>(R.id.title_list) as TextView
        private val author = view.findViewById<View>(R.id.author_list) as TextView
        private val publisher = view.findViewById<View>(R.id.publisher_list) as TextView
        private val year = view.findViewById<View>(R.id.year_list) as TextView
        private val cover = view.findViewById<View>(R.id.book_cover) as ImageView
        private var currentBook: Book? = null

        init {
            view.setOnClickListener {
                currentBook?.let {
                    onClick(it)
                }
            }
        }
        fun bind(book: Book) {
            currentBook = book
            isbn = book.isbn
            title.text = book.title
            author.text = book.author
            publisher.text = book.publisher
            year.text = book.year
            if (book.coverURl != "") {
                var image: Bitmap?
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    try {
                        val imageUrl = URL(book.coverURl)
                        image = BitmapFactory.decodeStream(
                            imageUrl.openConnection().getInputStream()
                        )
                        handler.post {
                            cover.setImageBitmap(image)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
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