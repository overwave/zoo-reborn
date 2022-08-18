package dev.overwave.game

import dev.overwave.draw.Actor
import org.openrndr.math.IntVector2

class Field(colors: Int) : MouseListener {
    private val boxSuppliers: Map<IntVector2, BoxSupplier>
    private val grid: Grid
    private val actors: List<Actor>

    init {
        val suppliers = mutableListOf<BoxSupplier>()
        for (i in 0..9) {
            suppliers.add(BoxSupplier(IntVector2(i, -1), IntVector2(0, 1), colors))
            suppliers.add(BoxSupplier(IntVector2(10, i), IntVector2(-1, 0), colors))
            suppliers.add(BoxSupplier(IntVector2(i, 10), IntVector2(0, -1), colors))
            suppliers.add(BoxSupplier(IntVector2(-1, i), IntVector2(1, 0), colors))
        }
        boxSuppliers = suppliers.associateBy(BoxSupplier::position)
        actors = suppliers.flatMap(BoxSupplier::getActors)
    }

    fun getActors() = boxSuppliers.values.flatMap(BoxSupplier::getActors)

    override fun mouseMove(event: MouseEvent) {
        for (supplier in boxSuppliers.values) {
            if (supplier.position == event.clampedIntPosition) {
                supplier.hover()
            } else {
                supplier.unhover()
            }
        }
    }

    override fun mouseClick(event: MouseEvent) {
        val box = boxSuppliers[event.clampedIntPosition]?.retrieve()
            ?: throw IllegalArgumentException("illegal position")
        println(box)
    }
}