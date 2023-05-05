package com.dicoding.mylibrary

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var rvBooks: RecyclerView
    private var listBook: ArrayList<Book> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBooks = findViewById(R.id.rv_books)
        rvBooks.setHasFixedSize(true)

        loadBooksData()
    }

    private fun showRecyclerList() {
        rvBooks.layoutManager = LinearLayoutManager(this)
        val bookListAdapter = BookListAdapter(listBook)
        rvBooks.adapter = bookListAdapter

        bookListAdapter.setOnItemClickCallback(object : BookListAdapter.OnItemClickCallback {
            override fun onItemClicked(isbn: String, action: String) {
                val book = BooksData.bookListData.find { it.isbn == isbn }
                when(action) {
                    // Move to detail page after book list item is clicked
                    "detail" -> {
                        val bookDetailActivity = Intent(this@MainActivity, BookDetailActivity::class.java)
                        bookDetailActivity.putExtra(BookDetailActivity.BOOK_ID, isbn)
                        startActivity(bookDetailActivity)
                    }

                    // Share a message to recommend a book after share button is clicked
                    "share" -> {
                        fun sendMessage(status: String): String {
                            return "Hi! I'd like to recommend a book that $status which title is ${book?.title} by ${book?.author}. It's a great book to read, hope you enjoy it too :))"
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

                        // Save changes to Shared Preferences
//                        saveFavoriteBooks(bookISBN, bookIsFavorite)

                        // Show toast after clicked and saved to shared preferences
                        when(book?.isFavorite) {
                            true -> {
                                favoriteToastHandler(book.title, "is added to")
                            }
                            false -> {
                                favoriteToastHandler(book.title, "is removed from")
                            }
                        }
                    }
                }
            }
        })
    }

    // Create action bar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // When menu on action bar is clicked/selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // Intent to about page
            R.id.action_about -> {
                val openAboutIntent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(openAboutIntent)
            }
            R.id.action_favorite -> {
                val openAboutIntent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(openAboutIntent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    // Function for handling toast after favorite button is clicked
    private fun favoriteToastHandler(favBookTitle: String, action:String) {
        Toast.makeText(this@MainActivity, "\"$favBookTitle\" $action My Favorite", Toast.LENGTH_SHORT).show()
    }

    private fun loadBooksData() {
        listBook.addAll(BooksData.bookListData)
        showRecyclerList()
    }
}