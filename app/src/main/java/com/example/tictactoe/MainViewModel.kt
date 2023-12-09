package com.example.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.Serializable

class MainViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<MainViewModelUiState>(MainViewModelUiState.TitleScreen)
    val uiState: StateFlow<MainViewModelUiState> = _uiState.stateIn(
        scope = viewModelScope, initialValue =
        MainViewModelUiState.TitleScreen,
        started = WhileSubscribed(5000)
    )

    private val _state = MutableStateFlow<MainViewModelState>(MainViewModelState())
    val state: StateFlow<MainViewModelState> = _state.stateIn(
        scope = viewModelScope, initialValue =
        MainViewModelState(),
        started = WhileSubscribed(5000)
    )

    private fun onEvent(event: MainViewModelEvent){
        when(event){
            is MainViewModelEvent.BeginEvent -> { _uiState.update{ MainViewModelUiState.NameScreen } }
            is MainViewModelEvent.NameOneUpdateEvent -> { _state.update{ it.copy(nameOne = event.name) } }
            is MainViewModelEvent.NameTwoUpdateEvent -> { _state.update{ it.copy(nameTwo = event.name) } }
            is MainViewModelEvent.GameStartEvent -> { _uiState.update{ MainViewModelUiState.GameScreen } }
            is MainViewModelEvent.TileUpdateEvent -> { selectTile(event.index) }
            is MainViewModelEvent.GameFinishedEvent -> { /* show winning dialogs */ }
        }
    }

    fun goToNameScreen(){
        onEvent(MainViewModelEvent.BeginEvent)
    }

    fun updateNameOne(name: String){
        onEvent(MainViewModelEvent.NameOneUpdateEvent(name))
    }

    fun updateNameTwo(name: String){
        onEvent(MainViewModelEvent.NameTwoUpdateEvent(name))
    }

    fun goToGameScreen(){
        onEvent(MainViewModelEvent.GameStartEvent)
    }

    fun updateTile(index: Int){
        onEvent(MainViewModelEvent.TileUpdateEvent(index))
    }

    fun selectTile(index: Int){

        _state.update {
            if(it.tiles[index].value == 0) {
                it.copy(
                    turn = !it.turn,
                    tiles = it.tiles.apply { this[index].value = if (it.turn) 1 else 2 },
                    finished = checkBoard())
            }

            else it
        }

        if(checkBoard()){
            finishGame()
        }
    }

    private fun checkBoard(): Boolean{
        //search board spaces...
        return false
    }

    private fun finishGame(){
        onEvent(MainViewModelEvent.GameFinishedEvent)
    }


}

data class TicTacToeTile(var value:Int = 0)

data class MainViewModelState (
    val nameOne : String = "",
    val nameTwo : String = "",
    val tiles : List<TicTacToeTile> = mutableListOf<TicTacToeTile>().apply{ repeat(9){ this.add(
        TicTacToeTile()
    )}},
    val turn : Boolean = true,
    var finished: Boolean = false
) : Serializable

sealed interface MainViewModelEvent{
    object BeginEvent: MainViewModelEvent
    data class NameOneUpdateEvent(val name: String): MainViewModelEvent
    data class NameTwoUpdateEvent(val name: String): MainViewModelEvent
    object GameStartEvent: MainViewModelEvent
    data class TileUpdateEvent(val index: Int): MainViewModelEvent
    object GameFinishedEvent: MainViewModelEvent
}

sealed interface MainViewModelUiState{
    object TitleScreen: MainViewModelUiState
    object NameScreen: MainViewModelUiState
    object GameScreen: MainViewModelUiState
}