package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.enum.NodeTypeEnum

@Component
class HomomorphismValidator : RelaxedHomomorphismValidator() {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun validate(homomorphismValidatorRequest: HomomorphismValidatorRequest): Boolean {
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

    override fun getAllPossibleEdgeImages(
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
    ) = rightDiagramEdgeNodeType == NodeTypeEnum.INTERMEDIATE || rightDiagramEdgeNodeType == leftDiagramEdgeNodeType
}
