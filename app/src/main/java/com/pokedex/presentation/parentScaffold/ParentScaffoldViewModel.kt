package com.pokedex.presentation.parentScaffold

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParentScaffoldViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(ScaffoldUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: ScaffoldUiEvent) {
        when (event) {
            is ScaffoldUiEvent.SearchQueryChanged -> {
                _state.update {
                    it.copy(searchQuery = event.searchQuery)
                }
            }
            is ScaffoldUiEvent.PokemonTypeIdChanged -> {
                _state.update {
                    it.copy(typeId = event.typeId)
                }
            }
            is ScaffoldUiEvent.BgColorChanged -> {
                _state.update {
                    it.copy(bgColor = event.bgColor)
                }
            }
            is ScaffoldUiEvent.BackBtnVisibility -> {
                _state.update {
                    it.copy(isBtnVisible = event.isVisible)
                }
            }
        }
    }
}