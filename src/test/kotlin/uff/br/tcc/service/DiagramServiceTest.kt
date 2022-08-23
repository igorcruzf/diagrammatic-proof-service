package uff.br.tcc.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uff.br.tcc.dto.Edge
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
    private val countermodelService = CountermodelService()
    private val hypothesisService = HypothesisService()

    private val diagramService = DiagramService(diagramTransformer, requestTransformer, homomorphismValidator, countermodelService, hypothesisService)

    @Test
    fun `should transform R intersection S in normal form`() {
        val normalIntersectionDiagram = normalIntersectionDiagram("R", "S")
        val diagrammaticProof = buildDiagrammaticProofWithIntersection("R", "S")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEdges(normalIntersectionDiagram.edges, lastStepDiagram.edges)
        assertEquals(normalIntersectionDiagram.nodes, lastStepDiagram.nodes)
    }

    @Test
    fun `should transform R composition S in normal form`() {
        val normalCompositionDiagram = normalCompositionDiagram("R", "S")
        val diagrammaticProof = buildDiagrammaticProofWithComposition("R", "S")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEquals(normalCompositionDiagram.edges.first().label, lastStepDiagram.edges.first().label)
        assertEquals(normalCompositionDiagram.edges.first().leftNode, lastStepDiagram.edges.first().leftNode)
        assertEquals(
            normalCompositionDiagram.edges.first().rightNode.type,
            lastStepDiagram.edges.first().rightNode.type
        )
        assertEquals(normalCompositionDiagram.edges.last().label, lastStepDiagram.edges.last().label)
        assertEquals(normalCompositionDiagram.edges.last().leftNode.type, lastStepDiagram.edges.last().leftNode.type)
        assertEquals(normalCompositionDiagram.edges.last().rightNode, lastStepDiagram.edges.last().rightNode)
    }

    @Test
    fun `should transform inverse of R in normal form`() {
        val normalInverseDiagram = normalInverseDiagram("R")
        val diagrammaticProof = buildDiagrammaticProofWithInverse("R")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)
        val lastStepDiagram = diagrammaticProof.diagrams.last()

        assertEdges(normalInverseDiagram.edges, lastStepDiagram.edges)
    }

    @Test
    fun `should transform expression in two diagrammatic proofs with normal diagrams and should be homomorphic in both directions`() {
        val expression = "(R comp S)int T inc ((R int(T comp (Sinv))) comp (((Rinv) comp T) int S)) int T"
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression)
        assertTrue(diagrammaticProofResponse.countermodel.isHomomorphic!!)

        val expression2 = "((R int(T comp (Sinv))) comp (((Rinv) comp T) int S)) int T inc (R comp S)int T"
        val diagrammaticProofResponse2 = diagramService.transformDiagramsAndValidateHomomorphism(expression2)
        assertTrue(diagrammaticProofResponse2.countermodel.isHomomorphic!!)
    }

    @Test
    fun `should apply hypothesis to transform in homomorphic diagrams when hypothesis is equal expression`() {
        val expression = "AcompB inc A"
        val hypothesis = listOf("AcompB inc A")
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression, hypothesis)
        assertTrue(diagrammaticProofResponse.countermodel.isHomomorphic!!)
    }

    @Test
    fun `should apply multiple hypotheses to transform in homomorphic diagrams`() {
        val expression = "A inc B"
        val hypotheses = listOf("A inc C", "C inc D", "D inc E comp F", "E comp F inc B")
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression, hypotheses)
        assertTrue(diagrammaticProofResponse.countermodel.isHomomorphic!!)
    }

//    @Test
    fun `should apply multiple hypotheses to transform but doesnt find homomorphism`() {
        val expression = "A inc B"
        val hypotheses = listOf("A inc C", "C inc A")
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression, hypotheses)
        assertFalse(diagrammaticProofResponse.countermodel.isHomomorphic!!)
    }

    @Test
    fun `should not apply hypotheses`() {
        val expression = "A inc B"
        val hypotheses = listOf("B inc C", "C inc D", "D inc E comp F", "E comp F inc B")
        val diagrammaticProofResponse = diagramService.transformDiagramsAndValidateHomomorphism(expression, hypotheses)
        assertFalse(diagrammaticProofResponse.countermodel.isHomomorphic!!)
    }

    private fun assertEdges(firstEdges: List<Edge>, secondEdges: List<Edge>) {
        assertEquals(firstEdges.size, secondEdges.size)
        firstEdges.forEach { firstEdge ->
            assertNotNull(
                secondEdges.find {
                    it.label == firstEdge.label &&
                        it.leftNode == firstEdge.leftNode &&
                        it.rightNode == firstEdge.rightNode
                }
            )
        }
    }
}
