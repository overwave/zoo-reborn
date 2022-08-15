package dev.overwave.game

import org.openrndr.math.IntVector2

class Field(colors: Int) {
    private val boxSuppliers: List<BoxSupplier>

    init {
        val suppliers = mutableListOf<BoxSupplier>()
        for (i in 0..9) {
            suppliers.add(BoxSupplier(IntVector2(i, -1), IntVector2(0, 1), colors))
        }
        for (i in 0..9) {
            suppliers.add(BoxSupplier(IntVector2(10, i), IntVector2(-1, 0), colors))
        }
        for (i in 9 downTo 0) {
            suppliers.add(BoxSupplier(IntVector2(i, 10), IntVector2(0, -1), colors))
        }
        for (i in 9 downTo 0) {
            suppliers.add(BoxSupplier(IntVector2(-1, i), IntVector2(1, 0), colors))
        }
        this.boxSuppliers = suppliers
    }

    fun getActors() = boxSuppliers.flatMap(BoxSupplier::getActors)
}