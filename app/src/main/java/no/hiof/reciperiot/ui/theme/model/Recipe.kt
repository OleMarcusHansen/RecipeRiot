package no.hiof.reciperiot.ui.theme.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: Int,
    val title: String,
    @DrawableRes val imageResourceId: Int,
    val cookingTime: String,
    var isFavourite: Boolean,

)