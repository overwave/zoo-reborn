package dev.overwave.game

import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2

data class MouseEvent(
    val mousePosition: Vector2,
    val worldPosition: Vector2,
    val intPosition: IntVector2,
    val clampedIntPosition: IntVector2
)
