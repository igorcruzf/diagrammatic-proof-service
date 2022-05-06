package com.br.uff.tcc.service

import com.br.uff.tcc.enum.NodeTypeEnum
import com.br.uff.tcc.model.AtomicTerm
import com.br.uff.tcc.model.Diagram
import com.br.uff.tcc.model.Edge
import com.br.uff.tcc.model.getEdgesWithSpecificNode

class HomomorphismValidator(
    private val leftDiagram: Diagram,
    private val rightDiagram: Diagram) {

    fun validate() = validate(this.leftDiagram.nodes.first().name, rightDiagram.nodes.first().name)

    private fun validate(
        leftDiagramNodeName: String,
        rightDiagramNodeName: String,
        edgesPath: List<Edge>? = null,
    ): Boolean {

        return validateEdgesInNode(rightDiagramNodeName, leftDiagramNodeName, position = "LEFT", edgesPath)
                && validateEdgesInNode(rightDiagramNodeName, leftDiagramNodeName, position = "RIGHT", edgesPath)
    }

    private fun validateEdgesInNode(
        rightDiagramNodeName: String,
        leftDiagramNodeName: String,
        position: String,
        edgesPath: List<Edge>?,
    ): Boolean {
        val rightDiagramEdges = rightDiagram.getEdgesWithSpecificNode(nodeName = rightDiagramNodeName, position = position)
        val leftDiagramEdges = leftDiagram.getEdgesWithSpecificNode(nodeName = leftDiagramNodeName, position = position)

        val validEdges = rightDiagramEdges.count {
                edge ->
                    edge.isMapped
                    || edgesPath?.contains(edge) ?: false
                    || hasAnyValidEdgeInLeftDiagram(leftDiagramEdges, edge, edgesPath)
        }

        return validEdges == rightDiagramEdges.count()
    }

    private fun hasAnyValidEdgeInLeftDiagram(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>?,
    ): Boolean {
        val isMapped = leftDiagramEdges.any{ leftDiagramEdge ->
            val thisEdgePath = edgesPath?.plus(rightDiagramEdge) ?: listOf(rightDiagramEdge)

            (leftDiagramEdge.term as AtomicTerm).name == (rightDiagramEdge.term as AtomicTerm).name
            && validateNodeType(rightDiagramEdge, leftDiagramEdge)
            && validate(leftDiagramEdge.rightNode.name, rightDiagramEdge.rightNode.name, thisEdgePath)
            && validate(leftDiagramEdge.leftNode.name, rightDiagramEdge.leftNode.name, thisEdgePath)
        }

        rightDiagramEdge.isMapped = isMapped
        return isMapped
    }

    private fun validateNodeType(rightDiagramEdge: Edge, leftDiagramEdge: Edge): Boolean {
        return (rightDiagramEdge.leftNode.nodeType == leftDiagramEdge.leftNode.nodeType
                        || rightDiagramEdge.leftNode.nodeType == NodeTypeEnum.INTERMEDIATE)
                && (rightDiagramEdge.rightNode.nodeType == leftDiagramEdge.rightNode.nodeType
                        || rightDiagramEdge.rightNode.nodeType == NodeTypeEnum.INTERMEDIATE)
    }
}