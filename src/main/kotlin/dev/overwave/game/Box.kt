package dev.overwave.game

import dev.overwave.draw.*
import dev.overwave.texture.BoxTexture
import dev.overwave.texture.boxTexture
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2
import java.util.*

class Box(
    position: IntVector2,
    direction: IntVector2,
    val type: BoxType,
    val static: Boolean = false
) : Actor {
    var position = position
        private set
    var direction = direction
        private set

    private var animation: Animation = EmptyAnimation()
//    private var callback: ((Box) -> Unit) = {}

    private var clickable = false

    override var appearance = updateAppearance()
        private set

    //    override fun getPosition() = position.vector2
    override fun getPosition() = position.vector2 + animation.getState()

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
        animation.update()

//        checkAnimationEnd()
    }

    private fun onAnimationEnd(callback: (Box) -> Unit): () -> Unit {
        animation = EmptyAnimation()
//        if (animation.isNotEmpty() && position.y > 11) {
//            println(position)
//            println(animation)
//        }
//        if (animation.isNotEmpty() && !animation.hasAnimations()) {
//            println(this)
        return { callback.invoke(this) }
//            callback = _callback
//        }
    }

    fun move(steps: Int = 1, callback: (Box) -> Unit = {}, delay: Int = 0) {
//        checkAnimationEnd(_callback)

        val distance = direction * steps
        animation.update()

        animation += ofDirection(distance, onAnimationEnd(callback), delay)
//        if (position.y > 12) {
//            println(animation)
//        }
        position += distance
    }

    fun reverse() {
        direction *= -1
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

    override fun toString() = "Box [$type] ${position.x} ${position.y}"

    fun finishAnimation() {
        animation.cancel()
        animation = EmptyAnimation()
    }
}
