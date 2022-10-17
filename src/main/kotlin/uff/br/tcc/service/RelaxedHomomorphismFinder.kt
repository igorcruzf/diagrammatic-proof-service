package uff.br.tcc.service

import org.slf4j.LoggerFactory
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismRequest
import uff.br.tcc.dto.Node

abstract class RelaxedHomomorphismFinder {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun find(homomorphismRequest: HomomorphismRequest): Boolean

    protected fun find(
        leftDiagramNode: Node,
        rightDiagramNode: Node,
        edgesPath: List<Edge>,
        homomorphismRequest: HomomorphismRequest
    ): Boolean {
        val isTailValid = isTailValid(homomorphismRequest, rightDiagramNode, edgesPath, leftDiagramNode)

        val isHeadValid = isHeadValid(homomorphismRequest, rightDiagramNode, edgesPath, leftDiagramNode)

        return (isTailValid && isHeadValid).also { isValid ->
            logger.info("Verified if ${leftDiagramNode.name} is image of ${rightDiagramNode.name}, result = $isValid")
            if (isValid) {
                homomorphismRequest.rightDiagram.nodes.first {
                    it.name == rightDiagramNode.name
                }.imageName = leftDiagramNode.name
            }
        }
    }

    private fun isTailValid(
        homomorphismRequest: HomomorphismRequest,
        rightDiagramNode: Node,
        edgesPath: List<Edge>,
        leftDiagramNode: Node,
    ) = homomorphismRequest.rightDiagram.edges.filter {
        it.leftNode == rightDiagramNode &&
            !edgesPath.contains(it)
    }.map { rightEdge ->
        getPossibleImagesFromTail(homomorphismRequest, leftDiagramNode, rightEdge)
            .any {
                find(
                    leftDiagramNode = it.rightNode,
                    rightDiagramNode = rightEdge.rightNode,
                    edgesPath = edgesPath + rightEdge,
                    homomorphismRequest = homomorphismRequest
                )
            }
    }.reduceOrNull { acc, isEdgeValid ->
        acc && isEdgeValid
    } ?: true

    private fun isHeadValid(
        homomorphismRequest: HomomorphismRequest,
        rightDiagramNode: Node,
        edgesPath: List<Edge>,
        leftDiagramNode: Node,
    ) = homomorphismRequest.rightDiagram.edges.filter {
        it.rightNode == rightDiagramNode &&
            !edgesPath.contains(it)
    }.map { rightEdge ->
        getPossibleImagesFromHead(homomorphismRequest, leftDiagramNode, rightEdge).any {
            find(
                leftDiagramNode = it.leftNode,
                rightDiagramNode = rightEdge.leftNode,
                edgesPath = edgesPath + rightEdge,
                homomorphismRequest = homomorphismRequest
            )
        }
    }.reduceOrNull { acc, isEdgeValid ->
        acc && isEdgeValid
    } ?: true

    protected open fun getPossibleImagesFromTail(
        homomorphismRequest: HomomorphismRequest,
        leftDiagramNode: Node,
        rightEdge: Edge,
    ) = homomorphismRequest.leftDiagram.edges.filter {
        it.leftNode == leftDiagramNode &&
            it.label == rightEdge.label
    }

    protected open fun getPossibleImagesFromHead(
        homomorphismRequest: HomomorphismRequest,
        leftDiagramNode: Node,
        rightEdge: Edge,
    ) = homomorphismRequest.leftDiagram.edges.filter {
        it.rightNode == leftDiagramNode &&
            it.label == rightEdge.label
    }
}
