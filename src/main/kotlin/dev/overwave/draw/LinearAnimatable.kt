package dev.overwave.draw

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.math.IntVector2
import org.openrndr.math.LinearType
import org.openrndr.math.Vector2

private const val SPEED = 100

class LinearAnimatable<T : LinearType<T>>(
    var state: T,
    target: T,
    duration: Long = 1,
    easing: Easing = Easing.None
) : Animatable() {
    init {
        ::state.animate(target, duration * SPEED, easing)
    }
}

fun ofDirection(direction: IntVector2): LinearAnimatable<Vector2> {
    val from = -direction.vector2
    val to = Vector2.ZERO
    val distance = from.distanceTo(to)
    return LinearAnimatable(from, to, distance.toLong())
}
