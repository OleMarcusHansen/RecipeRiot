package no.hiof.reciperiot.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: String = "",
    val title: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val imageURL: String = "https://oaidalleapiprodscus.blob.core.windows.net/private/org-CmrU9hQ7pdJBX7xOeLxsWe6T/user-VGLO1XEfBpsgy9KFlHwoBn4W/img-xGEMEzcwlRto5Hf9gJa42ueg.png?st=2023-11-06T12%3A46%3A11Z&se=2023-11-06T14%3A46%3A11Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-06T13%3A46%3A11Z&ske=2023-11-07T13%3A46%3A11Z&sks=b&skv=2021-08-06&sig=876gQTiFmaOR69AtVmP5mJ4ctPQT6ROOy5sdqXfQTlQ%3D",
    val cookingTime: String = "",
    var isFavourite: Boolean = true,
    var recipe_instructions: String = "",
    var userid: String = ""

)