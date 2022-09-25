package uff.br.tcc.service

import org.slf4j.LoggerFactory
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.enum.Direction
import uff.br.tcc.extensions.getEdgesWithSpecificNode

abstract class RelaxedHomomorphismFinder {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun find(homomorphismValidatorRequest: HomomorphismValidatorRequest): Boolean

    protected fun isNodeImageToRightDiagramNode(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        leftDiagramNodeName: String,
        rightDiagramNodeName: String,
        edgesPath: List<Edge> = emptyList(),
    ) = isAllEdgesInSameNodeValid(
        homomorphismValidatorRequest = homomorphismValidatorRequest,
        rightDiagramNodeName = rightDiagramNodeName,
        leftDiagramNodeName = leftDiagramNodeName,
        position = Direction.LEFT,
        edgesPath = edgesPath
    ) && isAllEdgesInSameNodeValid(
        homomorphismValidatorRequest = homomorphismValidatorRequest,
        rightDiagramNodeName = rightDiagramNodeName,
        leftDiagramNodeName = leftDiagramNodeName,
        position = Direction.RIGHT,
        edgesPath = edgesPath
    )

    private fun isAllEdgesInSameNodeValid(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        rightDiagramNodeName: String,
        leftDiagramNodeName: String,
        position: Direction,
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
        val possibleEdgeImage = getPossibleEdgeImage(
            homomorphismValidatorRequest, leftDiagramEdges,
            rightDiagramEdge, edgesPath
        )
        return (possibleEdgeImage != null)
            .also { isNotEmpty ->
                if (isNotEmpty) {
                    logger.debug(
                        "Possible image for nodes in edge $rightDiagramEdge " +
                            "is ${possibleEdgeImage?.label?.name()}"
                    )
                    addImageInNodes(homomorphismValidatorRequest, rightDiagramEdge, possibleEdgeImage!!)
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

    protected open fun getPossibleEdgeImage(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
        leftDiagramEdges: List<Edge>,
        rightDiagramEdge: Edge,
        edgesPath: List<Edge>,
    ): Edge? {
        val newEdgesPath = edgesPath.plus(rightDiagramEdge)
        logger.debug("Getting all possible images to edge $rightDiagramEdge with edges path $newEdgesPath.")
        return leftDiagramEdges.firstOrNull { edge ->
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
