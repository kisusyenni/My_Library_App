package com.dicoding.mylibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {
    private lateinit var rvFavoriteBooks: RecyclerView
    private var listBook: ArrayList<Book> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        supportActionBar?.title = "Favorite Books"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvFavoriteBooks = findViewById(R.id.rv_favorite_books)
        rvFavoriteBooks.setHasFixedSize(true)

        loadBooksData()
    }

    private fun showRecyclerList() {
        rvFavoriteBooks.layoutManager = LinearLayoutManager(this)
        val bookListAdapter = BookListAdapter(listBook)
        rvFavoriteBooks.adapter = bookListAdapter

        bookListAdapter.setOnItemClickCallback(object : BookListAdapter.OnItemClickCallback {
            override fun onItemClicked(isbn: String, action: String) {
                val book = BooksData.bookListData.find { it.isbn == isbn }
                when(action) {
                    // Move to detail page after book list item is clicked
                    "detail" -> {
                        val bookDetailActivity = Intent(this@FavoriteActivity, BookDetailActivity::class.java)
                        bookDetailActivity.putExtra(BookDetailActivity.BOOK_ID, isbn)
                        startActivity(bookDetailActivity)
                    }

                    // Share a message to recommend a book after share button is clicked
                    "share" -> {
                        fun sendMessage(status: String): String {
                            return """Hi! I'd like to recommend a book that $status which title is ${book?.title} by ${book?.author}. It's a great book to read, hope you enjoy it too :))"""
                        }
                        val message = when(book?.status) {
                            ReadingStatus.HAVE_NOT_READ.status-> "I'm interested in reading"
                            ReadingStatus.READING.status -> "I'm reading right now"
                            ReadingStatus.HAVE_READ.status -> "I have read"
                            else -> null
                        }
                        val sendBookIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, message?.let { sendMessage(it) })
                            type = "text/plain"
                        }

                        val shareBookIntent = Intent.createChooser(sendBookIntent, null)
                        startActivity(shareBookIntent)
                    }

                    "favorite" -> {

                        // Show toast after clicked
                        when(book?.isFavorite) {
                            true -> {
                                favoriteToastHandler(book.title, "is added to")
                            }
                            false -> {
                                favoriteToastHandler(book.title, "is removed from")
                            }
                        }

                        // clear and load new books data after button favorite clicked
                        listBook.clear()
                        loadBooksData()

                    }
                }
            }
        })
    }

    // Function for handling toast after favorite button is clicked
    private fun favoriteToastHandler(favBookTitle: String, action:String) {
        Toast.makeText(this@FavoriteActivity, "\"$favBookTitle\" $action My Favorite", Toast.LENGTH_SHORT).show()
    }

    private fun loadBooksData() {
        val favoriteBooksData = BooksData.bookListData.filter { it.isFavorite }

        listBook.addAll(favoriteBooksData)
        showRecyclerList()
    }


}