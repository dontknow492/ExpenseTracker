package org.ghost.expensetracker.data.viewModels.secondary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount
import org.ghost.expensetracker.data.useCase.category.DeleteCategoryUseCase
import org.ghost.expensetracker.data.useCase.category.GetCategoryWithExpenseCountUseCase
import org.ghost.expensetracker.data.useCase.category.UpdateCategoriesUseCase
import javax.inject.Inject


data class DateFilter(val minDate: Long? = null, val maxDate: Long? = null)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoryWithExpenseCountUseCase: GetCategoryWithExpenseCountUseCase,
    private val updateCategoriesUseCase: UpdateCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId: Long = _profileOwnerId

    // 1. StateFlow to hold the CURRENT filter state.
    private val _dateFilter = MutableStateFlow(DateFilter())


    private val _categoriesState =
        MutableStateFlow<UiState<List<CategoryWithExpenseCount>>>(UiState.Loading)

    // 2. The public, read-only version for the UI to collect.
    val categoriesState: StateFlow<UiState<List<CategoryWithExpenseCount>>> =
        _categoriesState.asStateFlow()


    init {
        // 3. The database observer now collects in the background and pushes updates to our UI state.
        viewModelScope.launch {
            _dateFilter.flatMapLatest { filter ->
                getCategoryWithExpenseCountUseCase(
                    profileOwnerId = _profileOwnerId,
                    minDate = filter.minDate,
                    maxDate = filter.maxDate
                )
                    .map<List<CategoryWithExpenseCount>, UiState<List<CategoryWithExpenseCount>>> {
                        UiState.Success(
                            it
                        )
                    }
                    .onStart { emit(UiState.Loading) }
                    .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
            }.collect { newUiState ->
                // Update the UI state whenever the database or filter changes
                _categoriesState.value = newUiState
            }
        }
    }

    fun onMinDateChange(date: Long?) {
        _dateFilter.update {
            it.copy(minDate = date)
        }
    }

    fun onMaxDateChange(date: Long?) {
        _dateFilter.update {
            it.copy(maxDate = date)
        }
    }

    fun moveCategory(fromIndex: Int, toIndex: Int) {
        Log.d("CategoryViewModel", "moveCategory: fromIndex=$fromIndex, toIndex=$toIndex")
        if (fromIndex == toIndex) return

        val currentState = _categoriesState.value
        // You can only reorder the list if it's currently in a Success state.
        if (currentState is UiState.Success) {
            val currentList = currentState.data.toMutableList()

            // Perform the move in the local list
            val movedItem = currentList.removeAt(fromIndex)
            currentList.add(toIndex, movedItem)

            // 4. Instantly update the UI with the reordered list.
            _categoriesState.value = UiState.Success(currentList)

            // 5. Trigger the background save operation
            saveNewOrder(currentList)
        }
    }

    private fun saveNewOrder(newList: List<CategoryWithExpenseCount>) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedCategories = newList.mapIndexed { index, item ->
                item.category.copy(displayOrder = index)
            }
            updateCategoriesUseCase(updatedCategories)
        }
    }

    fun deleteCategory(category: CategoryWithExpenseCount) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCategoryUseCase(category.category.id)
//            updateCategoriesUseCase(category.category.copy(isDeleted = true))
        }
    }

}