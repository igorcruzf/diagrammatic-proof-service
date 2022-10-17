package uff.br.tcc.service

import org.springframework.stereotype.Component
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismRequest
import uff.br.tcc.dto.Node
import uff.br.tcc.enum.NodeTypeEnum

@Component
class HomomorphismFinder : RelaxedHomomorphismFinder() {

    override fun find(homomorphismRequest: HomomorphismRequest) =
        find(
            homomorphismRequest = homomorphismRequest,
            leftDiagramNode = homomorphismRequest.leftDiagram.nodes.first(),
            rightDiagramNode = homomorphismRequest.rightDiagram.nodes.first(),
            edgesPath = listOf()
        )

    override fun getPossibleImagesFromTail(
        homomorphismRequest: HomomorphismRequest,
        leftDiagramNode: Node,
        rightEdge: Edge,
    ) = homomorphismRequest.leftDiagram.edges.filter {
        it.leftNode == leftDiagramNode &&
            it.label == rightEdge.label &&
            isNodesTypeValid(rightEdge, it)
    }

    override fun getPossibleImagesFromHead(
        homomorphismRequest: HomomorphismRequest,
        leftDiagramNode: Node,
        rightEdge: Edge,
    ) = homomorphismRequest.leftDiagram.edges.filter {
        it.rightNode == leftDiagramNode &&
            it.label == rightEdge.label &&
            isNodesTypeValid(rightEdge, it)
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
