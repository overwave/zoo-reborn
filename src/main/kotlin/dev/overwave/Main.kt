package dev.overwave

import dev.overwave.game.Field
import dev.overwave.game.MouseEvent
import org.openrndr.UnfocusBehaviour
import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.DepthTestPass
import org.openrndr.draw.DrawPrimitive
import org.openrndr.draw.MagnifyingFilter
import org.openrndr.draw.MinifyingFilter
import org.openrndr.draw.ShadeStyle
import org.openrndr.draw.shadeStyle
import org.openrndr.math.IntVector2
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.Vector4
import org.openrndr.math.clamp
import org.openrndr.math.transforms.lookAt
import org.openrndr.math.transforms.perspective
import kotlin.math.roundToInt
import org.openrndr.draw.loadImage as openRndrLoadImage


private const val SHADER_BEGIN = """
                        vec4 lightPosition = vec4(p_lightPosition, 1.0);
                        vec4 positionWorldSpace = u_modelMatrix * vec4(va_position, 1.0);
                        vec3 lightDir = normalize(lightPosition - positionWorldSpace).xyz;
                        vec3 normal = (u_modelNormalMatrix * vec4(va_normal, 1.0)).xyz;
                        float light = dot(normal, lightDir) * 0.4 + 0.5;
                        """
private const val SHADER_END = """
                        x_fill.rgb = tex.rgb * light;
                        """

fun castRay(point: Vector2, projection: Matrix44, view: Matrix44, width: Int, height: Int): Vector3 {
    val normalized = Vector4(2 * point.x / width - 1, 1 - 2 * point.y / height, -1.0, 1.0)
    val ipn = (projection.inversed * normalized).copy(z = -1.0, w = 0.0)
    return (view.inversed * ipn).xyz
}

private const val WIDTH = 1200
private const val HEIGHT = 700

fun loadImage(path: String): ColorBuffer {
    val image = openRndrLoadImage(path)
    image.filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
    return image
}

fun compileShader(textures: Int): ShadeStyle {
    val builder = StringBuilder(
        """
        vec4 tex = texture(p_texture_0, va_texCoord0.xy);
        vec4 tempTex;
        """
    )
    for (i in 1 until textures) {
        builder.append(
            """
            tempTex = texture(p_texture_$i, va_texCoord0.xy);
            tex.rgb = mix(tex.rgb, tempTex.rgb, tempTex.a);
            """
        )
    }
    val shaderCode = SHADER_BEGIN + builder + SHADER_END
    return shadeStyle { fragmentTransform = shaderCode }
}

fun main() {
    val textures = mutableMapOf<String, ColorBuffer>()
    val shaders = Array(10, ::compileShader).toList()

    val field = Field(3)

    application {

        configure {
            width = WIDTH
            height = HEIGHT
            title = "Zoo Reborn"
            unfocusBehaviour = UnfocusBehaviour.THROTTLE
            multisample = WindowMultisample.SampleCount(4)
        }

        val cameraPosition = Vector3(4.5, 4.5, 15.0)
        val lightPosition = Vector3(7.0, 5.0, 15.0)
        val view = lookAt(cameraPosition, Vector3(4.5, 4.5, 0.0))
        val projection = perspective(60.0, WIDTH.toDouble() / HEIGHT, 0.01, 1000.0)

        program {
            val box = Mesh("data/meshes/box.obj")

            mouse.moved.listen {
                val mouseEvent = getMouseEvent(it, projection, view, cameraPosition)
                field.mouseMove(mouseEvent)
            }
            mouse.buttonUp.listen {
                val mouseEvent = getMouseEvent(it, projection, view, cameraPosition)
                field.mouseClick(mouseEvent)
                field.mouseMove(mouseEvent)
            }

            extend {
                drawer.clear(ColorRGBa.WHITE)
                drawer.view = view
                drawer.projection = projection

                drawer.depthWrite = true
                drawer.depthTestPass = DepthTestPass.LESS_OR_EQUAL

                field.getActors().forEach { actor ->
                    actor.update(seconds, deltaTime)

                    drawer.model = Matrix44.IDENTITY

                    val actorTextures = actor.appearance.textures
                    val shader = shaders[actorTextures.size]

                    shader.parameter("lightPosition", lightPosition)

                    for ((index, texture) in actorTextures.withIndex()) {
                        val loadedTexture = textures.computeIfAbsent(texture.filename, ::loadImage)
                        shader.parameter("texture_$index", loadedTexture)
                    }
                    drawer.shadeStyle = shader

                    // val highlightColor = if (actor.hovered) ColorRGBa.BLUE_STEEL(a = 0.3) else ColorRGBa.TRANSPARENT
                    // shader.parameter("color", highlightColor)


                    drawer.translate(actor.getPosition())
                    drawer.rotate(actor.getRotation())
                    // drawer.rotate(Vector3.UNIT_Y,seconds * 10)

                    drawer.vertexBuffer(box.buffer, DrawPrimitive.TRIANGLES)
                }
            }
        }
    }
}

private fun getMouseEvent(
    nativeEvent: org.openrndr.MouseEvent,
    projection: Matrix44,
    view: Matrix44,
    cameraPosition: Vector3
): MouseEvent {
    val ray = castRay(nativeEvent.position, projection, view, WIDTH, HEIGHT)
    val point = cameraPosition - ray * ((cameraPosition.z - 0.5) / ray.z)

    val intPosition = IntVector2(point.x.roundToInt(), point.y.roundToInt())
    val clampedIntPosition = intPosition.clamp(IntVector2(-1, -1), IntVector2(10, 10))
    return MouseEvent(nativeEvent.position, point.xy, intPosition, clampedIntPosition)
}