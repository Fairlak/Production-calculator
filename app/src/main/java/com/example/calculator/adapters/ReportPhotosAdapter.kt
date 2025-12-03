
package com.example.calculator.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import java.io.File

class ReportPhotosAdapter(
    private val onAddClick: () -> Unit,
    private val onPhotoClick: (String) -> Unit
) : RecyclerView.Adapter<ReportPhotosAdapter.PhotoViewHolder>() {

    private val photos = mutableListOf<String>()
    private val MAX_PHOTOS = 5

    fun submitList(newPhotos: List<String>) {
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (position < photos.size) {
            holder.bindPhoto(photos[position])
        } else {
            holder.bindAddButton()
        }
    }

    override fun getItemCount(): Int {
        return if (photos.size < MAX_PHOTOS) {
            photos.size + 1
        } else {
            photos.size
        }
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImage: ImageView = itemView.findViewById(R.id.item_photo_image)
        private val addIcon: ImageView = itemView.findViewById(R.id.item_add_icon)

        fun bindPhoto(path: String) {
            addIcon.visibility = View.GONE
            photoImage.visibility = View.VISIBLE

            val imgFile = File(path)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                photoImage.setImageBitmap(myBitmap)
            }

            itemView.setOnClickListener { onPhotoClick(path) }
        }

        fun bindAddButton() {
            addIcon.visibility = View.VISIBLE
            photoImage.setImageBitmap(null)
            photoImage.visibility = View.VISIBLE
            itemView.setOnClickListener { onAddClick() }
        }
    }
}
