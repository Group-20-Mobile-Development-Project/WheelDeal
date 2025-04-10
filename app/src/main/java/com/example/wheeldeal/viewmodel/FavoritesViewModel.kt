package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheeldeal.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()

    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())
    val favoriteIds: StateFlow<List<String>> = _favoriteIds

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteIds()
                .onSuccess { ids -> _favoriteIds.value = ids }
        }
    }

    fun toggleFavorite(listingId: String) {
        viewModelScope.launch {
            val isFav = _favoriteIds.value.contains(listingId)
            if (isFav) {
                repository.removeFromFavorites(listingId)
                    .onSuccess {
                        _favoriteIds.value = _favoriteIds.value - listingId
                    }
            } else {
                repository.addToFavorites(listingId)
                    .onSuccess {
                        _favoriteIds.value = _favoriteIds.value + listingId
                    }
            }
        }
    }

    fun isFavorite(listingId: String): Boolean {
        return _favoriteIds.value.contains(listingId)
    }
}
