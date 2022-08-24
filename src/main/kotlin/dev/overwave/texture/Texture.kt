package dev.overwave.texture

class Texture(type: TextureType, textureName: String) {
    val filename = "data/meshes/${type.name.lowercase()}/${textureName.lowercase()}.png"
}

enum class TextureType {
    BOX
}
