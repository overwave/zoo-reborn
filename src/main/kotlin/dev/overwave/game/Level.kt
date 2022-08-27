package dev.overwave.game

import org.openrndr.math.IntVector2
import java.io.File

class Level(level: Int) {

    val colors: Int

    val boxes: List<Pair<IntVector2, BoxType>>

    init {
        val lines = File("data/levels/level_$level.dat").readLines()

        colors = lines[0].toInt()
        boxes = lines.stream()
            .skip(1)
            .map { it.split(" ") }
            .map { (x, y, type) -> IntVector2(x.toInt(), y.toInt()) to BoxType.valueOf(type) }
            .toList()
    }
}
