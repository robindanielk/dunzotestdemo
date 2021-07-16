package com.example.dunzotest.ui.photo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dunzotest.R
import com.example.dunzotest.model.photo.Photo
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoAdapter: RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    var photoList = ArrayList<Photo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.setData(photoList[position])

    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun setData(photos : List<Photo>){
        this.photoList.addAll(photos)
        notifyDataSetChanged()
    }

    fun clearData(){
        this.photoList.clear()
        notifyDataSetChanged()
    }


    inner class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun setData(photo: Photo){
            itemView.title.text = photo.title
            Glide.with(itemView.context).load(photo.imageUrl).centerCrop()
                .error(R.drawable.placeholder_image).into(itemView.image)
        }
    }

}