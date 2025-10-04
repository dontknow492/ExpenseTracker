package org.ghost.expensetracker.data.viewModels.addScreen

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.ui.states.AddUpdateCategoryUiState
import org.ghost.expensetracker.core.utils.getResourceEntryName
import org.ghost.expensetracker.core.utils.toHexCode
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.useCase.category.CreateCategoryUseCase
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val savedStateHandle: SavedStateHandle,
    @param: ApplicationContext private val context: Context,
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])

    private val _uiState = MutableStateFlow(AddUpdateCategoryUiState())
    val uiState = _uiState.asStateFlow()


    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, isNameError = false)
        }
    }

    fun onColorChange(color: Color?) {
        _uiState.update {
            it.copy(color = color)
        }
    }

    fun onIconIdChange(iconId: Int?) {
        _uiState.update {
            it.copy(iconId = iconId)
        }
    }

    fun saveCategory() {
        Log.d("AddCategory", "saveCategory: ${_uiState.value}, profile: $_profileOwnerId")
        val currentState = _uiState.value.copy(
            isLoading = false,
            isNameError = false,
            error = null
        )


        var category = Category(
            id = 0,
            profileOwnerId = _profileOwnerId,
            name = currentState.name,
            colorHex = currentState.color?.toHexCode(),
            iconName = null,
            displayOrder = 0
        )

        viewModelScope.launch {
            if (currentState.iconId != null) {
                val iconName = getResourceEntryName(currentState.iconId, context)
                category = category.copy(iconName = iconName)
            }


            val result = createCategoryUseCase(category)

            result.onSuccess {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isCategorySaved = true
                )
            }.onFailure { throwable ->
                when (throwable) {
                    is InvalidNameException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = throwable.message,
                                isNameError = true
                            )
                        }
                    }

                    is InvalidCredentialsException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = throwable.message
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = throwable.message
                            )
                        }
                    }
                }
            }
        }
    }

}