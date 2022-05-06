package uff.br.tcc.model

import uff.br.tcc.helper.buildGson
import java.util.stream.Collectors


data class DiagrammaticProof(val diagrams: MutableList<Diagram>)

data class Diagram(val nodes: MutableList<Node>, val edges: MutableList<Edge>, var stepDescription: String)

fun Diagram.deepCopy(): Diagram {
    val gson = buildGson()
    return gson.fromJson(gson.toJson(this), this::class.java)
}

fun Diagram.getEdgesWithSpecificNode(nodeName: String, position: String): MutableList<Edge> =
    edges.stream().filter {
        nodeName == when(position) {
            "LEFT" -> it.leftNode.name
            else -> it.rightNode.name
        }
    }.collect(Collectors.toList())