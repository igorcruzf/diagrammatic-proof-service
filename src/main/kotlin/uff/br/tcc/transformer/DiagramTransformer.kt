package uff.br.tcc.transformer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.term.NonAtomicTerm
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.extensions.getFirstEdgeWithNonAtomicTerm
import uff.br.tcc.extensions.transformComposition
import uff.br.tcc.extensions.transformIntersection
import uff.br.tcc.extensions.transformInverse

@Component
class DiagramTransformer {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun transformDiagram(diagram: Diagram) {
        logger.info("Transforming diagram $diagram.")
        val edge = diagram.edges.getFirstEdgeWithNonAtomicTerm()
        logger.info("First non atomic edge is $edge.")
        diagram.edges.remove(edge)
        val nonAtomicTerm = edge.label as NonAtomicTerm

        when (nonAtomicTerm.operation) {
            OperationEnum.COMPOSITION -> transformComposition(diagram = diagram, edge = edge)
            OperationEnum.INTERSECTION -> transformIntersection(diagram = diagram, edge = edge)
            OperationEnum.INVERSE -> transformInverse(diagram = diagram, edge = edge)
        }
        logger.info("Diagram transformed to $diagram.")
    }

    fun transformComposition(diagram: Diagram, edge: Edge) {
        logger.info("Transforming composition in edge $edge.")
        diagram.removedEdge = edge
        val (firstEdge, secondEdge, node) = edge.transformComposition()
        diagram.createdEdges = listOf(firstEdge, secondEdge)
        diagram.createdNode = node
        diagram.nodes.add(node)
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_COMPOSITION.name
    }

    fun transformIntersection(diagram: Diagram, edge: Edge) {
        logger.info("Transforming intersection in edge $edge.")
        diagram.removedEdge = edge
        val (firstEdge, secondEdge) = edge.transformIntersection()
        diagram.createdEdges = listOf(firstEdge, secondEdge)
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INTERSECTION.name
    }

    fun transformInverse(diagram: Diagram, edge: Edge) {
        logger.info("Transforming inverse in edge $edge.")
        diagram.removedEdge = edge
        val newEdge = edge.transformInverse()
        diagram.createdEdges = listOf(newEdge)
        diagram.edges.add(newEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INVERSE.name
    }
}
