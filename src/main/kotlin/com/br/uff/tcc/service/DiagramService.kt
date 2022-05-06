package com.br.uff.tcc.service

import com.br.uff.tcc.model.DiagrammaticProof
import com.br.uff.tcc.model.deepCopy
import com.br.uff.tcc.model.hasAnyNonAtomicTerm
import com.br.uff.tcc.transformer.DiagramTransformer

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