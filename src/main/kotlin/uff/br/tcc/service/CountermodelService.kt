package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uff.br.tcc.dto.CountermodelResponse
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.term.AtomicTerm
import uff.br.tcc.enum.StepDescriptionEnum

@Service
class CountermodelService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createCountermodel(
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof
    ): CountermodelResponse {
        logger.info("Initializing countermodel.")
        val countermodel = initializeCountermodel(leftDiagrammaticProof, rightDiagrammaticProof)

        var leftRelations = countermodel.relations
        for (diagram in leftDiagrammaticProof.diagrams.reversed()) {
            leftRelations = applyOperation(diagram, leftRelations)
            diagram.countermodelRelations = leftRelations
        }

        var rightRelations = countermodel.relations
        for (diagram in rightDiagrammaticProof.diagrams.reversed()) {
            rightRelations = applyOperation(diagram, rightRelations)
            diagram.countermodelRelations = rightRelations
        }

        countermodel.isHomomorphic = isHomomorphic(
            leftRelations, leftDiagrammaticProof, rightRelations, rightDiagrammaticProof
        )

        return countermodel
    }

    private fun initializeCountermodel(
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof
    ): CountermodelResponse {
        val leftNormalFormDiagram = leftDiagrammaticProof.diagrams.last()

        val rightNormalFormDiagram = rightDiagrammaticProof.diagrams.last()

        val universeVariables = leftNormalFormDiagram.nodes.mapIndexed { index, node ->
            node.name to index
        }.toMap()

        val relations = leftNormalFormDiagram.edges.map { (it.label as AtomicTerm).name }.distinct()

        val initialRelations = relations.associateWith { relation ->
            leftNormalFormDiagram.edges.filter {
                it.label.name() == relation
            }.map {
                (universeVariables[it.leftNode.name]!! to universeVariables[it.rightNode.name]!!)
            }
        }.toMap()

        val rightRelations = rightNormalFormDiagram.edges.map { (it.label as AtomicTerm).name }.distinct().filter {
            !relations.contains(it)
        }

        val allInitialRelations = if (rightRelations.isEmpty()) {
            initialRelations
        } else {
            initialRelations + rightRelations.map {
                it to listOf(EMPTY_SET_VALUE to EMPTY_SET_VALUE)
            }
        }

        logger.info(
            "Universe of countermodel = ${universeVariables.values}, relations = $allInitialRelations, " +
                "for diagram with initial label = ${leftDiagrammaticProof.diagrams.first().edges.first().label.name()}"
        )

        return CountermodelResponse(
            universeVariables,
            allInitialRelations
        )
    }

    private fun isHomomorphic(
        leftRelations: Map<String, List<Pair<Int, Int>>>,
        leftDiagrammaticProof: DiagrammaticProof,
        rightRelations: Map<String, List<Pair<Int, Int>>>,
        rightDiagrammaticProof: DiagrammaticProof,
    ) = leftRelations[leftDiagrammaticProof.diagrams.first().edges.first().label.name()]!!
        .containsAll(
            rightRelations[rightDiagrammaticProof.diagrams.first().edges.first().label.name()]!!
        )

    private fun applyOperation(
        diagram: Diagram,
        relationsInModel: Map<String, List<Pair<Int, Int>>>
    ): Map<String, List<Pair<Int, Int>>> {
        return when (diagram.stepDescription) {
            StepDescriptionEnum.REMOVE_INVERSE.name -> {
                handleInverse(relationsInModel, diagram)
            }
            StepDescriptionEnum.REMOVE_INTERSECTION.name -> {
                handleIntersection(relationsInModel, diagram)
            }
            StepDescriptionEnum.REMOVE_COMPOSITION.name -> {
                handleComposition(relationsInModel, diagram)
            }
            else -> relationsInModel
        }
    }

    private fun handleComposition(
        relationsInModel: Map<String, List<Pair<Int, Int>>>,
        diagram: Diagram,
    ) = relationsInModel + mapOf(
        diagram.removedEdge!!.label.name() to applyComposition(
            relationsInModel.getOrEmptySet(diagram.createdEdges!!.first().label.name()),
            relationsInModel.getOrEmptySet(diagram.createdEdges!!.last().label.name()),
        )
    )

    private fun handleIntersection(
        relationsInModel: Map<String, List<Pair<Int, Int>>>,
        diagram: Diagram,
    ) = relationsInModel + mapOf(
        diagram.removedEdge!!.label.name() to applyIntersection(
            relationsInModel.getOrEmptySet(diagram.createdEdges!!.first().label.name()),
            relationsInModel.getOrEmptySet(diagram.createdEdges!!.last().label.name()),
        )
    )

    private fun handleInverse(
        relationsInModel: Map<String, List<Pair<Int, Int>>>,
        diagram: Diagram,
    ) = relationsInModel + mapOf(
        diagram.removedEdge!!.label.name() to
            applyInverse(relationsInModel.getOrEmptySet(diagram.createdEdges!!.first().label.name()))
    )

    fun applyInverse(relation: List<Pair<Int, Int>>) =
        relation.map {
            it.second to it.first
        }

    fun applyIntersection(leftRelation: List<Pair<Int, Int>>, rightRelation: List<Pair<Int, Int>>) =
        leftRelation.filter {
            rightRelation.contains(it)
        }.ifEmpty {
            listOf(EMPTY_SET_VALUE to EMPTY_SET_VALUE)
        }

    fun applyComposition(leftRelation: List<Pair<Int, Int>>, rightRelation: List<Pair<Int, Int>>) =
        leftRelation.map { leftPair ->
            rightRelation.filter {
                it.first == leftPair.second
            }.map {
                leftPair.first to it.second
            }
        }.flatten().ifEmpty {
            listOf(EMPTY_SET_VALUE to EMPTY_SET_VALUE)
        }

    fun Map<String, List<Pair<Int, Int>>>.getOrEmptySet(label: String): List<Pair<Int, Int>> {
        return if (this[label] == null) {
            listOf(EMPTY_SET_VALUE to EMPTY_SET_VALUE)
        } else {
            this[label]!!
        }
    }

    companion object {
        const val EMPTY_SET_VALUE = -1
    }
}
