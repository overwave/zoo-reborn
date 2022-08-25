package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Appearance
import dev.overwave.draw.LinearAnimatable
import dev.overwave.draw.ofDirection
import dev.overwave.texture.BoxTexture
import dev.overwave.texture.boxTexture
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2
import java.util.*

class Box(
    position: IntVector2,
    val direction: IntVector2,
    val type: BoxType,
    val static: Boolean = false
) : Actor {
    var position = position
        private set

    private var animation: LinearAnimatable<Vector2>? = null
    private var callback: (() -> Unit)? = null

    private var clickable = false

    override var appearance = updateAppearance()
        private set

    override fun getPosition() = position.vector2 + (animation?.state ?: Vector2.ZERO)

    override fun getRotation(): Double {
        if (static || !hovered) return 0.0

        return when (direction) {
            IntVector2(1, 0) -> 0.0
            IntVector2(-1, 0) -> 180.0
            IntVector2(0, 1) -> 90.0
            IntVector2(0, -1) -> 270.0
            else -> throw IllegalStateException()
        }
    }

    override val size = Vector2(1.0)

    override var hovered = false
        private set

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return other is Box && type == other.type && position == other.position
    }

    override fun hashCode() = Objects.hash(type, position)
}
