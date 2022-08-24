package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Appearance
import dev.overwave.draw.LinearAnimatable
import dev.overwave.draw.ofDirection
import dev.overwave.texture.BoxTexture
import dev.overwave.texture.boxTexture
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2

class Box(position: IntVector2, private val direction: IntVector2, private val type: BoxType) : Actor {
    var position = position
        private set

    private var animation: LinearAnimatable<Vector2>? = null
    private var callback: (() -> Unit)? = null

    private var clickable = false

    override var appearance = updateAppearance()
        private set

    override fun getPosition() = position.vector2 + (animation?.state ?: Vector2.ZERO)

    override fun getRotation(): Double {
        if (!hovered) return 0.0

        return when (direction) {
            IntVector2(1, 0) -> 0.0
            IntVector2(-1, 0) -> 180.0
            IntVector2(0, 1) -> 90.0
            IntVector2(0, -1) -> 270.0
            else -> throw IllegalArgumentException()
        }
    }

    override val size = Vector2(1.0)

    override var hovered = false

    override fun update(seconds: Double, deltaTime: Double) {
        animation?.updateAnimation()

        if (animation?.hasAnimations() == false) {
            callback?.invoke()
            animation = null
            callback = null
        }
    }

    fun move(steps: Int = 1, _callback: () -> Unit = {}) {
        val direction = direction * steps
        animation = ofDirection(direction)
        callback = _callback
        position += direction
    }

    fun hover(_clickable: Boolean) {
        hovered = true
        clickable = _clickable
        appearance = updateAppearance()
    }

    fun unhover() {
        hovered = false
        clickable = true
        appearance = updateAppearance()
    }

    private fun updateAppearance(): Appearance {
        val texture = if (hovered) {
            if (clickable)
                BoxTexture.ARROW
            else
                BoxTexture.CROSS
        } else {
            BoxTexture.STATIC
        }
        return Appearance(boxTexture(type, texture))
    }
}
