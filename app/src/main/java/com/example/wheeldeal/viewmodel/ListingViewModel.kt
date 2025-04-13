package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ListingState {
    object Loading : ListingState()
    data class Success(val listings: List<CarListing>) : ListingState()
    data class Error(val message: String) : ListingState()
}

class ListingViewModel(
    private val repository: ListingRepository = ListingRepository()
) : ViewModel() {

    private val _listingState = MutableStateFlow<ListingState>(ListingState.Loading)
    val listingState: StateFlow<ListingState> = _listingState

    init {
        fetchListings()
    }

    fun fetchListings() {
        _listingState.value = ListingState.Loading
        viewModelScope.launch {
            val result = repository.getAllListings()
            _listingState.value = result.fold(
                onSuccess = { listings -> ListingState.Success(listings) },
                onFailure = { throwable -> ListingState.Error(throwable.message ?: "Unknown error") }
            )
        }
    }
    fun addListing(listing: CarListing, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.addListing(listing)
            onResult(result.isSuccess)
            if (result.isSuccess) {
                repository.sendNewListingNotification(listing)
                fetchListings()
            }
        }
    }


    fun deleteListing(listingId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteListing(listingId)
            onResult(result.isSuccess)
            fetchListings()
        }
    }

    fun updateListing(listing: CarListing, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateListing(listing)
            onResult(result.isSuccess)
            fetchListings()
        }
    }



}
