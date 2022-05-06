package uff.br.tcc.service

import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.model.AtomicTerm
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.utils.atomicDiagram
import uff.br.tcc.utils.normalIntersectionDiagram
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HomomorphismValidatorTest {

    private val homomorphismValidator: HomomorphismValidator = HomomorphismValidator()

    @Test
    fun `should validate that R intersection S in included in R` (){
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("R")

        assertTrue(homomorphismValidator.validate(leftDiagram, rightDiagram))
    }

    @Test
    fun `should validate that R intersection S in included in S` (){
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("S")

        assertTrue(homomorphismValidator.validate(leftDiagram, rightDiagram))
    }

    @Test
    fun `should validate that R is not included in R intersection S`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("R")

        assertFalse(homomorphismValidator.validate(leftDiagram, rightDiagram))
    }

    @Test
    fun `should validate that S is not included in R intersection S`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("S")

        assertFalse(homomorphismValidator.validate(leftDiagram, rightDiagram))
    }

    @Test
    fun `should validate that R intersection S is included in S intersection R and vice versa`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = normalIntersectionDiagram("S", "R")

        assertTrue(homomorphismValidator.validate(leftDiagram, rightDiagram))
        assertTrue(homomorphismValidator.validate(rightDiagram, leftDiagram))
    }

    @Test
    fun `should validate that R intersection S is included in R intersection R`() {
        val rightDiagram = normalIntersectionDiagram("R", "R")
        val leftDiagram = normalIntersectionDiagram("R", "S")

        assertTrue(homomorphismValidator.validate(leftDiagram, rightDiagram))
    }

    @Test
    fun `should validate that complex diagram H is included in diagram G but H is not included in G`(){
        val diagramG = buildDiagramG()
        val diagramH = buildDiagramH()
        assertTrue(homomorphismValidator.validate(diagramG, diagramH))
        assertFalse(homomorphismValidator.validate(diagramH, diagramG))
    }

    private fun buildDiagramG(): Diagram {
        val nodeA = Node(name = "a", nodeType = NodeTypeEnum.INTERMEDIATE)
        val nodeB = Node(name = "b", nodeType = NodeTypeEnum.INTERMEDIATE)
        val nodeC = Node(name = "c", nodeType = NodeTypeEnum.INTERMEDIATE)
        val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
        val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

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
        val nodeX = Node(name = "x", nodeType = NodeTypeEnum.INTERMEDIATE)
        val nodeY = Node(name = "y", nodeType = NodeTypeEnum.INTERMEDIATE)
        val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
        val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

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