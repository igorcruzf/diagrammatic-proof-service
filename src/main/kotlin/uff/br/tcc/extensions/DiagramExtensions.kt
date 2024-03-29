package uff.br.tcc.extensions

import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.term.ITerm
import uff.br.tcc.enum.Direction
import uff.br.tcc.helper.buildGson
import java.util.stream.Collectors

fun DiagrammaticProof.deepCopy(): DiagrammaticProof {
    val gson = buildGson()
    val counterModelRelations = diagrams.first().countermodelRelations
    return gson.fromJson(gson.toJson(this), this::class.java).also {
        it.diagrams.forEachIndexed { index, diagram ->
            diagram.countermodelRelations = this.diagrams[index].countermodelRelations
        }
        it.diagrams.first().countermodelRelations = counterModelRelations
    }
}

fun Diagram.deepCopy(): Diagram {
    val gson = buildGson()
    val copyCounterModelRelations = countermodelRelations
    val newDiagram = gson.fromJson(gson.toJson(this), this::class.java)
    newDiagram.countermodelRelations = copyCounterModelRelations
    return newDiagram
}

fun ITerm.deepCopy(): ITerm {
    val gson = buildGson()
    return gson.fromJson(gson.toJson(this), this::class.java)
}

fun Diagram.getEdgesWithSpecificNode(nodeName: String, position: Direction): MutableList<Edge> =
    edges.stream().filter {
        nodeName == when (position) {
            Direction.LEFT -> it.leftNode.name
            Direction.RIGHT -> it.rightNode.name
        }
    }.collect(Collectors.toList())
