package dev.overwave.draw

import org.openrndr.math.Vector2

interface Animation {
    fun getState(): Vector2

    fun update()

    fun cancel()

    val empty: Boolean

    operator fun plus(animation: Animation): Animation
}
