package no.hiof.reciperiot.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: Int = 0,
    val title: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val cookingTime: String = "",
    var isFavourite: Boolean = false,
    var recipe_instructions: String = ""

)