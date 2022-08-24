package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2

class Field(colors: Int) : MouseListener, Drawable {
    private val boxSuppliers: Map<IntVector2, BoxSupplier>
    private val grid = Grid()

    init {
        val suppliers = mutableListOf<BoxSupplier>()
        for (i in 0..9) {
            suppliers.add(BoxSupplier(IntVector2(i, -1), IntVector2(0, 1), colors))
            suppliers.add(BoxSupplier(IntVector2(10, i), IntVector2(-1, 0), colors))
            suppliers.add(BoxSupplier(IntVector2(i, 10), IntVector2(0, -1), colors))
            suppliers.add(BoxSupplier(IntVector2(-1, i), IntVector2(1, 0), colors))
        }
        boxSuppliers = suppliers.associateBy(BoxSupplier::position)
    }

    override fun getActors(): List<Actor> {
        val supplierActors = boxSuppliers.values.flatMap(BoxSupplier::getActors)
        return grid.getActors() + supplierActors
    }

    override fun mouseMove(event: MouseEvent) {
        for (supplier in boxSuppliers.values) {
            if (supplier.position == event.clampedIntPosition) {
                if (grid.iterate(supplier.position, supplier.direction) == null) {
                    supplier.hover(false)
                } else {
                    supplier.hover(true)
                }
            } else {
                supplier.unhover()
            }
        }
    }

    override fun mouseClick(event: MouseEvent) {
        val supplier = boxSuppliers[event.clampedIntPosition] ?: throw IllegalArgumentException("illegal position")

        val steps = grid.iterate(supplier.position, supplier.direction) ?: return

        val box = supplier.retrieve()
        box.move(steps, ::makeTurn)

        grid.add(box)
    }

    private fun makeTurn() {
        System.err.println("check for combo!")
        // var allIdle = true
        // for (cell in grid.cells) {
        //     if (cell.isBusy()) {
        //
        //     }
        // }
    }
}