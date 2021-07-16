package com.example.dunzotest.model.photo

import com.example.dunzotest.api.PhotoApi
import io.reactivex.Single
import javax.inject.Inject


interface PhotoListRepository {
    fun getPhotos(searchText: String, pageNo: Int): Single<PhotoResponse>
}

class PhotoListRepositoryImpl @Inject constructor(var photoApi: PhotoApi) :
    PhotoListRepository {
    override fun getPhotos(searchText: String, pageNo: Int): Single<PhotoResponse> {
        return photoApi.getPhotos(searchText, pageNo)
    }
}