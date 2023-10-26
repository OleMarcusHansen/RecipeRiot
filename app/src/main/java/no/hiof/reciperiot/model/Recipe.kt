package no.hiof.reciperiot.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: Int,
    val title: String,
    @DrawableRes val imageResourceId: Int,
    val cookingTime: String,
    var isFavourite: Boolean,
    var recipe_instructions: String

)