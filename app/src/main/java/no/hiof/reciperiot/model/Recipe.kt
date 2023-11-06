package no.hiof.reciperiot.model

import androidx.annotation.DrawableRes

data class Recipe(
    val id: Int = 0,
    val title: String = "",
    @DrawableRes val imageResourceId: Int = 0,
    val imageURL: String = "https://oaidalleapiprodscus.blob.core.windows.net/private/org-CmrU9hQ7pdJBX7xOeLxsWe6T/user-VGLO1XEfBpsgy9KFlHwoBn4W/img-LaXblXBRSWv6n3515EuRzY3I.png?st=2023-11-06T11%3A30%3A43Z&se=2023-11-06T13%3A30%3A43Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-06T10%3A20%3A17Z&ske=2023-11-07T10%3A20%3A17Z&sks=b&skv=2021-08-06&sig=DsiP7YT4Lee4mAe4r9sPqZOBH9vFe2kE5SoxcwY7PTo%3D",
    val cookingTime: String = "",
    var isFavourite: Boolean = true,
    var recipe_instructions: String = ""

)