package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.extensions.getEdgesWithSpecificNode

@Component
class HomomorphismValidator {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun validate(homomorphismValidatorRequest: HomomorphismValidatorRequest): Boolean {
        logger.info(
            "Validating leftDiagram ${homomorphismValidatorRequest.leftDiagram} and " +
                "rightDiagram ${homomorphismValidatorRequest.rightDiagram}."
        )
        return isNodeImageToRightDiagramNode(
            homomorphismValidatorRequest,
            homomorphismValidatorRequest.leftDiagram.nodes.first().name,
            homomorphismValidatorRequest.rightDiagram.nodes.first().name
        )
    }

    private fun isNodeImageToRightDiagramNode(
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
        logger.info("Right diagram edges in position $position with node $rightDiagramNodeName = $rightDiagramEdges.")

        val leftDiagramEdges = homomorphismValidatorRequest.leftDiagram.getEdgesWithSpecificNode(
            nodeName = leftDiagramNodeName,
            position = position
        )
        logger.info("Left diagram edges in position $position with node $leftDiagramNodeName = $leftDiagramEdges.")

        val validEdges = rightDiagramEdges.filter { edge ->
            edgesPath.contains(edge) ||
                canMapEdgeToLeftDiagram(
                    homomorphismValidatorRequest = homomorphismValidatorRequest,
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
                logger.info(
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
        logger.info("Adding $rightDiagramEdge nodes image to nodes in $edgeImage.")
        homomorphismValidatorRequest.rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.leftNode.name
            }.imageName = edgeImage.leftNode.name
        homomorphismValidatorRequest.rightDiagram.nodes
            .first {
                it.name == rightDiagramEdge.rightNode.name
            }.imageName = edgeImage.rightNode.name
    }

    private fun getAllPossibleEdgeImages(
        homomorphismValidatorRequest: HomomorphismValidatorRequest,
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
