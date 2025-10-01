package org.ghost.expensetracker.data.viewModels.secondary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.useCase.profile.DeleteAccountUseCase
import org.ghost.expensetracker.data.useCase.profile.GetAccountsUseCase
import org.ghost.expensetracker.data.useCase.profile.UpdateAccountUseCase
import javax.inject.Inject


data class AccountsUiState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId: Long = _profileOwnerId

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeAccountUpdates()
    }

    private fun observeAccountUpdates() {
        viewModelScope.launch {
            getAccountsUseCase(profileOwnerId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(error = throwable.message, isLoading = false)
                    }
                }
                .collect { fetchedAccounts ->
                    _uiState.update {
                        it.copy(accounts = fetchedAccounts, isLoading = false, error = null)
                    }
                }
        }
    }

    fun move(fromIndex: Int, toIndex: Int) {
        Log.d("AccountsViewModel", "Moving item from $fromIndex to $toIndex")
        if (fromIndex == toIndex) return


        val currentState = _uiState.value
        val currentList = currentState.accounts.toMutableList()

        val movedItem = currentList.removeAt(fromIndex)
        currentList.add(toIndex, movedItem)

        _uiState.value = currentState.copy(accounts = currentList)

        saveNewOrder(currentList)
    }

    private fun saveNewOrder(newList: List<Account>) {
        viewModelScope.launch {
            val updatedAccounts = newList.mapIndexed { index, item ->
                item.copy(displayOrder = index)
            }
            updateAccountUseCase(updatedAccounts)
        }
    }


    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            val success = deleteAccountUseCase(account)
            if (!success) {
                _uiState.update {
                    it.copy(error = "Failed to delete account")
                }
            }

        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            val success = updateAccountUseCase(account)
            if (!success) {
                _uiState.update {
                    it.copy(error = "Failed to update account")
                }
            }
        }

    }

}