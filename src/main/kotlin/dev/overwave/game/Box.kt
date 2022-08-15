package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Appearance
import dev.overwave.texture.Texture
import dev.overwave.texture.TextureType
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2

class Box(private var position: IntVector2, private val type: BoxType) : Actor {
    override val positionDouble = position.vector2

    override val size = Vector2(1.0)

    override val appearance: Appearance =
        object : Appearance() {
            override val texture = Texture(type.name, TextureType.BOX)
        }

    fun move(distance: IntVector2) {
        position += distance
    }
}
