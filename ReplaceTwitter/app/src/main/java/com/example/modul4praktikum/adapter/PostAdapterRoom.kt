package com.example.modul4praktikum.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.modul4praktikum.room.PostDatabase
import com.google.android.material.textview.MaterialTextView
import com.modul4praktikum.R

class PostAdapterRoom(private var playerList: List<PostDatabase>) :

    RecyclerView.Adapter<PostAdapterRoom.PlayerViewHolder>() {

    // Deklarasi variabel untuk callback ketika item diklik
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var stateFav = false


    // Map untuk menyimpan status suka dari setiap item
    private val likedStatus = mutableMapOf<Int, Boolean>()

    // Fungsi untuk mengatur callback ketika item diklik
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    // Interface untuk callback ketika item diklik
    interface OnItemClickCallback {
        fun onItemClicked(data: PostDatabase)
        fun onMoreClicked(data: PostDatabase, position: Int)

    }

    // Kelas ViewHolder untuk menyimpan referensi view yang digunakan dalam RecyclerView
    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: MaterialTextView = itemView.findViewById(R.id.name)
        val playerUsername: MaterialTextView = itemView.findViewById(R.id.etUsername)
        val playerDescription: MaterialTextView = itemView.findViewById(R.id.description)
        val playerWaktu: MaterialTextView = itemView.findViewById(R.id.waktupost)
        val playerImage: ImageView = itemView.findViewById(R.id.postimage)
        val suka: ImageView = itemView.findViewById(R.id.suka)
        val totalSuka: MaterialTextView = itemView.findViewById(R.id.totalsuka)
        val tagar: MaterialTextView = itemView.findViewById(R.id.tagar)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        val btnSaved: ImageView = itemView.findViewById(R.id.savedbtn)
        val btnShare : ImageView= itemView.findViewById (R.id.sharebtn)



    }

    // Fungsi untuk membuat ViewHolder (Melakukan setting untuk XML yang akan kita gunakan untuk menampilkan data)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.postrv, parent, false)
        return PlayerViewHolder(view)
    }

    // Fungsi untuk mengikat data dengan ViewHolder (memasukkan data yang kita miliki ke dalam XML ViewHolder)
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val data = playerList[position]

        holder.playerName.text = data.name
        holder.playerUsername.text = data.username
        holder.playerWaktu.text = data.waktu
        // Mengatur image
        val uri = Uri.fromFile(data.image)
        holder.playerImage.setImageURI(uri)
        // Mengatur aksi ketika botton favorit diklik
        holder.btnSaved.setOnClickListener {
            stateFav = !stateFav
            holder.btnSaved.setImageResource(if (stateFav) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24)
        }



        holder.btnShare.setOnClickListener {
            // Mengambil data pos terkait
            val postData = playerList[position]

            // Membuat pesan yang akan dibagikan
            val shareMessage = "Lihat postingan ${postData.name} di aplikasi kami!"

            // Membuat Intent untuk berbagi pesan
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }

            // Menampilkan dialog untuk memilih aplikasi untuk berbagi
            val chooserIntent = Intent.createChooser(shareIntent, "Bagikan Postingan Melalui")
            holder.itemView.context.startActivity(chooserIntent)
        }


        // Set initial like status and icon
        val isLiked = likedStatus[position] ?: false
        holder.suka.setImageResource(if (isLiked) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_border_24)
        holder.totalSuka.text = data.likes.toString()

        // mengatur aksi ketika button more di klik
        holder.btnMore.setOnClickListener { onItemClickCallback.onMoreClicked(playerList [holder.layoutPosition], holder.absoluteAdapterPosition) }
        // Mengatur aksi ketika item diklik
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(playerList[holder.layoutPosition]) }

        // Mengatur aksi ketika suka ImageView diklik
        holder.suka.setOnClickListener {
            val newLikedStatus = !(likedStatus[position] ?: false)
            likedStatus[position] = newLikedStatus

            if (newLikedStatus) {
                holder.suka.setImageResource(R.drawable.baseline_favorite_24)
                data.likes += 1
            } else {
                holder.suka.setImageResource(R.drawable.outline_favorite_border_24)
                data.likes -= 1
            }

            holder.totalSuka.text = data.likes.toString()
        }




        // Ekstraksi dan pengaturan hashtags serta deskripsi
        val (cleanDescription, hashtags) = extractDescriptionAndHashtags(data.description)
        holder.playerDescription.text = cleanDescription
        holder.tagar.text = hashtags.joinToString(" ")
    }

    // Fungsi untuk mendapatkan jumlah item
    override fun getItemCount(): Int = playerList.size

    // Fungsi untuk mengekstraksi hashtags dari deskripsi dan menghapusnya dari deskripsi
    private fun extractDescriptionAndHashtags(description: String): Pair<String, List<String>> {
        val words = description.split(" ")
        val hashtags = mutableListOf<String>()
        val cleanDescription = StringBuilder()

        for (word in words) {
            if (word.startsWith("#")) {
                hashtags.add(word)
            } else {
                if (cleanDescription.isNotEmpty()) {
                    cleanDescription.append(" ")
                }
                cleanDescription.append(word)
            }
        }

        return Pair(cleanDescription.toString(), hashtags)
    }
}
