package com.example.dunzotest.ui.photo

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dunzotest.R
import com.example.dunzotest.ui.common.LoadingWidget
import com.example.dunzotest.ui.photo.adapter.PhotoAdapter
import com.example.dunzotest.util.DunzoTestApplication
import com.example.dunzotest.util.ViewModelFactory
import javax.inject.Inject

class PhotoListActivity : AppCompatActivity(), TextWatcher, LoadingWidget.Callback {
    lateinit var viewModel: PhotoListViewModel
    lateinit var adapter: PhotoAdapter
    lateinit var layoutManager: GridLayoutManager
    lateinit var recyclerView: RecyclerView
    lateinit var errorText: TextView
    lateinit var loader: ProgressBar
    lateinit var paginationLoader: LoadingWidget
    lateinit var editText: EditText

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private val stateObserver = Observer<PhotoListState> { state ->
        state?.let {
            when (it) {
                is PhotoListState.LoadingState -> handleLoadingState(it)
                is PhotoListState.PaginationLoadingState -> handlePaginationLoadingState(it)
                is PhotoListState.SuccessListState -> handelSuccessList(it)
                is PhotoListState.ErrorState -> handleErrorState(it)
                is PhotoListState.PaginationErrorState -> handlePaginationErrorState(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as DunzoTestApplication).component.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PhotoListViewModel::class.java)
        initView()
        setUpRecyclerView()
        setListeners()
        loadData()
    }

    fun initView() {
        recyclerView = findViewById(R.id.recycler_view)
        errorText = findViewById(R.id.error_text)
        loader = findViewById(R.id.loader)
        paginationLoader = findViewById(R.id.bottom_loader)
        editText = findViewById(R.id.edit_text)
    }

    fun setListeners() {
        editText.addTextChangedListener(this)
        paginationLoader.callback = this
        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)
    }

    fun handelSuccessList(state: PhotoListState.SuccessListState) {
        adapter.setData(state.photoList)
        errorText.visibility = View.GONE
        loader.visibility = View.GONE
        paginationLoader.hideLoading()
    }

    fun handleErrorState(state: PhotoListState.ErrorState) {
        if (!state.error.isEmpty()) {
            adapter.clearData()
            errorText.visibility = View.VISIBLE
            loader.visibility = View.GONE
            errorText.text = state.error
            paginationLoader.hideLoading()
        }
    }

    fun handlePaginationErrorState(state: PhotoListState.PaginationErrorState) {
        if (!state.error.isEmpty()) {
            errorText.visibility = View.GONE
            loader.visibility = View.VISIBLE
            paginationLoader.setRetryMessage(state.error)
        }
    }

    fun handleLoadingState(state: PhotoListState.LoadingState) {
        if (state.loading) {
            adapter.clearData()
            errorText.visibility = View.GONE
            loader.visibility = View.VISIBLE
            paginationLoader.hideLoading()
        }
    }

    fun handlePaginationLoadingState(state: PhotoListState.PaginationLoadingState) {
        if (state.loading) {
            loader.visibility = View.GONE
            errorText.visibility = View.GONE
            paginationLoader.showLoading()
        }
    }

    //on activity started depend on new instance or rotation get data
    fun loadData() {
        if (viewModel.allPhotoList.size > 0) {
            //restore list
            errorText.visibility = View.GONE
            paginationLoader.hideLoading()
            adapter.setData(viewModel.allPhotoList)
        } else {
            viewModel.setEmptySearchTextData()
        }
    }


    fun setUpRecyclerView() {
        adapter = PhotoAdapter()
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        } else {
            layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!viewModel.paginationDone) {
                    val isLastPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    if (viewModel.stateLiveData.value is PhotoListState.SuccessListState && isLastPosition) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }

    //on retry tapped at bottom widget
    override fun retryNextPageLoad() {
        if (viewModel.stateLiveData.value is PhotoListState.SuccessListState) {
            viewModel.loadNextPage()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        viewModel.searchEditText.onNext(editText.text.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}