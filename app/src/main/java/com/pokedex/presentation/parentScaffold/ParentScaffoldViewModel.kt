package com.pokedex.presentation.parentScaffold

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ParentScaffoldViewModel @Inject constructor(): ViewModel() {

    private val _searchQuery= MutableStateFlow("")
    val searchQuery= _searchQuery.asStateFlow()

    private val _bgColor = MutableStateFlow(Color.White)
    val bgColor = _bgColor.asStateFlow()

    fun onSearchQueryChanged(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    fun onBgColorChanged(color: Color) {
        _bgColor.value = color
    }
}