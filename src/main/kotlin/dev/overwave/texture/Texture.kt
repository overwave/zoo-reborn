package dev.overwave.texture

class Texture(name: String, type: TextureType) {
    val path = type.toPath(name)
}

enum class TextureType(val toPath: (name: String) -> (String)) {
    BOX({
        val filename = it.lowercase().replace('_', '-')
        "data/meshes/box/${filename}_tex.png"
    })
}
