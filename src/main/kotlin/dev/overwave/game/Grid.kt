package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2

class Grid : Drawable {
    val cells = mutableListOf<Box>(Box(IntVector2(3, 3), IntVector2.ZERO, BoxType.MACAW))

    override fun getActors(): List<Actor> {
        return cells
    }

    fun getBoxAt(position: IntVector2): Box? {
        return cells.find { it.position == position }
    }

    fun add(box: Box) {
        if (getBoxAt(box.position) != null) {
            throw IllegalArgumentException("the required space is already occupied")
        }
        cells.add(box)
    }

    fun iterate(from: IntVector2, direction: IntVector2): Int? {
        for (i in 1..10) {
            val position = from + direction * i
            if (position.x > 9 || position.x < 0 || position.y > 9 || position.y < 0) {
                return null
            }

            val box = getBoxAt(position)
            if (box != null) {
                return if (i == 1) null else i - 1
            }
        }
        return null
    }
}
