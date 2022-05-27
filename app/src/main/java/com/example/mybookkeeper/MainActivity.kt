package com.example.mybookkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickOfAdd(v: View) {
        val intent = Intent(this, AddBookActivity::class.java)
        startActivity(intent)
    }

    fun onClickOfView(v: View) {
        val intent = Intent(this, BookListActivity::class.java)
        startActivity(intent)
    }
}