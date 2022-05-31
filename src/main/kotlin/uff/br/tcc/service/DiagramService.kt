package uff.br.tcc.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uff.br.tcc.extensions.deepCopy
import uff.br.tcc.extensions.hasAnyNonAtomicTerm
import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.DiagrammaticProofResponse
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer

@Service
class DiagramService(
    @Autowired private val diagramTransformer: DiagramTransformer,
    @Autowired private val requestTransformer: RequestTransformer,
    @Autowired private val homomorphismValidator: HomomorphismValidator
) {

    fun transformDiagramsAndValidateHomomorphism(diagramsRequest: String): DiagrammaticProofResponse {
        val diagrams = requestTransformer.splitToDiagrams(diagramsRequest)
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())

        addDiagramsUntilNormalForm(leftDiagrammaticProof)
        addDiagramsUntilNormalForm(rightDiagrammaticProof)

        val isHomomorphic = homomorphismValidator.validate(
            leftDiagrammaticProof.diagrams.last(),
            rightDiagrammaticProof.diagrams.last()
        )

        return DiagrammaticProofResponse(
            leftDiagrammaticProof = leftDiagrammaticProof,
            rightDiagrammaticProof = rightDiagrammaticProof,
            isHomomorphic = isHomomorphic
        )
    }

    fun addDiagramsUntilNormalForm(diagrammaticProof: DiagrammaticProof) {
        while (diagrammaticProof.diagrams.last().edges.hasAnyNonAtomicTerm()) {
            diagrammaticProof.createNextDiagram()
        }
    }

    private fun DiagrammaticProof.createNextDiagram() {
        val diagram = this.diagrams.last()
        val newDiagram = diagram.deepCopy()
        diagramTransformer.transformDiagram(newDiagram)
        this.diagrams.add(newDiagram)
    }
}
