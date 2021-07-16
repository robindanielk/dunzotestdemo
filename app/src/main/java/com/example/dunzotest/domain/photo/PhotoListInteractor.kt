package com.example.dunzotest.domain.photo

import com.example.dunzotest.model.photo.Photo
import com.example.dunzotest.model.photo.PhotoListRepository
import com.example.dunzotest.model.photo.PhotoResponse
import io.reactivex.Single
import io.reactivex.functions.Function
import javax.inject.Inject

class PhotoListInteractor @Inject constructor(var repository: PhotoListRepository) :
    PhotoListUseCases {
    override fun getPhotos(searchText: String, pageNo: Int): Single<ArrayList<Photo>> {
        return repository.getPhotos(searchText, pageNo).map { response ->
            photoListModelMapper(
                response
            )
        }
    }

    fun photoListModelMapper(response: PhotoResponse?): ArrayList<Photo> {
        var list = ArrayList<Photo>()
        if (response?.photoData?.photos?.size ?: 0 > 0) {
            for (photo in response?.photoData?.photos!!) {
                photo.imageUrl =
                    "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_m.jpg"
                list.add(photo)
            }
        }
        return list
    }
}