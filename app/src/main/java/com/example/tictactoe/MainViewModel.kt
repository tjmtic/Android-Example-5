package com.example.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(): ViewModel() {


    private val _uiState = MutableStateFlow<MainViewModelUiState>(MainViewModelUiState.TitleScreen)
    val uiState : StateFlow<MainViewModelUiState> = _uiState.stateIn(
        scope = viewModelScope,
        initialValue = MainViewModelUiState.TitleScreen,
        started = SharingStarted.WhileSubscribed(5000)
    )

    private val _state = MutableStateFlow<MainViewModelState>(MainViewModelState())
    val state : StateFlow<MainViewModelState> = _state.stateIn(
        scope = viewModelScope,
        initialValue = MainViewModelState(),
        started = SharingStarted.WhileSubscribed(5000)
    )


    private fun onEvent(event: MainViewModelEvent){
        when(event){
            is MainViewModelEvent.BeginEvent -> { _uiState.update{ MainViewModelUiState.NameScreen } }
            is MainViewModelEvent.UpdateNameOne -> { _state.update{ it.copy(nameOne = event.name )}}
            is MainViewModelEvent.UpdateNameTwo -> { _state.update{ it.copy(nameTwo = event.name )}}
            is MainViewModelEvent.StartGameEvent -> { _uiState.update{ MainViewModelUiState.GameScreen }}
        }
    }


    fun goToNameScreen(){
        onEvent(MainViewModelEvent.BeginEvent)
    }

    fun updateNameOne(name: String){
        onEvent(MainViewModelEvent.UpdateNameOne(name))
    }
    fun updateNameTwo(name: String){
        onEvent(MainViewModelEvent.UpdateNameTwo(name))
    }

    fun goToGameScreen(){
        onEvent(MainViewModelEvent.StartGameEvent)
    }

    fun selectTile(index: Int){

        _state.update{
            if(it.tiles[index] == 0){
                it.copy(tiles = it.tiles.mapIndexed{ index, value -> if(index == 0){ return@mapIndexed 0 } else value})
            }
            else it
        }


    }

}

data class MainViewModelState(
    val nameOne: String = "",
    val nameTwo: String = "",
    val tiles: List<Int> = listOf(0,0,0,0,0,0,0,0,0)
)

sealed interface MainViewModelEvent {
    object BeginEvent: MainViewModelEvent
    data class UpdateNameOne(val name: String): MainViewModelEvent
    data class UpdateNameTwo(val name: String): MainViewModelEvent
    object StartGameEvent: MainViewModelEvent
}

sealed interface MainViewModelUiState {
    object TitleScreen: MainViewModelUiState
    object NameScreen: MainViewModelUiState
    object GameScreen: MainViewModelUiState
}