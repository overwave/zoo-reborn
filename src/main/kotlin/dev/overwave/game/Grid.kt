package dev.overwave.game

import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2

class Grid : Drawable {
    private val boxes = mutableListOf<Box>(Box(IntVector2(3, 3), IntVector2.ZERO, BoxType.JELLYFISH, static = true))

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

    fun findIntersection(from: IntVector2, direction: IntVector2): Int? {
        for (i in 1..10) {
            val position = from + direction * i
            if (position.x > 9 || position.x < 0 || position.y > 9 || position.y < 0) {
                return null
            }

            val box = getBoxAt(position)
            if (box != null) {
                return i - 1
            }
        }
        return null
    }

    fun remove(boxes: Collection<Box>) {
        this.boxes.removeAll(boxes)
    }
}
