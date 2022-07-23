package uff.br.tcc.extensions

import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.Edge
import uff.br.tcc.helper.buildGson
import java.util.stream.Collectors

fun Diagram.deepCopy(): Diagram {
    val gson = buildGson()
    return gson.fromJson(gson.toJson(this), this::class.java)
}

fun Diagram.getEdgesWithSpecificNode(nodeName: String, position: String): MutableList<Edge> =
    edges.stream().filter {
        nodeName == when (position) {
            "LEFT" -> it.leftNode.name
            else -> it.rightNode.name
        }
    }.collect(Collectors.toList())
