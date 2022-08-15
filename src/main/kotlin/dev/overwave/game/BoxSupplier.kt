package dev.overwave.game

import dev.overwave.draw.Actor
import org.openrndr.math.IntVector2
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors
import java.util.stream.Stream

const val BOX_SUPPLIER_QUEUE_SIZE = 10L

class BoxSupplier(private val position: IntVector2, private val direction: IntVector2, private val colors: Int) {

    var shifted = false;

    private val queue: Queue<Box> = Stream.iterate(position) { it - direction }
        .limit(BOX_SUPPLIER_QUEUE_SIZE)
        .map { it to getRandomBoxType(colors) }
        .map { (pos, type) -> Box(pos, type) }
        .collect(Collectors.toCollection(::LinkedList))

    fun retrieve(): Box {
        val box = Box(position - direction * 10, getRandomBoxType(colors))
        queue.add(box)
        shifted = true
        return queue.poll() ?: throw IllegalStateException()
    }

    fun shift() {
        queue.forEach { it.move(direction) }
    }

    fun getActors(): List<Actor> {
        return queue.stream()
            .limit(3L)
            .toList()
    }


}

private fun getRandomBoxType(boundary: Int) = BoxType.values()[ThreadLocalRandom.current().nextInt(boundary)]