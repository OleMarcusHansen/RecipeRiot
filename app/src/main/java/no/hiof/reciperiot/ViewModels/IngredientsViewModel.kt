package no.hiof.reciperiot.ViewModels


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.hiof.reciperiot.data.IngredientsRepository

class IngredientsViewModel : ViewModel() {

    var newIngredient by mutableStateOf("")
    var ingredientsList by mutableStateOf(emptyList<Pair<String, MutableState<Boolean>>>())

    private val repository = IngredientsRepository() // Create a repository for data operations


    fun getIngredients() {
        viewModelScope.launch {
            repository.getIngredients()
        }
    }

    fun saveIngredients(ingredientsToSave: List<Pair<String, Boolean>>) {
        viewModelScope.launch {
            repository.saveCheckedStatesOfIngredients(ingredientsToSave)
        }
    }

    fun getIngredientsForHomeScreen() {
        viewModelScope.launch {
            repository.getIngredientsForHomeScreen()
        }
    }

    fun deleteIngredient(ingredientName: String) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredientName)
        }
    }
}