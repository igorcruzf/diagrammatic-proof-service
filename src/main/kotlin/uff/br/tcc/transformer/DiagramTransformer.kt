package uff.br.tcc.transformer

import org.springframework.stereotype.Component
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.extensions.getFirstEdgeWithNonAtomicTerm
import uff.br.tcc.extensions.transformComposition
import uff.br.tcc.extensions.transformIntersection
import uff.br.tcc.extensions.transformInverse
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.term.NonAtomicTerm

@Component
class DiagramTransformer {

    fun transformDiagram(diagram: Diagram) {
        val edge = diagram.edges.getFirstEdgeWithNonAtomicTerm()
        diagram.edges.remove(edge)
        val nonAtomicTerm = edge.label as NonAtomicTerm

        when (nonAtomicTerm.operation) {
            OperationEnum.COMPOSITION -> transformComposition(diagram = diagram, edge = edge)
            OperationEnum.INTERSECTION -> transformIntersection(diagram = diagram, edge = edge)
            OperationEnum.INVERSE -> transformInverse(diagram = diagram, edge = edge)
        }
    }

    fun transformComposition(diagram: Diagram, edge: Edge) {
        val (firstEdge, secondEdge, node) = edge.transformComposition()
        diagram.nodes.add(node)
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_COMPOSITION.name
    }

    fun transformIntersection(diagram: Diagram, edge: Edge) {
        val (firstEdge, secondEdge) = edge.transformIntersection()
        diagram.edges.add(firstEdge)
        diagram.edges.add(secondEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INTERSECTION.name
    }

    fun transformInverse(diagram: Diagram, edge: Edge) {
        val newEdge = edge.transformInverse()
        diagram.edges.add(newEdge)
        diagram.stepDescription = StepDescriptionEnum.REMOVE_INVERSE.name
    }
}
