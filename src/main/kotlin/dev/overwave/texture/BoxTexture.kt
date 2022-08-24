package dev.overwave.texture

import dev.overwave.game.BoxType

enum class BoxTexture {
    STATIC,
    ARROW,
    CROSS
}

fun boxTexture(type: BoxType, boxTexture: BoxTexture): Array<Texture> {
    val base = Texture(TextureType.BOX, "${type.name}_base")
    val layer = when (boxTexture) {
        BoxTexture.STATIC -> Texture(TextureType.BOX, type.name)
        else -> Texture(TextureType.BOX, boxTexture.name)
    }
    return arrayOf(base, layer)
}
