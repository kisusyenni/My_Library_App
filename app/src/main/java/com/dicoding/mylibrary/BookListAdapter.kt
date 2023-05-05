package com.dicoding.mylibrary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BookListAdapter(private val listBook: ArrayList<Book>): RecyclerView.Adapter<BookListAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        var tvAuthor: TextView = itemView.findViewById(R.id.tv_item_author)
        var tvStatus: TextView = itemView.findViewById(R.id.tv_item_status)
        var rbRating: RatingBar = itemView.findViewById(R.id.rb_item_rating)
        var ivImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        var btnShare: Button = itemView.findViewById(R.id.btn_share)
        var btnFavorite: ToggleButton = itemView.findViewById(R.id.btn_favorite)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row_book, viewGroup, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    // Bind book view holder with layout
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val book = listBook[position]
        println(position)
        Glide.with(holder.itemView.context)
            .load(book.image)
            .apply(RequestOptions().override(100, 150))
            .into(holder.ivImage)
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = "By ${book.author}"
        holder.rbRating.rating = book.rating.toFloat()
        holder.btnFavorite.isChecked = book.isFavorite
        // set text view status text and icon
        when(book.status) {
            ReadingStatus.HAVE_NOT_READ.status -> {
                holder.tvStatus.text = "You haven't read this book"
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_visibility_off_24,0,0,0)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.tvStatus.context, R.color.red_700))
            }
            ReadingStatus.READING.status -> {
                holder.tvStatus.text = "You are reading this book"
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_remove_red_eye_24,0,0,0)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.tvStatus.context, R.color.cyan_900))
            }
            ReadingStatus.HAVE_READ.status -> {
                holder.tvStatus.text = "You have read this book"
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_emoji_events_24,0,0,0)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.tvStatus.context, R.color.amber_700))
            }
        }

        // When itemView is clicked, send callback to do intent to detail page
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(book.isbn,"detail")
        }

        // When share button is clicked, send callback to share text to other apps
        holder.btnShare.setOnClickListener {
            onItemClickCallback.onItemClicked(book.isbn,"share")
        }

        // When favorite button is clicked
        holder.btnFavorite.setOnCheckedChangeListener { _, isChecked ->

            book.isFavorite = isChecked
            // Send book array position callback to show toast information
            onItemClickCallback.onItemClicked(book.isbn,"favorite")
        }
    }

    override fun getItemCount(): Int {
        return listBook.size
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(isbn: String, action: String)
    }

}