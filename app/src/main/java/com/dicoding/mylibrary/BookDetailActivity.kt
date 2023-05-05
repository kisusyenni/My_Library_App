package com.dicoding.mylibrary

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat

class BookDetailActivity : AppCompatActivity() {

    companion object {
        const val BOOK_ID = "book_id"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        // Get book id
        val bookId = intent.getStringExtra(BOOK_ID)

        // Get book data
        var book = BooksData.bookListData.find{ it.isbn == bookId}
        if(book == null) { book = BooksData.bookListData[0]}

        // Declare and initiate book variables
        val bookTitle: TextView = findViewById(R.id.book_detail_title)
        val bookAuthor: TextView = findViewById(R.id.book_detail_author)
        val bookImage: ImageView = findViewById(R.id.book_detail_image)
        val bookRating: RatingBar = findViewById(R.id.book_detail_rating)
        val bookSynopsis: TextView = findViewById(R.id.book_detail_synopsis)
        val bookPublisher: TextView = findViewById(R.id.book_detail_publisher)
        val bookPages: TextView = findViewById(R.id.book_detail_pages)
        val bookISBN: TextView = findViewById(R.id.book_detail_isbn)
        val bookLanguage: TextView = findViewById(R.id.book_detail_language)
        val bookGenres: TextView = findViewById(R.id.book_detail_genres)
        val bookStatus: TextView = findViewById(R.id.book_detail_status)
        val btnShare: Button = findViewById(R.id.detail_btn_share)
        val btnFavorite: ToggleButton = findViewById(R.id.detail_btn_favorite)
        btnFavorite.isChecked = book.isFavorite

        // Set action bar
        supportActionBar?.title = book.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Change book variables' content if book id exist
        bookId.let {
            bookTitle.text = book.title
            bookAuthor.text = "by ${book.author}"
            bookImage.setBackgroundResource(book.image)
            bookRating.rating = book.rating.toFloat()
            bookSynopsis.text = book.synopsis
            bookPublisher.text = book.publisher
            bookPages.text = book.pages.toString()
            bookISBN.text = book.isbn
            bookLanguage.text = book.language
            bookGenres.text = book.genres
            when(book.status) {
                ReadingStatus.HAVE_NOT_READ.status -> {
                    bookStatus.text = "You haven't read this book"
                    bookStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_visibility_off_24,0,0,0)
                    bookStatus.setTextColor(ContextCompat.getColor(bookStatus.context, R.color.red_700))
                }
                ReadingStatus.READING.status -> {
                    bookStatus.text = "You are reading this book"
                    bookStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_remove_red_eye_24,0,0,0)
                    bookStatus.setTextColor(ContextCompat.getColor(bookStatus.context, R.color.cyan_900))
                }
                ReadingStatus.HAVE_READ.status -> {
                    bookStatus.text = "You have read this book"
                    bookStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_emoji_events_24,0,0,0)
                    bookStatus.setTextColor(ContextCompat.getColor(bookStatus.context, R.color.amber_700))
                }
            }
        }

        // When share button is clicked, send callback to share text to other apps
        btnShare.setOnClickListener {
            shareBook(book)
        }

        // When favorite button is clicked
        btnFavorite.setOnCheckedChangeListener { _, isChecked ->
            book.isFavorite = isChecked
            // Show toast after clicked and saved to shared preferences
            when(book.isFavorite) {
                true -> {
                    favoriteToastHandler(book.title, "is added to")
                }
                false -> {
                    favoriteToastHandler(book.title, "is removed from")
                }
            }
        }
    }

    private fun shareBook(book: Book) {
        fun sendMessage(status: String): String {
            return "Hi! I'd like to recommend a book that $status which title is ${book.title} by ${book.author}. It's a great book to read, hope you enjoy it too :))"
        }
        val message = when(book.status) {
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

    // Function for handling toast after favorite button is clicked
    private fun favoriteToastHandler(favBookTitle: String, action:String) {
        Toast.makeText(this@BookDetailActivity, "\"$favBookTitle\" $action My Favorite", Toast.LENGTH_SHORT).show()
    }
}