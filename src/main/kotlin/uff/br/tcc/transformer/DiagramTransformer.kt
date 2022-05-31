package uff.br.tcc.transformer

import org.springframework.stereotype.Component
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Term
import uff.br.tcc.model.getFirstEdgeWithNonAtomicTerm

@Component
class DiagramTransformer {

    fun transformDiagram(diagram: Diagram) {

        val edge = diagram.edges.getFirstEdgeWithNonAtomicTerm()
        diagram.edges.remove(edge)
        val term = edge.term as Term

        when (term.operation) {
            OperationEnum.COMPOSITION -> transformComposition(diagram = diagram, edge = edge)
            OperationEnum.INTERSECTION -> transformIntersection(diagram = diagram, edge = edge)
            OperationEnum.INVERSE -> transformInverse(diagram = diagram, edge = edge)
        }
    }

    private fun transformComposition(diagram: Diagram, edge: Edge) {
        val (firstEdge, secondEdge, node) = edge.transformComposition()
        diagram.nodes.add(node)
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_COMPOSITION.name
    }

    private fun transformIntersection(diagram: Diagram, edge: Edge) {
        val (firstEdge, secondEdge) = edge.transformIntersection()
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INTERSECTION.name
    }

    private fun transformInverse(diagram: Diagram, edge: Edge) {
        val newEdge = edge.transformInverse()
        diagram.edges.add(newEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INVERSE.name
    }
}
