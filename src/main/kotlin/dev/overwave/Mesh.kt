package dev.overwave

import de.javagl.obj.Obj
import de.javagl.obj.ObjFace
import de.javagl.obj.ObjReader
import org.openrndr.draw.BufferWriter
import org.openrndr.draw.VertexBuffer
import org.openrndr.draw.vertexBuffer
import org.openrndr.draw.vertexFormat
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import java.io.FileInputStream
import java.util.Random

class Mesh(path: String) {
    val buffer: VertexBuffer

    init {
        val obj = FileInputStream(path).use(ObjReader::read)

        val vertices = obj.numFaces * 3
        buffer = vertexBuffer(vertexFormat {
            position(3)
            textureCoordinate(2)
            normal(3)
        }, vertices)
        buffer.put {
            for (i in 0 until obj.numFaces) {
                val face = obj.getFace(i)
                if (face.numVertices != 3) {
                    throw IllegalArgumentException("non-triangular face found")
                }
                for (index in 0..2) {
                    writeVertex(obj, face, index)
                }
            }
        }
    }

    private fun BufferWriter.writeVertex(obj: Obj, face: ObjFace, index: Int) {
        val vertex = obj.getVertex(face.getVertexIndex(index))
        write(Vector3(vertex.x.toDouble(), vertex.y.toDouble(), vertex.z.toDouble()))

        val tex = obj.getTexCoord(face.getTexCoordIndex(index))
        write(Vector2(tex.x.toDouble(), tex.y.toDouble()))

        val normal = obj.getNormal(face.getNormalIndex(index))
        write(Vector3(normal.x.toDouble(), normal.y.toDouble(), normal.z.toDouble()))
    }
}