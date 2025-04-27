// BuyFilterViewModel.kt
package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wheeldeal.model.CarListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class FilterState(
    val showFilters: Boolean = false,
    val brand: String = "",
    val category: String = "",
    val transmission: String = "",
    val fuelType: String = "",
    val year: String = "",
    val budget: Float = 1_000_000f,
    val sortType: String = "default", // "default", "price_asc", "price_desc"

    // "Applied" copies of each filter
    val appliedBrand: String = "",
    val appliedCategory: String = "",
    val appliedTransmission: String = "",
    val appliedFuelType: String = "",
    val appliedYear: String = "",
    val appliedBudget: Float = 1_000_000f,
    val appliedSortType: String = "default"
)

class BuyFilterViewModel : ViewModel() {

    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters

    fun toggleFilterSection() {
        _filters.update { it.copy(showFilters = !it.showFilters) }
    }

    fun updateBrand(value: String) {
        _filters.update { it.copy(brand = value) }
    }

    fun updateCategory(value: String) {
        _filters.update { it.copy(category = value) }
    }

    fun updateTransmission(value: String) {
        _filters.update { it.copy(transmission = value) }
    }

    fun updateFuelType(value: String) {
        _filters.update { it.copy(fuelType = value) }
    }

    fun updateYear(value: String) {
        _filters.update { it.copy(year = value) }
    }

    fun updateBudget(value: Float) {
        _filters.update { it.copy(budget = value) }
    }

    fun updateSort(sortType: String) {
        _filters.update { it.copy(sortType = sortType) }
    }

    fun resetFilters() {
        _filters.update {
            it.copy(
                brand = "",
                category = "",
                transmission = "",
                fuelType = "",
                year = "",
                budget = 50_000f,
                sortType = "default",
                appliedBrand = "",
                appliedCategory = "",
                appliedTransmission = "",
                appliedFuelType = "",
                appliedYear = "",
                appliedBudget = 50_000f,
                appliedSortType = "default"
            )
        }
    }

    fun applyFilters() {
        _filters.update {
            it.copy(
                appliedBrand = it.brand.trim(),
                appliedCategory = it.category,
                appliedTransmission = it.transmission,
                appliedFuelType = it.fuelType,
                appliedYear = it.year,
                appliedBudget = it.budget,
                appliedSortType = it.sortType
            )
        }
    }

    fun updateAllFilters(
        brand: String,
        category: String,
        transmission: String,
        fuelType: String,
        year: String,
        budget: Float
    ) {
        _filters.update {
            it.copy(
                brand = brand,
                category = category,
                transmission = transmission,
                fuelType = fuelType,
                year = year,
                budget = budget,
                appliedBrand = brand.trim(),
                appliedCategory = category,
                appliedTransmission = transmission,
                appliedFuelType = fuelType,
                appliedYear = year,
                appliedBudget = budget
            )
        }
    }

    fun filterListings(listings: List<CarListing>): List<CarListing> {
        val f = _filters.value
        val filtered = listings.filter {
            (f.appliedBrand.isBlank() || it.brand.equals(f.appliedBrand, ignoreCase = true)) &&
                    (f.appliedCategory.isBlank() || it.category == f.appliedCategory) &&
                    (f.appliedTransmission.isBlank() || it.transmission == f.appliedTransmission) &&
                    (f.appliedFuelType.isBlank() || it.fuelType == f.appliedFuelType) &&
                    (f.appliedYear.isBlank() || it.year.toString() == f.appliedYear) &&
                    it.price <= f.appliedBudget
        }

        return when (f.appliedSortType) {
            "price_asc" -> filtered.sortedBy { it.price }
            "price_desc" -> filtered.sortedByDescending { it.price }
            else -> filtered
        }
    }
}