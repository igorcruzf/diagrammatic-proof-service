package uff.br.tcc.transformer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.model.term.AtomicTerm
import uff.br.tcc.model.term.ITerm
import uff.br.tcc.model.term.NonAtomicTerm

class DiagramTransformerTest {

    private val diagramTransformer = DiagramTransformer()
    private val inputNode = Node("input", NodeTypeEnum.INPUT)
    private val outputNode = Node("output", NodeTypeEnum.OUTPUT)

    @Test
    fun `should transform inverse`() {
        val term = buildInverseTerm()
        val edge = buildEdge(term)
        val diagram = buildDiagramWithoutEdge(inputNode, outputNode)

        diagramTransformer.transformInverse(diagram, edge)
        assertInverse(diagram, diagram.edges.first())
    }

    @Test
    fun `should transform composition`() {
        val term = buildCompositionTerm()
        val edge = buildEdge(term)
        val diagram = buildDiagramWithoutEdge(inputNode, outputNode)

        diagramTransformer.transformComposition(diagram, edge)
        assertComposition(diagram, diagram.edges.first(), diagram.edges.last())
    }

    @Test
    fun `should transform intersection`() {
        val term = buildIntersectionTerm()
        val edge = buildEdge(term)
        val diagram = buildDiagramWithoutEdge(inputNode, outputNode)

        diagramTransformer.transformIntersection(diagram, edge)
        assertIntersection(diagram, diagram.edges.first(), diagram.edges.last())
    }

    @Test
    fun `should transform diagram by removing inverse`() {
        val compositionEdge = buildEdge(buildCompositionTerm())
        val inverseEdge = buildEdge(buildInverseTerm())
        val intersectionEdge = buildEdge(buildIntersectionTerm())
        val atomicEdge = buildEdge(AtomicTerm("C"))
        val diagram = buildDiagram(
            inputNode, outputNode,
            mutableListOf(
                atomicEdge, inverseEdge, compositionEdge, intersectionEdge
            )
        )
        diagramTransformer.transformDiagram(diagram)

        val diagramEdge = diagram.edges.last()
        assertInverse(diagram, diagramEdge)
    }

    @Test
    fun `should transform diagram by removing composition`() {
        val compositionEdge = buildEdge(buildCompositionTerm())
        val inverseEdge = buildEdge(buildInverseTerm())
        val intersectionEdge = buildEdge(buildIntersectionTerm())
        val atomicEdge = buildEdge(AtomicTerm("C"))
        val diagram = buildDiagram(
            inputNode, outputNode,
            mutableListOf(
                atomicEdge, compositionEdge, inverseEdge, intersectionEdge
            )
        )
        diagramTransformer.transformDiagram(diagram)
        val firstDiagramEdge = diagram.edges[diagram.edges.count() - 2]
        val secondDiagramEdge = diagram.edges.last()
        assertComposition(diagram, firstDiagramEdge, secondDiagramEdge)
    }

    @Test
    fun `should transform diagram by removing intersection`() {
        val compositionEdge = buildEdge(buildCompositionTerm())
        val inverseEdge = buildEdge(buildInverseTerm())
        val intersectionEdge = buildEdge(buildIntersectionTerm())
        val atomicEdge = buildEdge(AtomicTerm("C"))
        val diagram = buildDiagram(
            inputNode, outputNode,
            mutableListOf(
                atomicEdge, intersectionEdge, compositionEdge, inverseEdge
            )
        )

        diagramTransformer.transformDiagram(diagram)
        val firstDiagramEdge = diagram.edges[diagram.edges.count() - 2]
        val secondDiagramEdge = diagram.edges.last()
        assertIntersection(diagram, firstDiagramEdge, secondDiagramEdge)
    }

    @Test
    fun `should throw NoSuchElementException for not having edge to transform`() {
        val atomicEdge = buildEdge(AtomicTerm("C"))
        val diagram = buildDiagram(
            inputNode, outputNode,
            mutableListOf(
                atomicEdge
            )
        )
        assertThrows<NoSuchElementException> {
            diagramTransformer.transformDiagram(diagram)
        }
    }

    private fun assertIntersection(diagram: Diagram, firstDiagramEdge: Edge, secondDiagramEdge: Edge) {
        assertEquals(StepDescriptionEnum.REMOVE_INTERSECTION.name, diagram.stepDescription)
        assertEquals(2, diagram.nodes.count())

        assertEquals(AtomicTerm("A"), firstDiagramEdge.term)
        assertEquals(inputNode, firstDiagramEdge.leftNode)
        assertEquals(outputNode, firstDiagramEdge.rightNode)

        assertEquals(AtomicTerm("B"), secondDiagramEdge.term)
        assertEquals(inputNode, secondDiagramEdge.leftNode)
        assertEquals(outputNode, secondDiagramEdge.rightNode)
    }

    private fun assertComposition(diagram: Diagram, firstDiagramEdge: Edge, secondDiagramEdge: Edge) {
        assertEquals(StepDescriptionEnum.REMOVE_COMPOSITION.name, diagram.stepDescription)
        assertEquals(firstDiagramEdge.rightNode, secondDiagramEdge.leftNode)
        assertEquals(3, diagram.nodes.count())

        assertEquals(AtomicTerm("A"), firstDiagramEdge.term)
        assertEquals(inputNode, firstDiagramEdge.leftNode)
        assertEquals(NodeTypeEnum.INTERMEDIATE, firstDiagramEdge.rightNode.type)

        assertEquals(AtomicTerm("B"), secondDiagramEdge.term)
        assertEquals(outputNode, secondDiagramEdge.rightNode)
        assertEquals(NodeTypeEnum.INTERMEDIATE, secondDiagramEdge.leftNode.type)
    }

    private fun assertInverse(diagram: Diagram, diagramEdge: Edge) {
        assertEquals(StepDescriptionEnum.REMOVE_INVERSE.name, diagram.stepDescription)
        assertEquals(AtomicTerm("A"), diagramEdge.term)
        assertEquals(outputNode, diagramEdge.leftNode)
        assertEquals(inputNode, diagramEdge.rightNode)
    }

    private fun buildCompositionTerm() = NonAtomicTerm(
        leftTerm = AtomicTerm("A"),
        operation = OperationEnum.COMPOSITION,
        rightTerm = AtomicTerm("B")
    )

    private fun buildInverseTerm() = NonAtomicTerm(
        leftTerm = AtomicTerm("A"),
        operation = OperationEnum.INVERSE
    )

    private fun buildIntersectionTerm() = NonAtomicTerm(
        leftTerm = AtomicTerm("A"),
        operation = OperationEnum.INTERSECTION,
        rightTerm = AtomicTerm("B")
    )

    private fun buildEdge(term: ITerm) = Edge(
        leftNode = inputNode,
        rightNode = outputNode,
        term = term
    )

    private fun buildDiagramWithoutEdge(inputNode: Node, outputNode: Node) =
        Diagram(
            nodes = mutableListOf(inputNode, outputNode),
            edges = mutableListOf(),
            stepDescription = StepDescriptionEnum.BEGIN.name
        )

    private fun buildDiagram(inputNode: Node, outputNode: Node, edges: MutableList<Edge>) =
        Diagram(
            nodes = mutableListOf(inputNode, outputNode),
            edges = edges,
            stepDescription = StepDescriptionEnum.BEGIN.name
        )
}
