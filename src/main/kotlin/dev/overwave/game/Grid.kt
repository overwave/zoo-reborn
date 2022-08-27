package dev.overwave.game

import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2
import java.util.stream.Collectors

class Grid(level: Level) : Drawable {
    private val boxes : MutableList<Box> = level.boxes.stream()
        .map { (position, type) ->
            Box(position, IntVector2.ZERO, type, static = true)
        }.collect(Collectors.toCollection(::ArrayList))

    override fun getActors() = boxes

    fun getBoxes(): List<Box> = boxes

    fun getBoxAt(position: IntVector2): Box? {
        return boxes.find { it.position == position }
    }

    fun add(box: Box) {
        if (getBoxAt(box.position) != null) {
            throw IllegalArgumentException("the required space is already occupied")
        }
        boxes.add(box)
    }

    fun findIntersection(from: IntVector2, direction: IntVector2): Pair<Boolean, Int> {
        for (i in 1..11) {
            val position = from + direction * i
            if (position.x > 9 || position.x < 0 || position.y > 9 || position.y < 0) {
                return false to i
            }

            val box = getBoxAt(position)
            if (box != null) {
                return if (i == 1) {
                    false to 0
                } else {
                    true to (i - 1)
                }
            }
        }
        throw IndexOutOfBoundsException()
    }

    fun remove(boxes: Collection<Box>) {
        this.boxes.removeAll(boxes)
    }
}
