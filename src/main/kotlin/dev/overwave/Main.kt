package dev.overwave

import dev.overwave.game.Field
import org.openrndr.UnfocusBehaviour
import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.Vector4
import org.openrndr.math.transforms.lookAt
import org.openrndr.math.transforms.perspective

class Main {
}

private const val BOX_SIZE = 64.0

private const val SHADER = """
                        vec3 lightDir = normalize(vec3(0.3, 0.4, 1.5));
                        float light = dot(va_normal, lightDir) * 0.4 + 0.5;
                        x_fill = texture(p_texture, va_texCoord0.xy);
                        x_fill.rgb *= light; 
                    """

fun castRay(point: Vector2, projection: Matrix44, view: Matrix44, width: Int, height: Int): Vector3 {
    val normalized = Vector4(2 * point.x / width - 1, 1 - 2 * point.y / height, -1.0, 1.0)
    val ipn = (projection.inversed * normalized).copy(z = -1.0, w = 0.0)
    return (view.inversed * ipn).xyz
}

private const val WIDTH = 1800
private const val HEIGHT = 1300

fun main() {
    val field = Field(8)
    val textures = mutableMapOf<String, ColorBuffer>()


    application {

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
        var picker = Vector2(0.0)

        program {
            val box = Mesh("data/meshes/box.obj")

            mouse.moved.listen {
                val ray = castRay(it.position, projection, view, WIDTH, HEIGHT)
                val point = cameraPosition - ray * ((cameraPosition.z - 0.5) / ray.z)
                picker = point.xy
            }


            extend {
                drawer.clear(ColorRGBa.WHITE)
                drawer.view = view
                drawer.projection = projection

                drawer.fill = ColorRGBa.PINK
                drawer.depthWrite = true
                drawer.depthTestPass = DepthTestPass.LESS_OR_EQUAL

                val actors = field.getActors()

                actors.forEach { actor ->
                    drawer.model = Matrix44.IDENTITY

                    val texturePath = actor.appearance.texture.path
                    val texture = textures.computeIfAbsent(texturePath, ::loadImage)
                    drawer.shadeStyle = shadeStyle {
                        fragmentTransform = SHADER
                        parameter("texture", texture)
                    }
//                }

                    drawer.translate(actor.positionDouble.xy0)
//                    drawer.translate(Vector3(actor.positionDouble.x, actor.positionDouble.y, -15.0))
//                    drawer.rotate(Vector3(0.0, 0.0, 1.0), 90.0)

//                    drawer.rotate(Vector3.UNIT_Z, seconds * 15)
//                    drawer.rotate(Vector3.UNIT_X, seconds * 5)
                    drawer.vertexBuffer(box.buffer, DrawPrimitive.TRIANGLES)
                }

                drawer.model = Matrix44.IDENTITY
                drawer.translate(picker)
                drawer.vertexBuffer(box.buffer, DrawPrimitive.TRIANGLES)
            }
        }
    }
}