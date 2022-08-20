package dev.overwave

import dev.overwave.game.Field
import dev.overwave.game.MouseEvent
import org.openrndr.UnfocusBehaviour
import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extras.color.presets.BLUE_STEEL
import org.openrndr.math.IntVector2
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.Vector4
import org.openrndr.math.clamp
import org.openrndr.math.transforms.lookAt
import org.openrndr.math.transforms.perspective
import kotlin.math.roundToInt

class Main {
}

private const val SHADER = """
                        vec3 lightDir = normalize(vec3(0.3, 0.4, 1.5));
                        float light = dot(va_normal, lightDir) * 0.4 + 0.5;
                        vec4 tex = texture(p_texture, va_texCoord0.xy);
                        tex.rgb *= light;
                        x_fill.rgb = mix(tex.rgb, p_color.rgb, p_color.a);
                    """

fun castRay(point: Vector2, projection: Matrix44, view: Matrix44, width: Int, height: Int): Vector3 {
    val normalized = Vector4(2 * point.x / width - 1, 1 - 2 * point.y / height, -1.0, 1.0)
    val ipn = (projection.inversed * normalized).copy(z = -1.0, w = 0.0)
    return (view.inversed * ipn).xyz
}

private const val WIDTH = 1200
private const val HEIGHT = 700

fun _loadImage(path:String): ColorBuffer {
    val image = loadImage(path)
    image.filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
    return image
}

fun main() {
    val textures = mutableMapOf<String, ColorBuffer>()
    // val hoverSubscribers = mutableListOf<Hoverable>()
    val field = Field(8)


    application {
        // hoverSubscribers.add(field)

        configure {
            width = WIDTH
            height = HEIGHT
            title = "Zoo Reborn"
            unfocusBehaviour = UnfocusBehaviour.THROTTLE
            multisample = WindowMultisample.SampleCount(4)
        }

        val cameraPosition = Vector3(4.5, 4.5, 15.0)
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
            }


            extend {
                drawer.clear(ColorRGBa.WHITE)
                drawer.view = view
                drawer.projection = projection

                drawer.depthWrite = true
                drawer.depthTestPass = DepthTestPass.LESS_OR_EQUAL

                val actors = field.getActors()

                actors.forEach { actor ->
                    drawer.model = Matrix44.IDENTITY

                    val texturePath = actor.appearance.texture.path
                    val texture = textures.computeIfAbsent(texturePath, ::_loadImage)
                    drawer.shadeStyle = shadeStyle {
                        fragmentTransform = SHADER
                        parameter("texture", texture)

                        val highlightColor = if (actor.hovered) ColorRGBa.BLUE_STEEL(a = 0.3) else ColorRGBa.TRANSPARENT
                        parameter("color", highlightColor)
                    }
//                }

                    drawer.translate(actor.positionDouble.xy0)
//                    drawer.translate(Vector3(actor.positionDouble.x, actor.positionDouble.y, -15.0))
//                    drawer.rotate(Vector3(1.0, 0.0, 1.0), 90.0)

//                    drawer.rotate(Vector3.UNIT_Z, seconds * 15)
//                    drawer.rotate(Vector3.UNIT_X, seconds * 5)
                    drawer.vertexBuffer(box.buffer, DrawPrimitive.TRIANGLES)
                }

                // drawer.model = Matrix44.IDENTITY
                // drawer.translate(picker)
                // drawer.vertexBuffer(box.buffer, DrawPrimitive.TRIANGLES)
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