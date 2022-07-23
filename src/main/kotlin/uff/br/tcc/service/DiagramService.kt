package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.DiagrammaticProofResponse
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.extensions.deepCopy
import uff.br.tcc.extensions.hasAnyNonAtomicTerm
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer

@Service
class DiagramService(
    @Autowired private val diagramTransformer: DiagramTransformer,
    @Autowired private val requestTransformer: RequestTransformer,
    @Autowired private val homomorphismValidator: HomomorphismValidator
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun transformDiagramsAndValidateHomomorphism(diagramsRequest: String): DiagrammaticProofResponse {
        val diagrams = requestTransformer.splitToDiagrams(diagramsRequest)
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())

        addDiagramsUntilNormalForm(leftDiagrammaticProof)
        logger.info("All diagrams in left diagrammatic proof = $leftDiagrammaticProof.")
        addDiagramsUntilNormalForm(rightDiagrammaticProof)
        logger.info("All diagrams in right diagrammatic proof = $rightDiagrammaticProof.")

        val isHomomorphic = homomorphismValidator.validate(
            HomomorphismValidatorRequest(
                leftDiagram = leftDiagrammaticProof.diagrams.last(),
                rightDiagram = rightDiagrammaticProof.diagrams.last()
            )

        )

        logger.info(
            "Homomorphism = $isHomomorphic with left diagram ${leftDiagrammaticProof.diagrams.last()}" +
                " and right diagram ${rightDiagrammaticProof.diagrams.last()}."
        )
        return DiagrammaticProofResponse(
            leftDiagrammaticProof = leftDiagrammaticProof,
            rightDiagrammaticProof = rightDiagrammaticProof,
            isHomomorphic = isHomomorphic
        )
    }

    fun addDiagramsUntilNormalForm(diagrammaticProof: DiagrammaticProof) {
        logger.info("Adding diagrams in $diagrammaticProof until acquiring normal form.")
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
