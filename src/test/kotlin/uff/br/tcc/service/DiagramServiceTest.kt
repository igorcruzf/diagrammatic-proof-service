package uff.br.tcc.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer
import uff.br.tcc.utils.buildDiagrammaticProofWithIntersection
import uff.br.tcc.utils.normalIntersectionDiagram

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
}
