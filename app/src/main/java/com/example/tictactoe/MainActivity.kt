package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()
        var uiState: MainViewModelUiState by mutableStateOf(MainViewModelUiState.TitleScreen)

        lifecycleScope.launch{
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.uiState.onEach{ uiState = it }.collect{}
            }
        }

        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val state by mainViewModel.state.collectAsState()

                    when(uiState){
                        is MainViewModelUiState.TitleScreen -> { TitleScreen { mainViewModel.goToNameScreen() } }
                        is MainViewModelUiState.NameScreen -> { NameScreen(state.nameOne, {name -> mainViewModel.updateNameOne(name)},
                                                                            state.nameTwo, {name -> mainViewModel.updateNameTwo(name)},
                                                                            { mainViewModel.goToGameScreen() }) }
                        is MainViewModelUiState.GameScreen -> { GameScreen(state.tiles) { index -> mainViewModel.selectTile(index) } }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello edit $name!",
        modifier = modifier
    )
}

@Composable
fun TitleScreen(onClick: () -> Unit ){
    Column{
        Text(text="Tic Tac Toe")
        Button( modifier = Modifier,
            onClick = {onClick()}, content = {
            Text(text="Play")
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(value1: String, onValue1Change: (String) -> Unit, value2: String, onValue2Change: (String) -> Unit, onClick: () -> Unit){

    Column{
        TextField(value= value1, onValueChange = { onValue1Change(it)} )
        TextField(value= value2, onValueChange = { onValue2Change(it)} )
        Button( modifier = Modifier,
            onClick = { onClick()}, content = {
            Text(text="Play")
        })
    }


}

@Composable
fun Tile(tile: TicTacToeTile, onClick: () -> Unit){

    Box(Modifier.clickable { onClick() }) {
        when (tile.value) {
            1 -> {
                Text("X")
            }

            2 -> {
                Text("O")
            }

            else -> {
                Text("")
            }
        }
    }
}

@Composable
fun GameScreen(board: List<TicTacToeTile>, onClick: (Int) -> Unit){

    LazyVerticalGrid(columns= GridCells.Fixed(3)) {
        itemsIndexed(board) { index, item ->
            Tile(item) { onClick(index) }
        }
    }

}

@Preview
@Composable
fun PreviewTitleScreen(){
    TitleScreen({})
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TicTacToeTheme {
        Greeting("Android")
    }
}

@Preview
@Composable
fun GameScreenPreview(){
    GameScreen(board = mutableListOf<TicTacToeTile>().apply{ repeat(9){ this.add(
        TicTacToeTile()
    )}}, onClick = {})
}