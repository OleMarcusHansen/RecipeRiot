import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.hiof.reciperiot.data.IngredientsRepository

class IngredientsViewModel : ViewModel() {

    private val repository = IngredientsRepository() // Create a repository for data operations

    val ingredientsList = repository.ingredientsList

    fun getIngredients() {
        viewModelScope.launch {
            repository.getIngredients()
        }
    }

    fun saveIngredients(ingredientsToSave: List<Pair<String, Boolean>>) {
        viewModelScope.launch {
            repository.saveIngredients(ingredientsToSave)
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