package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uff.br.tcc.dto.CountermodelResponse
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.DiagrammaticProofResponse
import uff.br.tcc.dto.HomomorphismValidatorRequest
import uff.br.tcc.extensions.deepCopy
import uff.br.tcc.extensions.hasAnyNonAtomicTerm
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer

@Service
class DiagramService(
    private val diagramTransformer: DiagramTransformer,
    private val requestTransformer: RequestTransformer,
    private val homomorphismValidator: HomomorphismValidator,
    private val countermodelService: CountermodelService,
    private val hypothesisService: HypothesisService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun transformDiagramsAndValidateHomomorphism(
        diagramsRequest: String,
        hypotheses: List<String> = listOf()
    ): DiagrammaticProofResponse {
        val diagrams = requestTransformer.splitToDiagrams(diagramsRequest)
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())

        addDiagramsUntilNormalForm(leftDiagrammaticProof)
        logger.info("All diagrams in left diagrammatic proof = $leftDiagrammaticProof.")
        addDiagramsUntilNormalForm(rightDiagrammaticProof)
        logger.info("All diagrams in right diagrammatic proof = $rightDiagrammaticProof.")

        val countermodelResponse = countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof)

        if (countermodelResponse.isHomomorphic!!) {
            homomorphismValidator.validate(
                HomomorphismValidatorRequest(
                    leftDiagram = leftDiagrammaticProof.diagrams.last(),
                    rightDiagram = rightDiagrammaticProof.diagrams.last()
                )
            )
        } else if (hypotheses.isNotEmpty()) {
            val hypothesesResponse = findHomomorphismWithHypotheses(
                hypotheses = hypotheses,
                leftDiagrammaticProof = leftDiagrammaticProof,
                rightDiagrammaticProof = rightDiagrammaticProof
            )

            if (hypothesesResponse != null) {
                homomorphismValidator.validate(
                    HomomorphismValidatorRequest(
                        leftDiagram = hypothesesResponse.first.diagrams.last(),
                        rightDiagram = rightDiagrammaticProof.diagrams.last()
                    )
                )
                return buildProofResponse(hypothesesResponse.second, hypothesesResponse.first, rightDiagrammaticProof)
            }
        }

        return buildProofResponse(countermodelResponse, leftDiagrammaticProof, rightDiagrammaticProof)
    }

    private fun buildProofResponse(
        countermodelResponse: CountermodelResponse,
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof,
    ): DiagrammaticProofResponse {
        logger.info(
            "Homomorphism = ${countermodelResponse.isHomomorphic} " +
                "with left diagram ${leftDiagrammaticProof.diagrams.last()}" +
                " and right diagram ${rightDiagrammaticProof.diagrams.last()}."
        )
        return DiagrammaticProofResponse(
            leftDiagrammaticProof = leftDiagrammaticProof,
            rightDiagrammaticProof = rightDiagrammaticProof,
            countermodel = countermodelResponse
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

    private fun findHomomorphismWithHypotheses(
        hypotheses: List<String>,
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof
    ): Pair<DiagrammaticProof, CountermodelResponse>? {
        var diagrammaticProofsToApplyHypothesis = listOf(leftDiagrammaticProof)

        val normalizedHypotheses = normalizeHypotheses(hypotheses)

        for (i in (0..100)) {
            logger.info("Trying to apply hypothesis in loop $i")
            diagrammaticProofsToApplyHypothesis = diagrammaticProofsToApplyHypothesis.map {
                normalizedHypotheses.mapNotNull { (leftHypothesis, rightHypothesis) ->
                    val isHypothesisApplicable = hypothesisService.applyHypothesis(
                        HomomorphismValidatorRequest(
                            leftDiagram = it.diagrams.last(),
                            rightDiagram = leftHypothesis.diagrams.last()
                        )
                    )

                    if (isHypothesisApplicable) {
                        val newLeftDiagrammaticProof =
                            createNewLeftDiagrammaticProof(it, leftHypothesis, rightHypothesis)

                        val countermodelResponse = countermodelService.createCountermodel(
                            newLeftDiagrammaticProof, rightDiagrammaticProof
                        )

                        if (countermodelResponse.isHomomorphic!!) {
                            logger.info("Found homomorphism with hypotheses")
                            return Pair(newLeftDiagrammaticProof, countermodelResponse)
                        } else {
                            newLeftDiagrammaticProof
                        }
                    } else null
                }
            }.flatten()
        }
        return null
    }

    private fun createNewLeftDiagrammaticProof(
        leftDiagrammaticProof: DiagrammaticProof,
        leftHypothesis: DiagrammaticProof,
        rightHypothesis: DiagrammaticProof,
    ): DiagrammaticProof {
        val newLeftDiagrammaticProof = leftDiagrammaticProof.deepCopy()
        newLeftDiagrammaticProof.diagrams.add(
            diagramTransformer.addHypothesis(
                leftDiagrammaticProof.diagrams.last(),
                Pair(leftHypothesis, rightHypothesis)
            )
        )
        addDiagramsUntilNormalForm(newLeftDiagrammaticProof)
        logger.debug("All diagrams in left diagrammatic proof = $newLeftDiagrammaticProof.")
        return newLeftDiagrammaticProof
    }

    private fun normalizeHypotheses(hypotheses: List<String>) = hypotheses.map {
        val hypothesisDiagrams = requestTransformer.splitToDiagrams(it)
        val leftHypothesisDiagramsProof =
            requestTransformer.transformToDiagrammaticProof(hypothesisDiagrams.first())
        val rightHypothesisDiagramsProof =
            requestTransformer.transformToDiagrammaticProof(hypothesisDiagrams.last())

        addDiagramsUntilNormalForm(leftHypothesisDiagramsProof)
        logger.debug("All diagrams in left diagrammatic proof = $leftHypothesisDiagramsProof.")
        addDiagramsUntilNormalForm(rightHypothesisDiagramsProof)
        logger.debug("All diagrams in right diagrammatic proof = $rightHypothesisDiagramsProof.")
        Pair(leftHypothesisDiagramsProof, rightHypothesisDiagramsProof)
    }
}
