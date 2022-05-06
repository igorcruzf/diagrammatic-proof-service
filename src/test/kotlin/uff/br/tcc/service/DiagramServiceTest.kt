package uff.br.tcc.service

import uff.br.tcc.utils.buildDiagrammaticProofWithIntersection
import uff.br.tcc.utils.normalIntersectionDiagram
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiagramServiceTest {

    private val diagramService = DiagramService()

    @Test
    fun `should transform R intersection S in normal form`(){
        val normalIntersectionDiagram = normalIntersectionDiagram("R", "S")
        val initialDiagramProof = buildDiagrammaticProofWithIntersection("R", "S")

        val lastStepDiagram = diagramService.transformToNormalForm(initialDiagramProof).diagrams.last()

        assertEquals(normalIntersectionDiagram.edges, lastStepDiagram.edges)
        assertEquals(normalIntersectionDiagram.nodes, lastStepDiagram.nodes)
    }
}