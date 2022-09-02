package uff.br.tcc.service

import org.slf4j.LoggerFactory
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.extensions.getEdgesWithSpecificNode

abstract class RelaxedHomomorphismValidator {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun validate(homomorphismValidatorRequest: HomomorphismValidatorRequest): Boolean

    protected fun isNodeImageToRightDiagramNode(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        leftDiagramNodeName: String,
        rightDiagramNodeName: String,
        edgesPath: List<Edge> = emptyList(),
    ) = isAllEdgesInSameNodeValid(
        homomorphismValidatorRequest, rightDiagramNodeName, leftDiagramNodeName,
        position = "LEFT", edgesPath
    ) &&
        isAllEdgesInSameNodeValid(
            homomorphismValidatorRequest, rightDiagramNodeName, leftDiagramNodeName,
            position = "RIGHT", edgesPath
        )

    private fun isAllEdgesInSameNodeValid(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        rightDiagramNodeName: String,
        leftDiagramNodeName: String,
        position: String,
        edgesPath: List<Edge>,
    ): Boolean {
        val rightDiagramEdges = homomorphismValidatorRequest.rightDiagram.getEdgesWithSpecificNode(
            nodeName = rightDiagramNodeName,
            position = position
        )
        logger.debug("Right diagram edges in position $position with node $rightDiagramNodeName = $rightDiagramEdges.")

        val leftDiagramEdges = homomorphismValidatorRequest.leftDiagram.getEdgesWithSpecificNode(
            nodeName = leftDiagramNodeName,
            position = position
        )
        logger.debug("Left diagram edges in position $position with node $leftDiagramNodeName = $leftDiagramEdges.")

        val validEdges = rightDiagramEdges.filter { edge ->
            edgesPath.contains(edge) ||
                canMapEdgeToLeftDiagram(
                    homomorphismValidatorRequest = homomorphismValidatorRequest,
                    leftDiagramEdges = leftDiagramEdges,
                    rightDiagramEdge = edge,
                    edgesPath = edgesPath
                )
        }
        logger.debug("Total of valid edges in a total of ${rightDiagramEdges.count()} is ${validEdges.count()}.")

        return (validEdges == rightDiagramEdges).also {
            if (!it) {
                logger.debug("Total edges that was mapped with success = $validEdges")
                logger.debug("Total edges in right diagram = $rightDiagramEdges")
            }
        }
    }

    private fun canMapEdgeToLeftDiagram(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>
    ): Boolean {
        val possibleEdgeImages = getAllPossibleEdgeImages(
            homomorphismValidatorRequest, leftDiagramEdges,
            rightDiagramEdge, edgesPath
        )
        return possibleEdgeImages.isNotEmpty()
            .also { isNotEmpty ->
                logger.debug(
                    "Total of possible images for nodes in edge $rightDiagramEdge " +
                        "is ${possibleEdgeImages.count()}"
                )
                if (isNotEmpty) {
                    addImageInNodes(homomorphismValidatorRequest, rightDiagramEdge, possibleEdgeImages.first())
                }
            }
    }

    private fun addImageInNodes(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        rightDiagramEdge: Edge,
        edgeImage: Edge
    ) {
        logger.debug("Adding $rightDiagramEdge nodes image to nodes in $edgeImage.")
        homomorphismValidatorRequest.rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.leftNode.name
            }.imageName = edgeImage.leftNode.name
        homomorphismValidatorRequest.rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.rightNode.name
            }.imageName = edgeImage.rightNode.name
    }

    protected open fun getAllPossibleEdgeImages(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): List<Edge> {
        val newEdgesPath = edgesPath.plus(rightDiagramEdge)
        logger.debug("Getting all possible images to edge $rightDiagramEdge with edges path $newEdgesPath.")
        return leftDiagramEdges.filter { edge ->
            logger.debug("Analysing if nodes in $edge is an option of image to nodes in $rightDiagramEdge.")
            edge.label.name() == rightDiagramEdge.label.name() &&
                isNodeImageToRightDiagramNode(
                    homomorphismValidatorRequest, edge.rightNode.name,
                    rightDiagramEdge.rightNode.name, newEdgesPath
                ) &&
                isNodeImageToRightDiagramNode(
                    homomorphismValidatorRequest, edge.leftNode.name,
                    rightDiagramEdge.leftNode.name, newEdgesPath
                )
        }
    }
}
