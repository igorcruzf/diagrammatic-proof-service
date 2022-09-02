package uff.br.tcc.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.dto.Node
import uff.br.tcc.dto.term.AtomicTerm
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.utils.atomicDiagram
import uff.br.tcc.utils.normalIntersectionDiagram

class HypothesisServiceTest {

    private val hypothesisService: HypothesisService = HypothesisService()

    @Test
    fun `should validate that R in included in R`() {
        val leftDiagram = atomicDiagram("R")
        val rightDiagram = atomicDiagram("R")

        Assertions.assertTrue(
            hypothesisService.validate(
                HomomorphismValidatorRequest(leftDiagram, rightDiagram)
            )
        )
    }

    @Test
    fun `should validate that R intersection S in included in R`() {
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("R")

        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R intersection S in included in S`() {
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("S")

        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R is not included in R intersection S`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("R")

        Assertions.assertFalse(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that S is not included in R intersection S`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("S")

        Assertions.assertFalse(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R intersection S is included in S intersection R and vice versa`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = normalIntersectionDiagram("S", "R")

        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(rightDiagram, leftDiagram)))
    }

    @Test
    fun `should validate that R intersection S is included in R intersection R`() {
        val rightDiagram = normalIntersectionDiagram("R", "R")
        val leftDiagram = normalIntersectionDiagram("R", "S")

        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that (A comp B) int (A comp C) is not included in A comp (B int C)`() {

        fun buildLeftDiagram(): Diagram {
            val nodeA = Node(name = "a", type = NodeTypeEnum.INTERMEDIATE)
            val nodeB = Node(name = "b", type = NodeTypeEnum.INTERMEDIATE)
            val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
            val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)
            return Diagram(
                nodes = mutableListOf(nodeA, nodeB, inputNode, outputNode),
                edges = mutableListOf(
                    Edge(inputNode, nodeA, AtomicTerm("A")),
                    Edge(nodeA, outputNode, AtomicTerm("B")),
                    Edge(inputNode, nodeB, AtomicTerm("A")),
                    Edge(nodeB, outputNode, AtomicTerm("C"))
                ),
                stepDescription = ""
            )
        }

        fun buildRightDiagram(): Diagram {
            val nodeA = Node(name = "a", type = NodeTypeEnum.INTERMEDIATE)
            val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
            val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)
            return Diagram(
                nodes = mutableListOf(nodeA, inputNode, outputNode),
                edges = mutableListOf(
                    Edge(inputNode, nodeA, AtomicTerm("A")),
                    Edge(nodeA, outputNode, AtomicTerm("B")),
                    Edge(nodeA, outputNode, AtomicTerm("C"))
                ),
                stepDescription = ""
            )
        }

        val leftDiagram = buildLeftDiagram()
        val rightDiagram = buildRightDiagram()

        Assertions.assertFalse(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that S is included in R composition S composition T`() {
        val rightDiagram = atomicDiagram("S")

        val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
        val intermediateNode = Node("a", type = NodeTypeEnum.INTERMEDIATE)
        val intermediateNode2 = Node("b", type = NodeTypeEnum.INTERMEDIATE)
        val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)
        val leftDiagram = Diagram(
            nodes = mutableListOf(inputNode, outputNode, intermediateNode, intermediateNode2),
            edges = mutableListOf(
                Edge(
                    leftNode = inputNode,
                    label = AtomicTerm("R"),
                    rightNode = intermediateNode
                ),
                Edge(
                    leftNode = intermediateNode,
                    label = AtomicTerm("S"),
                    rightNode = intermediateNode2
                ),
                Edge(
                    leftNode = intermediateNode2,
                    label = AtomicTerm("T"),
                    rightNode = outputNode
                )
            ),
            stepDescription = ""
        )

        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that complex diagram H is included in diagram G but H is not included in G`() {
        val diagramG = buildDiagramG()
        val diagramH = buildDiagramH()
        Assertions.assertTrue(hypothesisService.validate(HomomorphismValidatorRequest(diagramG, diagramH)))
        Assertions.assertFalse(hypothesisService.validate(HomomorphismValidatorRequest(diagramH, diagramG)))
    }

    private fun buildDiagramG(): Diagram {
        val nodeA = Node(name = "a", type = NodeTypeEnum.INTERMEDIATE)
        val nodeB = Node(name = "b", type = NodeTypeEnum.INTERMEDIATE)
        val nodeC = Node(name = "c", type = NodeTypeEnum.INTERMEDIATE)
        val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
        val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

        return Diagram(
            nodes = mutableListOf(inputNode, outputNode, nodeA, nodeB, nodeC),
            edges = mutableListOf(
                Edge(inputNode, nodeA, AtomicTerm("A")),
                Edge(inputNode, nodeA, AtomicTerm("B")),
                Edge(nodeB, nodeA, AtomicTerm("B")),
                Edge(nodeB, outputNode, AtomicTerm("C")),
                Edge(nodeB, outputNode, AtomicTerm("F")),
                Edge(nodeB, nodeC, AtomicTerm("D")),
                Edge(nodeC, outputNode, AtomicTerm("E")),
            ),
            stepDescription = ""
        )
    }

    private fun buildDiagramH(): Diagram {
        val nodeX = Node(name = "x", type = NodeTypeEnum.INTERMEDIATE)
        val nodeY = Node(name = "y", type = NodeTypeEnum.INTERMEDIATE)
        val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
        val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

        return Diagram(
            nodes = mutableListOf(inputNode, outputNode, nodeX, nodeY),
            edges = mutableListOf(
                Edge(inputNode, nodeX, AtomicTerm("A")),
                Edge(nodeY, nodeX, AtomicTerm("B")),
                Edge(nodeY, outputNode, AtomicTerm("C")),
                Edge(nodeY, outputNode, AtomicTerm("F")),
            ),
            stepDescription = ""
        )
    }
}
