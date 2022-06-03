package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.extensions.getEdgesWithSpecificNode
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge

@Component
class HomomorphismValidator {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private lateinit var leftDiagram: Diagram
    private lateinit var rightDiagram: Diagram

    fun validate(leftDiagram: Diagram, rightDiagram: Diagram): Boolean {
        logger.info("Validating leftDiagram $leftDiagram and rightDiagram $rightDiagram.")
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
        val rightDiagramEdges = rightDiagram.getEdgesWithSpecificNode(
            nodeName = rightDiagramNodeName,
            position = position
        )
        logger.info("Right diagram edges in position $position with node $rightDiagramNodeName = $rightDiagramEdges.")

        val leftDiagramEdges = leftDiagram.getEdgesWithSpecificNode(
            nodeName = leftDiagramNodeName,
            position = position
        )
        logger.info("Left diagram edges in position $position with node $leftDiagramNodeName = $leftDiagramEdges.")

        val validEdges = rightDiagramEdges.filter { edge ->
            edge.isMappedInLeftDiagram ||
                edgesPath.contains(edge) ||
                canMapEdgeToLeftDiagram(
                    leftDiagramEdges = leftDiagramEdges,
                    rightDiagramEdge = edge,
                    edgesPath = edgesPath
                )
        }
        logger.info("Total of valid edges in a total of ${rightDiagramEdges.count()} is ${validEdges.count()}.")

        return (validEdges == rightDiagramEdges).also {
            if (!it) {
                logger.info("Total edges that was mapped with success = $validEdges")
                logger.info("Total edges in right diagram = $rightDiagramEdges")
            }
        }
    }

    private fun canMapEdgeToLeftDiagram(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): Boolean {
        val possibleEdgeImages = getAllPossibleEdgeImages(leftDiagramEdges, rightDiagramEdge, edgesPath)
        return possibleEdgeImages.isNotEmpty()
            .also { isNotEmpty ->
                logger.info(
                    "Total of possible images for nodes in edge $rightDiagramEdge " +
                        "is ${possibleEdgeImages.count()}"
                )
                if (isNotEmpty) {
                    addImageInNodes(rightDiagramEdge, possibleEdgeImages.first())
                }
            }
    }

    private fun addImageInNodes(rightDiagramEdge: Edge, edgeImage: Edge) {
        logger.info("Adding $rightDiagramEdge nodes image to nodes in $edgeImage.")
        rightDiagramEdge.isMappedInLeftDiagram = true
        rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.leftNode.name
            }.imageName = edgeImage.leftNode.name
        rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.rightNode.name
            }.imageName = edgeImage.rightNode.name
    }

    private fun getAllPossibleEdgeImages(
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): List<Edge> {
        val newEdgesPath = edgesPath.plus(rightDiagramEdge)
        logger.info("Getting all possible images to edge $rightDiagramEdge with edges path $newEdgesPath.")
        return leftDiagramEdges.filter { edge ->
            logger.info("Analysing if nodes in $edge is an option of image to nodes in $rightDiagramEdge.")
            edge.label.name() == rightDiagramEdge.label.name() &&
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
