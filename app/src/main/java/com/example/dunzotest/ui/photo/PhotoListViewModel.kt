package com.example.dunzotest.ui.photo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dunzotest.R
import com.example.dunzotest.domain.photo.PhotoListUseCases
import com.example.dunzotest.model.photo.Photo
import com.example.dunzotest.util.MainUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoListViewModel @Inject constructor(
    application: Application,
    var pholistUseCases: PhotoListUseCases
) :
    AndroidViewModel(application) {


    var pageNo = 0
    var allPhotoList = ArrayList<Photo>()
    var paginationDone: Boolean = false

    var stateLiveData: MutableLiveData<PhotoListState> = MutableLiveData<PhotoListState>()

    var searchText: String = ""
    var disp: CompositeDisposable = CompositeDisposable()
    val searchEditText = PublishSubject.create<String>()

    init {
        setSearchTextListener()
    }

    fun resetList() {
        pageNo = 0
        allPhotoList.clear()
        paginationDone = false
        setLoadingState()
    }

    fun setEmptySearchTextData() {
        if (searchText.trim().isEmpty()) {
            setErrorState(MainUtil.getMessage(getApplication(), R.string.search_msg))
        }
    }


    override fun onCleared() {
        super.onCleared()
        disp.clear()
    }

    fun loadNextPage() {
        setPaginationLoadingState()
        disp.add(
            pholistUseCases.getPhotos(searchText, pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    handleSuccessResult(it)
                }, {
                    handleFailedData(it)
                })
        )

    }


    fun handleSuccessResult(list: ArrayList<Photo>) {
        if (list.size > 0) {
            allPhotoList.addAll(list)
            setSuccessData(list)
            pageNo++
        } else {
            if (searchText.trim().isEmpty()) {
                setErrorState(MainUtil.getMessage(getApplication(), R.string.search_msg))
            } else if (pageNo == 0) {
                setErrorState(MainUtil.getMessage(getApplication(), R.string.no_result_found))
            } else {
                paginationDone = true
            }
        }
    }


    fun handleFailedData(t: Throwable?) {
        t?.let {
            if (pageNo == 0) {
                setErrorState(MainUtil.getErrorTye(t, getApplication()))
            } else {
                setPaginationErrorState(MainUtil.getErrorTye(t, getApplication()))
            }
        }
        setSearchTextListener()
    }

    fun setSearchTextListener() {
        disp.add(
            searchEditText.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter(object : Predicate<String> {
                    override fun test(str: String): Boolean {
                        return handleSearchTextChange(str)
                    }
                })
                .switchMapSingle(object : Function<String, Single<ArrayList<Photo>>> {
                    override fun apply(string: String): Single<ArrayList<Photo>> {
                        searchText = string
                        resetList()
                        return pholistUseCases.getPhotos(searchText, pageNo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    }
                })
                .subscribe({
                    handleSuccessResult(it)
                }, {
                    handleFailedData(it)
                })
        )
    }

    fun handleSearchTextChange(str: String): Boolean {
        searchText = str
        if (str.trim().isEmpty()) {
            setEmptySearchTextData()
            return false
        }
        return true
    }

    //handle all states
    fun setSuccessData(list: ArrayList<Photo>) {
        stateLiveData.postValue(PhotoListState.SuccessListState(list))
    }

    fun setErrorState(msg: String) {
        stateLiveData.postValue(PhotoListState.ErrorState(msg))
    }

    fun setLoadingState() {
        stateLiveData.postValue(PhotoListState.LoadingState(true))
    }

    fun setPaginationLoadingState() {
        stateLiveData.postValue(PhotoListState.PaginationLoadingState(true))
    }

    fun setPaginationErrorState(msg: String) {
        stateLiveData.postValue(PhotoListState.PaginationErrorState(msg))
    }


}