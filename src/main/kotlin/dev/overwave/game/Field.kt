package dev.overwave.game

import dev.overwave.draw.Actor
import dev.overwave.draw.Drawable
import org.openrndr.math.IntVector2


class Field(colors: Int) : MouseListener, Drawable {
    private val boxSuppliers: Map<IntVector2, BoxSupplier>
    private val grid = Grid()
    private val movingBoxes = mutableListOf<Box>()

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
                val (collided, _) = grid.findIntersection(supplier.position, supplier.direction)
                if (!collided) {
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
        val (collided, steps) = (grid.findIntersection(supplier.position, supplier.direction))
        if (!collided) return

        val box = supplier.retrieve()
        movingBoxes.add(box)
        box.move(steps, ::moveEnded)

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

    private fun moveEnded(box: Box) {
        println(movingBoxes)
        movingBoxes.remove(box)

        if (movingBoxes.isEmpty()) {
            makeTurn()
        }
    }

    private fun makeTurn() {
//        println("made turn")
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
//        println("static boxes pushed")
        val boxesToRemove = mutableListOf<Box>()
        if (movingBoxes.isNotEmpty()) throw IllegalStateException("no moving boxes when pushing allowed!")

        for (box in grid.getBoxes()) {
            if (box.static) continue

            val (collided, steps) = grid.findIntersection(box.position, box.direction)

            if (steps == 0) continue

            box.move(steps, ::moveEnded)
            if (!collided) {
//                println("box pushed to a supplier")
                boxSuppliers[box.position]?.push(box, steps) ?: throw IllegalStateException("accepting supplier not found")
                boxesToRemove.add(box)
            }
            movingBoxes.add(box)
        }
//        println("removed ${boxesToRemove.size} boxes")
        grid.remove(boxesToRemove)
    }
}