package uff.br.tcc.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer
import uff.br.tcc.utils.buildDiagrammaticProofWithInverse

class CounterModelServiceTest {

    private val requestTransformer = RequestTransformer()
    private val diagramTransformer = DiagramTransformer()
    private val homomorphismValidator = HomomorphismValidator()
    private val countermodelService = CountermodelService()

    private val diagramService = DiagramService(diagramTransformer, requestTransformer, homomorphismValidator, countermodelService)

    @Test
    fun `should validate that inverse of R is included in itself`() {
        val diagrammaticProof = buildDiagrammaticProofWithInverse("R")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)

        assert(countermodelService.createCountermodel(diagrammaticProof, diagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R is included in itself`() {
        val diagrammaticProof = buildDiagrammaticProofWithInverse("R")
        diagramService.addDiagramsUntilNormalForm(diagrammaticProof)

        assert(countermodelService.createCountermodel(diagrammaticProof, diagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R is not included in S`() {
        val diagrams = requestTransformer.splitToDiagrams("R inc S")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assertFalse(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R in included in R`() {
        val diagrams = requestTransformer.splitToDiagrams("R inc R")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R intersection S in included in R`() {
        val diagrams = requestTransformer.splitToDiagrams("R int S inc R")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R intersection S in included in S`() {
        val diagrams = requestTransformer.splitToDiagrams("R int S inc S")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R is not included in R intersection S`() {
        val diagrams = requestTransformer.splitToDiagrams("R inc R int S")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assertFalse(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that S is not included in R intersection S`() {
        val diagrams = requestTransformer.splitToDiagrams("S inc R int S")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assertFalse(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R intersection S is included in S intersection R and vice versa`() {
        val diagrams = requestTransformer.splitToDiagrams("R int S inc S int R")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
        assert(countermodelService.createCountermodel(rightDiagrammaticProof, leftDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that R intersection S is included in R intersection R`() {
        val diagrams = requestTransformer.splitToDiagrams("R int S inc R int R")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that (A comp B) int (A comp C) is not included in A comp (B int C)`() {
        val diagrams = requestTransformer.splitToDiagrams("(A comp B) int (A comp C) inc A comp (B int C)")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assertFalse(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that (A int (B inv)) comp C is included in (A comp C) int ((B inv) comp C)`() {
        val diagrams = requestTransformer
            .splitToDiagrams("(A int (B inv)) comp C inc (A comp C) int ((B inv) comp C)")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assert(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }

    @Test
    fun `should validate that (A comp C) int ((B inv) comp C) is not included in (A int (B inv)) comp C`() {
        val diagrams = requestTransformer
            .splitToDiagrams("(A comp C) int ((B inv) comp C) inc (A int (B inv)) comp C")
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        diagramService.addDiagramsUntilNormalForm(leftDiagrammaticProof)
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())
        diagramService.addDiagramsUntilNormalForm(rightDiagrammaticProof)
        assertFalse(countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof).isHomomorphic!!)
    }
}
