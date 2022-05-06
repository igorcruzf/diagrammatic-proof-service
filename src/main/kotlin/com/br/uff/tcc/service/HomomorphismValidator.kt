package com.br.uff.tcc.service

import com.br.uff.tcc.enum.NodeTypeEnum
import com.br.uff.tcc.model.AtomicTerm
import com.br.uff.tcc.model.Diagram
import com.br.uff.tcc.model.Edge
import com.br.uff.tcc.model.getEdgesWithSpecificNode

class HomomorphismValidator {
    private lateinit var leftDiagram: Diagram
    private lateinit var rightDiagram: Diagram

    fun validate(leftDiagram: Diagram, rightDiagram: Diagram): Boolean{
        this.leftDiagram = leftDiagram
        this.rightDiagram = rightDiagram
        return validate(leftDiagram.nodes.first().name, rightDiagram.nodes.first().name)
    }

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
                    || isPossibleToMapToLeftDiagram(
                        leftDiagramEdges = leftDiagramEdges,
                        rightDiagramEdge = edge,
                        edgesPath = edgesPath
                    )
        }

        return validEdges == rightDiagramEdges.count()
    }

    private fun isPossibleToMapToLeftDiagram(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>?,
    ): Boolean {
        val newEdgesPath = edgesPath?.plus(rightDiagramEdge) ?: listOf(rightDiagramEdge)

        val possibleEdgeImages = leftDiagramEdges.filter{
                leftDiagramEdge ->
                    (leftDiagramEdge.term as AtomicTerm).name == (rightDiagramEdge.term as AtomicTerm).name
                    && validateNodeType(rightDiagramEdge, leftDiagramEdge)
                    && validate(leftDiagramEdge.rightNode.name, rightDiagramEdge.rightNode.name, newEdgesPath)
                    && validate(leftDiagramEdge.leftNode.name, rightDiagramEdge.leftNode.name, newEdgesPath)
        }

        if(possibleEdgeImages.isNotEmpty()){
            rightDiagramEdge.isMapped = true
            rightDiagramEdge.leftNode.nodeImageName = possibleEdgeImages.first().leftNode.name
            rightDiagramEdge.rightNode.nodeImageName = possibleEdgeImages.first().rightNode.name
        }
        return possibleEdgeImages.isNotEmpty()
    }

    private fun validateNodeType(rightDiagramEdge: Edge, leftDiagramEdge: Edge): Boolean {
        return (rightDiagramEdge.leftNode.nodeType == leftDiagramEdge.leftNode.nodeType
                        || rightDiagramEdge.leftNode.nodeType == NodeTypeEnum.INTERMEDIATE)
                && (rightDiagramEdge.rightNode.nodeType == leftDiagramEdge.rightNode.nodeType
                        || rightDiagramEdge.rightNode.nodeType == NodeTypeEnum.INTERMEDIATE)
    }
}