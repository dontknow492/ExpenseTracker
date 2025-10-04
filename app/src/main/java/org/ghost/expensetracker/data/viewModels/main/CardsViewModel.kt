package org.ghost.expensetracker.data.viewModels.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.core.ui.states.CardsUiState
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.useCase.profile.DeleteCardUseCase
import org.ghost.expensetracker.data.useCase.profile.FilterCardsUseCase
import org.ghost.expensetracker.data.useCase.profile.UpdateCardUseCase
import javax.inject.Inject


@HiltViewModel
class CardsViewModel @Inject constructor(
    private val filterCardsUseCase: FilterCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId: Long = _profileOwnerId


    private companion object {
        const val QUERY_KEY = "query"
        const val SORT_BY_KEY = "sortBy"
        const val SORT_ORDER_KEY = "sortOrder"
    }

    private val _uiState = MutableStateFlow(
        CardsUiState(
            // Restore state from SavedStateHandle or use defaults
            query = savedStateHandle[QUERY_KEY] ?: "",
            sortBy = savedStateHandle[SORT_BY_KEY] ?: CardSortBy.ADDED_AT,
            sortOrder = savedStateHandle[SORT_ORDER_KEY] ?: SortOrder.DESCENDING
        )
    )
    val uiState = _uiState.asStateFlow()


    init {
        // This block runs when the ViewModel is created.
        // It sets up a reactive stream to fetch cards whenever search or sort options change.
        observeCardUpdates()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCardUpdates() {
        // We create a flow that emits whenever the query or sorting changes.
        // `distinctUntilChanged` prevents re-fetching if the state updates but these values don't change.
        val searchAndSortFlow = _uiState.map {
            Triple(it.query, it.sortBy, it.sortOrder)
        }

        viewModelScope.launch {
            // `flatMapLatest` is crucial here. If the user types a new query or changes the sort order,
            // it cancels the previous database query and starts a new one.
            searchAndSortFlow.flatMapLatest { (query, sortBy, sortOrder) ->
                val name = if (query.isEmpty() || query.isBlank()) null else query
                filterCardsUseCase(
                    profileOwnerId = profileOwnerId,
                    name = name,
                    sortBy = sortBy,
                    sortOrder = sortOrder
                )
                    .onStart {
                        // Set loading state to true before starting the data fetch
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    .catch { throwable ->
                        // If an error occurs, update the state with the error message
                        _uiState.update {
                            it.copy(error = throwable.message, isLoading = false)
                        }
                    }
            }.collect { fetchedCards ->
                // Once data is successfully fetched, update the state
                _uiState.update {
                    it.copy(cards = fetchedCards, isLoading = false, error = null)
                }
            }
        }
    }


    fun move(fromIndex: Int, toIndex: Int) {
        Log.d("CardsViewModel", "Moving item from $fromIndex to $toIndex")
        if (fromIndex == toIndex) return


        val currentState = _uiState.value
        val currentList = currentState.cards.toMutableList()

        val movedItem = currentList.removeAt(fromIndex)
        currentList.add(toIndex, movedItem)

        _uiState.value = currentState.copy(cards = currentList)

        saveNewOrder(currentList)
    }

    private fun saveNewOrder(newList: List<Card>) {
        viewModelScope.launch {
            val updatedCards = newList.mapIndexed { index, item ->
                item.copy(displayOrder = index)
            }
            updateCardUseCase(updatedCards)
        }
    }


    // ## Public functions to handle UI events ##

    fun onQueryChanged(query: String) {
        // Update the state in both the UI StateFlow and the SavedStateHandle
        savedStateHandle[QUERY_KEY] = query
        _uiState.update { it.copy(query = query) }
    }

    fun onSortByChanged(sortBy: CardSortBy) {
        savedStateHandle[SORT_BY_KEY] = sortBy
        _uiState.update { it.copy(sortBy = sortBy) }
    }

    fun onSortOrderChanged(sortOrder: SortOrder) {
        savedStateHandle[SORT_ORDER_KEY] = sortOrder
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            // Perform delete operation in the background
            val success = deleteCardUseCase(card)
            if (!success) {
                _uiState.update { it.copy(error = "Failed to delete card") }
            }
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            updateCardUseCase(card)
        }
    }


}