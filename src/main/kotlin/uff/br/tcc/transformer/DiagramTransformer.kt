package uff.br.tcc.transformer

import uff.br.tcc.enum.Operation
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Term
import uff.br.tcc.model.getFirstEdgeWithNonAtomicTerm

class DiagramTransformer {

    fun transformDiagram(diagram: Diagram) {

        val edge = diagram.edges.getFirstEdgeWithNonAtomicTerm()
        diagram.edges.remove(edge)
        val term = edge.term as Term

        when (term.operation) {
            Operation.COMPOSITION -> transformComposition(diagram = diagram, edge = edge)
            Operation.INTERSECTION -> transformIntersection(diagram = diagram, edge = edge)
            Operation.INVERSE -> transformInverse(diagram = diagram, edge = edge)
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