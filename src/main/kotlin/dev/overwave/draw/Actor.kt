package dev.overwave.draw

import org.openrndr.math.Vector2

interface Actor {
    val positionDouble: Vector2

    val size: Vector2

    val pivot: Vector2
        get() = positionDouble + size / 2.0

    val hovered: Boolean

    val appearance: Appearance
}