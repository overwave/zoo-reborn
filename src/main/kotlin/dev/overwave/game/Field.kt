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
                val intersection = grid.findIntersection(supplier.position, supplier.direction) ?: 0
                if (intersection == 0) {
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
        val supplier = boxSuppliers[event.clampedIntPosition] ?: return
        val intersection = (grid.findIntersection(supplier.position, supplier.direction)) ?: 0
        if (intersection == 0) return

        val box = supplier.retrieve()
        box.move(intersection, ::makeTurn)

        grid.add(box)
    }

    private fun getNeighbors(cell: Box): List<Box> {
        return listOfNotNull(
            grid.getBoxAt(cell.position + IntVector2.UNIT_X),
            grid.getBoxAt(cell.position + IntVector2.UNIT_Y),
            grid.getBoxAt(cell.position - IntVector2.UNIT_X),
            grid.getBoxAt(cell.position - IntVector2.UNIT_Y),
        ).filter { it.type == cell.type }
    }

    private fun makeTurn() {
        grid.remove(getGroupedBoxes())

        pushNotStaticBoxes()
    }

    private fun getGroupedBoxes(): Set<Box> {
        val checkedBoxes = mutableSetOf<Box>()
        val groupedBoxes = mutableSetOf<Box>()

        for (box in grid.getBoxes()) {
            if (checkedBoxes.contains(box)) continue

            val currentGroup = mutableSetOf(box)
            val boxesToCheck = ArrayDeque(listOf(box))

            while (boxesToCheck.isNotEmpty()) {
                val neighbors = getNeighbors(boxesToCheck.removeLast())
                for (neighbor in neighbors) {
                    if (currentGroup.add(neighbor)) {
                        boxesToCheck.add(neighbor)
                    }
                }
            }

            checkedBoxes.addAll(currentGroup)
            if (currentGroup.size > 2) {
                groupedBoxes.addAll(currentGroup)
            }
        }

        return groupedBoxes
    }

    private fun pushNotStaticBoxes() {
        for ((index, box) in grid.getBoxes().withIndex()) {
            if (box.static) continue

            val intersection = grid.findIntersection(box.position, box.direction)
            if (intersection == null) {
                TODO("push to supplier")
            } else if (intersection == 0) {
                continue
            }

            if (index != grid.getBoxes().size - 1) {
                box.move(intersection)
            } else {
                box.move(intersection, ::makeTurn)
            }
        }
    }
}