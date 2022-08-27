package dev.overwave.draw

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2

private const val SPEED = 100L

class LinearAnimation(
    private val from: Vector2,
    private val to: Vector2,
    private val callback: () -> Unit,
    duration: Int = 1,
    delay: Int = 0
//    easing: Easing = Easing.None
) : Animatable(), Animation {
    private var animation = object : Animatable() {
        var state = from

    }

    init {
        animation.apply {
//            if (delay != 0) {
//                state = to
//                ::state.animate(to, delay* 2 * SPEED, Easing.None)
//                ::state.complete()
//            }
            val animationKey = ::state.animate(to, duration * SPEED, Easing.None)
            animationKey.completed.listen {
//                try {
                callback()
//                } catch (e: Throwable) {
//                    e.printStackTrace()
//                }
            }
            animationKey.cancelled.listen {
//                try {
                callback()
//                } catch (e: Throwable) {
//                    e.printStackTrace()
//                }
            }
//            animationKey.cancelled.listen {
//                System.err.println(it)
//            }
        }
//        val animationKey = animation::state.animate(to, duration * SPEED, Easing.None)
//            animationKey.cancelled.listen { callback() }
    }

    override fun getState() = animation.state

    override fun update() = animation.updateAnimation()

    override val empty = false

    override operator fun plus(animation: Animation): LinearAnimation {
        if (animation is EmptyAnimation) return this

        animation as LinearAnimation
        val from = this.animation.state + animation.animation.state
        val to = this.to + animation.to
        val distance = from.distanceTo(to).toInt()

        val composedCallback = {
            System.err.println("nested called")
            callback()
            animation.callback()
        }
        return LinearAnimation(from, to, composedCallback, distance)
    }

    override fun toString() = "Animation $from -> $to"
}

fun ofDirection(direction: IntVector2, callback: () -> Unit, delay: Int): LinearAnimation {
    val from = -direction.vector2
    val to = Vector2.ZERO
    val distance = from.distanceTo(to).toInt()
    return LinearAnimation(from, to, callback, distance, delay)
}
