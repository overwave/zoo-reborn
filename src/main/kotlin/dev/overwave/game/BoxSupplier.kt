package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors
import java.util.stream.Stream

const val BOX_SUPPLIER_QUEUE_SIZE = 10L

class BoxSupplier(val position: IntVector2, val direction: IntVector2, private val colors: Int) : Drawable {

    private val queue: Queue<Box> = Stream.iterate(position) { it - direction }
        .limit(BOX_SUPPLIER_QUEUE_SIZE)
        .map { it to getRandomBoxType(colors) }
        .map { (pos, type) -> Box(pos, direction, type) }
        .collect(Collectors.toCollection(::LinkedList))

    fun retrieve(): Box {
        val ejectedBox = queue.poll() ?: throw IllegalStateException()

        hover(true)
        val newBox = Box(position - direction * 10, direction, getRandomBoxType(colors))
        queue.add(newBox)
        queue.forEach(Box::move)

        return ejectedBox
    }

    override fun getActors(): List<Actor> {
        return queue.stream()
            .limit(3L)
            .toList()
    }

    fun hover(active: Boolean) {
        queue.peek().hover(active)
    }

    fun unhover() {
        queue.peek().unhover()
    }
}

private fun getRandomBoxType(boundary: Int) = BoxType.values()[ThreadLocalRandom.current().nextInt(boundary)]