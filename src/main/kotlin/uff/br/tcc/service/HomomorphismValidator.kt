package uff.br.tcc.service

import org.springframework.stereotype.Component
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.getEdgesWithSpecificNode

@Component
class HomomorphismValidator {
    private lateinit var leftDiagram: Diagram
    private lateinit var rightDiagram: Diagram

    fun validate(leftDiagram: Diagram, rightDiagram: Diagram): Boolean {
        this.leftDiagram = leftDiagram
        this.rightDiagram = rightDiagram
        return isNodeImageToRightDiagramNode(leftDiagram.nodes.first().name, rightDiagram.nodes.first().name)
    }

    private fun isNodeImageToRightDiagramNode(
        leftDiagramNodeName: String,
        rightDiagramNodeName: String,
        edgesPath: List<Edge> = emptyList(),
    ) = isAllEdgesInSameNodeValid(rightDiagramNodeName, leftDiagramNodeName, position = "LEFT", edgesPath) &&
        isAllEdgesInSameNodeValid(rightDiagramNodeName, leftDiagramNodeName, position = "RIGHT", edgesPath)

    private fun isAllEdgesInSameNodeValid(
        rightDiagramNodeName: String,
        leftDiagramNodeName: String,
        position: String,
        edgesPath: List<Edge>,
    ): Boolean {
        val rightDiagramEdges = rightDiagram.getEdgesWithSpecificNode(nodeName = rightDiagramNodeName, position = position)
        val leftDiagramEdges = leftDiagram.getEdgesWithSpecificNode(nodeName = leftDiagramNodeName, position = position)

        val validEdges = rightDiagramEdges.filter { edge ->
            edge.isMappedInLeftDiagram ||
                edgesPath.contains(edge) ||
                canMapEdgeToLeftDiagram(
                    leftDiagramEdges = leftDiagramEdges,
                    rightDiagramEdge = edge,
                    edgesPath = edgesPath
                )
        }
        return validEdges == rightDiagramEdges
    }

    private fun canMapEdgeToLeftDiagram(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): Boolean {
        val possibleEdgeImages = getAllPossibleEdgeImages(leftDiagramEdges, rightDiagramEdge, edgesPath)
        return possibleEdgeImages.isNotEmpty()
            .also { isNotEmpty ->
                if (isNotEmpty) {
                    rightDiagramEdge.isMappedInLeftDiagram = true
                    rightDiagramEdge.leftNode.imageName = possibleEdgeImages.first().leftNode.name
                    rightDiagramEdge.rightNode.imageName = possibleEdgeImages.first().rightNode.name
                }
            }
    }

    private fun getAllPossibleEdgeImages(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): List<Edge> {
        val newEdgesPath = edgesPath.plus(rightDiagramEdge)
        return leftDiagramEdges.filter { edge ->
            edge.term.name() == rightDiagramEdge.term.name() &&
                isNodesTypeValid(rightDiagramEdge, edge) &&
                isNodeImageToRightDiagramNode(edge.rightNode.name, rightDiagramEdge.rightNode.name, newEdgesPath) &&
                isNodeImageToRightDiagramNode(edge.leftNode.name, rightDiagramEdge.leftNode.name, newEdgesPath)
        }
    }

    private fun isNodesTypeValid(
        rightDiagramEdge: Edge,
        leftDiagramEdge: Edge
    ) = isNodeTypeValid(rightDiagramEdge.leftNode.type, leftDiagramEdge.leftNode.type) &&
        isNodeTypeValid(rightDiagramEdge.rightNode.type, leftDiagramEdge.rightNode.type)

    private fun isNodeTypeValid(
        rightDiagramEdgeNodeType: NodeTypeEnum,
        leftDiagramEdgeNodeType: NodeTypeEnum
    ) = rightDiagramEdgeNodeType == NodeTypeEnum.INTERMEDIATE ||
        rightDiagramEdgeNodeType == leftDiagramEdgeNodeType
}
