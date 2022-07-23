package uff.br.tcc.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.dto.Node
import uff.br.tcc.dto.term.AtomicTerm
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.utils.atomicDiagram
import uff.br.tcc.utils.normalIntersectionDiagram

class HomomorphismValidatorTest {

    private val homomorphismValidator: HomomorphismValidator = HomomorphismValidator()

    @Test
    fun `should validate that R in included in R`() {
        val leftDiagram = atomicDiagram("R")
        val rightDiagram = atomicDiagram("R")

        assertTrue(
            homomorphismValidator.validate(
                HomomorphismValidatorRequest(leftDiagram, rightDiagram)
            )
        )
    }

    @Test
    fun `should validate that R intersection S in included in R`() {
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("R")

        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R intersection S in included in S`() {
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("S")

        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R is not included in R intersection S`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("R")

        assertFalse(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that S is not included in R intersection S`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("S")

        assertFalse(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that R intersection S is included in S intersection R and vice versa`() {
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = normalIntersectionDiagram("S", "R")

        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(rightDiagram, leftDiagram)))
    }

    @Test
    fun `should validate that R intersection S is included in R intersection R`() {
        val rightDiagram = normalIntersectionDiagram("R", "R")
        val leftDiagram = normalIntersectionDiagram("R", "S")

        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
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

        assertFalse(homomorphismValidator.validate(HomomorphismValidatorRequest(leftDiagram, rightDiagram)))
    }

    @Test
    fun `should validate that complex diagram H is included in diagram G but H is not included in G`() {
        val diagramG = buildDiagramG()
        val diagramH = buildDiagramH()
        assertTrue(homomorphismValidator.validate(HomomorphismValidatorRequest(diagramG, diagramH)))
        assertFalse(homomorphismValidator.validate(HomomorphismValidatorRequest(diagramH, diagramG)))
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
