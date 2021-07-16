package com.example.dunzotest.model.photo

import com.example.dunzotest.model.photo.PhotoData
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photos")
    var photoData: PhotoData? = null
}