package no.hiof.reciperiot.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: String = "",
    val title: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val imageURL: String = "https://www.healthylifestylesliving.com/wp-content/uploads/2015/12/placeholder-256x256.gif",
    val cookingTime: String = "",
    var favourite: Boolean = true,
    var recipe_instructions: String = "",
    var recipe_nutrition: String = "",
    var userid: String = ""

)