package uff.br.tcc.service

import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.deepCopy
import uff.br.tcc.model.hasAnyNonAtomicTerm
import uff.br.tcc.transformer.DiagramTransformer

class DiagramService {

    private val diagramTransformer: DiagramTransformer = DiagramTransformer()

    fun transformToNormalForm(diagrammaticProof: DiagrammaticProof): DiagrammaticProof {
        while (diagrammaticProof.diagrams.last().edges.hasAnyNonAtomicTerm()) {
            createNextDiagram(diagrammaticProof)
        }
        return diagrammaticProof
    }

    private fun createNextDiagram(diagrammaticProof: DiagrammaticProof) {
        val diagram = diagrammaticProof.diagrams.last()
        val newDiagram = diagram.deepCopy()
        diagramTransformer.transformDiagram(newDiagram)
        diagrammaticProof.diagrams.add(newDiagram)
    }
}