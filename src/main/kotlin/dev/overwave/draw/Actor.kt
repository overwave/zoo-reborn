package dev.overwave.draw

import org.openrndr.math.Vector2

interface Actor {
    fun getPosition(): Vector2

    fun getRotation(): Double

    val size: Vector2

    val hovered: Boolean

    val appearance: Appearance

    fun update(seconds: Double, deltaTime: Double) {}
}