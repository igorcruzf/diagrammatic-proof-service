package uff.br.tcc.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer
import uff.br.tcc.utils.buildDiagrammaticProofWithComposition
import uff.br.tcc.utils.buildDiagrammaticProofWithIntersection
import uff.br.tcc.utils.buildDiagrammaticProofWithInverse
import uff.br.tcc.utils.normalCompositionDiagram
import uff.br.tcc.utils.normalIntersectionDiagram
import uff.br.tcc.utils.normalInverseDiagram

class DiagramServiceTest {

    private val requestTransformer = RequestTransformer()
    private val diagramTransformer = DiagramTransformer()
    private val homomorphismValidator = HomomorphismValidator()

    private val diagramService = DiagramService(diagramTransformer, requestTransformer, homomorphismValidator)

    @Test
    fun `should transform R intersection S in normal form`() {
        val normalIntersectionDiagram = normalIntersectionDiagram("R", "S")
        val diagrammaticProof = buildDiagrammaticProofWithIntersection("R", "S")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEquals(normalIntersectionDiagram.edges, lastStepDiagram.edges)
        assertEquals(normalIntersectionDiagram.nodes, lastStepDiagram.nodes)
    }

    @Test
    fun `should transform R composition S in normal form`() {
        val normalCompositionDiagram = normalCompositionDiagram("R", "S")
        val diagrammaticProof = buildDiagrammaticProofWithComposition("R", "S")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEquals(normalCompositionDiagram.edges.first().term, lastStepDiagram.edges.first().term)
        assertEquals(normalCompositionDiagram.edges.first().leftNode, lastStepDiagram.edges.first().leftNode)
        assertEquals(
            normalCompositionDiagram.edges.first().rightNode.type,
            lastStepDiagram.edges.first().rightNode.type
        )
        assertEquals(normalCompositionDiagram.edges.last().term, lastStepDiagram.edges.last().term)
        assertEquals(normalCompositionDiagram.edges.last().leftNode.type, lastStepDiagram.edges.last().leftNode.type)
        assertEquals(normalCompositionDiagram.edges.last().rightNode, lastStepDiagram.edges.last().rightNode)
    }

    @Test
    fun `should transform inverse of R in normal form`() {
        val normalInverseDiagram = normalInverseDiagram("R")
        val diagrammaticProof = buildDiagrammaticProofWithInverse("R")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEquals(normalInverseDiagram.edges, lastStepDiagram.edges)
    }

    @Test
    fun `should transform expression in two diagrammatic proofs with normaldiagrams and should be homomorphic in both directions`() {
        val expression = "(R \\circ S)\\cap T \\subseteq ((R \\cap(T \\circ (S\\inv))) \\circ (((R\\inv) \\circ T) \\cap S)) \\cap T"
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression)
        assertTrue(diagrammaticProofResponse.isHomomorphic)

        val expression2 = "((R \\cap(T \\circ (S\\inv))) \\circ (((R\\inv) \\circ T) \\cap S)) \\cap T \\subseteq (R \\circ S)\\cap T"
        val diagrammaticProofResponse2 = diagramService.transformDiagramsAndValidateHomomorphism(expression2)
        assertTrue(diagrammaticProofResponse2.isHomomorphic)
    }
}
