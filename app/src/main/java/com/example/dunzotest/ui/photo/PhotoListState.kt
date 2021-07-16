package com.example.dunzotest.ui.photo

import com.example.dunzotest.model.photo.Photo

sealed class PhotoListState() {
    data class LoadingState(var loading:Boolean):PhotoListState()
    data class PaginationLoadingState(var loading:Boolean):PhotoListState()
    data class ErrorState(var error: String):PhotoListState()
    data class PaginationErrorState(var error: String):PhotoListState()
    data class SuccessListState(val photoList: ArrayList<Photo>):PhotoListState()

}
