package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wheeldeal.model.CarListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class FilterState(
    val showFilters: Boolean = false,
    val brand: String = "",
    val transmission: String = "",
    val fuelType: String = "",
    val year: String = "",
    val budget: Float = 1000000f,

    // Applied filters
    val appliedBrand: String = "",
    val appliedTransmission: String = "",
    val appliedFuelType: String = "",
    val appliedYear: String = "",
    val appliedBudget: Float = 1000000f
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

    fun resetFilters() {
        _filters.update {
            it.copy(
                brand = "",
                transmission = "",
                fuelType = "",
                year = "",
                budget = 50000f,
                appliedBrand = "",
                appliedTransmission = "",
                appliedFuelType = "",
                appliedYear = "",
                appliedBudget = 50000f
            )
        }
    }

    fun applyFilters() {
        _filters.update {
            it.copy(
                appliedBrand = it.brand.trim(),
                appliedTransmission = it.transmission,
                appliedFuelType = it.fuelType,
                appliedYear = it.year,
                appliedBudget = it.budget
            )
        }
    }

    fun updateAllFilters(
        brand: String,
        transmission: String,
        fuelType: String,
        year: String,
        budget: Float
    ) {
        _filters.update {
            it.copy(
                brand = brand,
                transmission = transmission,
                fuelType = fuelType,
                year = year,
                budget = budget,
                appliedBrand = brand.trim(),
                appliedTransmission = transmission,
                appliedFuelType = fuelType,
                appliedYear = year,
                appliedBudget = budget
            )
        }
    }

    fun filterListings(listings: List<CarListing>): List<CarListing> {
        val f = _filters.value
        return listings.filter {
            (f.appliedBrand.isBlank() || it.brand.equals(f.appliedBrand, ignoreCase = true)) &&
                    (f.appliedTransmission.isBlank() || it.transmission == f.appliedTransmission) &&
                    (f.appliedFuelType.isBlank() || it.fuelType == f.appliedFuelType) &&
                    (f.appliedYear.isBlank() || it.year.toString() == f.appliedYear) &&
                    it.price <= f.appliedBudget
        }
    }
}
