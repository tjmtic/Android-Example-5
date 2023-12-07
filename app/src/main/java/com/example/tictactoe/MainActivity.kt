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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.tictactoe.ui.theme.MyApplicationFoxTestTheme
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
            MyApplicationFoxTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val state by mainViewModel.state.collectAsState()

                    when(uiState){
                        is MainViewModelUiState.TitleScreen -> { TitleScreen { mainViewModel.goToNameScreen() }
                        }
                        is MainViewModelUiState.NameScreen -> { NameScreen() }
                        is MainViewModelUiState.GameScreen -> {
                            GameScreen(state.tiles) { index -> mainViewModel.selectTile(index) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun TitleScreen(onClick: () -> Unit ){
    Column{
        Text(text="Tic Tac Toe")
        Button( modifier = Modifier
            .height(100.dp)
            .weight(16F), onClick = {onClick()}, content = {
            Text(text="Play")
        })
    }
}

@Composable
fun NameScreen(){


}

@Composable
fun Tile(value: Int, onClick: () -> Unit){

    Box(Modifier.clickable { onClick() }) {
        when (value) {
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
fun GameScreen(board: List<Int>, onClick: (Int) -> Unit){

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
    MyApplicationFoxTestTheme {
        Greeting("Android")
    }
}