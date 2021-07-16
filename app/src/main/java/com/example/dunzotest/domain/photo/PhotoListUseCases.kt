package com.example.dunzotest.domain.photo

import com.example.dunzotest.model.photo.Photo
import io.reactivex.Single

interface PhotoListUseCases{
    fun getPhotos(searchText: String, pageNo: Int): Single<ArrayList<Photo>>
}
