package dev.overwave.draw

import org.openrndr.math.Vector2

class EmptyAnimation : Animation {
    override fun getState() = Vector2.ZERO

    override fun update() {}

    override fun cancel() {}

    override val empty = true

    override fun plus(animation: Animation) = animation
}