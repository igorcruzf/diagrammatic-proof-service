package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uff.br.tcc.dto.CountermodelResponse
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.DiagrammaticProofResponse
import uff.br.tcc.dto.HomomorphismRequest
import uff.br.tcc.extensions.deepCopy
import uff.br.tcc.extensions.hasAnyNonAtomicTerm
import uff.br.tcc.transformer.DiagramTransformer
import uff.br.tcc.transformer.RequestTransformer

@Service
class DiagramService(
    private val diagramTransformer: DiagramTransformer,
    private val requestTransformer: RequestTransformer,
    private val homomorphismFinder: HomomorphismFinder,
    private val countermodelService: CountermodelService,
    private val hypothesisService: HypothesisService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun validateHomomorphism(
        diagramsRequest: String,
        hypotheses: List<String> = listOf()
    ): DiagrammaticProofResponse {
        val (leftDiagrammaticProof, rightDiagrammaticProof) = transformDiagrams(diagramsRequest)

        val countermodelResponse = countermodelService.createCountermodel(leftDiagrammaticProof, rightDiagrammaticProof)

        if (countermodelResponse.isHomomorphic!!) {
            homomorphismFinder.find(
                HomomorphismRequest(
                    leftDiagram = leftDiagrammaticProof.diagrams.last(),
                    rightDiagram = rightDiagrammaticProof.diagrams.last()
                )
            )
        } else if (hypotheses.isNotEmpty()) {
            return validateWithHypotheses(
                hypotheses,
                leftDiagrammaticProof,
                rightDiagrammaticProof,
                countermodelResponse
            )
        }

        return buildProofResponse(countermodelResponse, leftDiagrammaticProof, rightDiagrammaticProof)
    }

    private fun validateWithHypotheses(
        hypotheses: List<String>,
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof,
        countermodelResponse: CountermodelResponse,
    ): DiagrammaticProofResponse {
        val hypothesesResponse = findHomomorphismWithHypothesesOrNull(
            hypotheses = hypotheses,
            leftDiagrammaticProof = leftDiagrammaticProof,
            rightDiagrammaticProof = rightDiagrammaticProof
        )

        return hypothesesResponse?.let {
            countermodelResponse.isHomomorphic = true
            buildProofResponse(countermodelResponse, hypothesesResponse, rightDiagrammaticProof)
        } ?: buildProofResponse(countermodelResponse, leftDiagrammaticProof, rightDiagrammaticProof)
    }

    private fun transformDiagrams(diagramsRequest: String): Pair<DiagrammaticProof, DiagrammaticProof> {
        val diagrams = requestTransformer.splitToDiagrams(diagramsRequest)
        val leftDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.first())
        val rightDiagrammaticProof = requestTransformer.transformToDiagrammaticProof(diagrams.last())

        addDiagramsUntilNormalForm(leftDiagrammaticProof)
        logger.info("All diagrams in left diagrammatic proof = $leftDiagrammaticProof.")
        addDiagramsUntilNormalForm(rightDiagrammaticProof)
        logger.info("All diagrams in right diagrammatic proof = $rightDiagrammaticProof.")
        return Pair(leftDiagrammaticProof, rightDiagrammaticProof)
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

    private fun findHomomorphismWithHypothesesOrNull(
        hypotheses: List<String>,
        leftDiagrammaticProof: DiagrammaticProof,
        rightDiagrammaticProof: DiagrammaticProof
    ): DiagrammaticProof? {
        var diagrammaticProofsToApplyHypothesis = listOf(leftDiagrammaticProof)

        val normalizedHypotheses = normalizeHypotheses(hypotheses)

        for (i in (0..MAX_LOOPS)) {
            logger.info("Trying to apply hypothesis in loop $i")
            diagrammaticProofsToApplyHypothesis = diagrammaticProofsToApplyHypothesis.map {
                normalizedHypotheses.mapNotNull { (leftHypothesis, rightHypothesis) ->
                    val isHypothesisApplicable = hypothesisService.find(
                        HomomorphismRequest(
                            leftDiagram = it.diagrams.last(),
                            rightDiagram = leftHypothesis.diagrams.last()
                        )
                    )

                    if (isHypothesisApplicable) {

                        val newLeftDiagrammaticProof = applyHypothesisInLeftDiagrammaticProof(
                            it,
                            leftHypothesis,
                            rightHypothesis
                        )

                        val isHomomorphic = homomorphismFinder.find(
                            HomomorphismRequest(
                                newLeftDiagrammaticProof.diagrams.last(),
                                rightDiagrammaticProof.diagrams.last()
                            )
                        )

                        if (isHomomorphic) {
                            logger.info("Found homomorphism with hypotheses")
                            return newLeftDiagrammaticProof
                        } else {
                            newLeftDiagrammaticProof
                        }
                    } else null
                }
            }.flatten()
        }
        return null
    }

    private fun applyHypothesisInLeftDiagrammaticProof(
        leftDiagrammaticProof: DiagrammaticProof,
        leftHypothesis: DiagrammaticProof,
        rightHypothesis: DiagrammaticProof,
    ): DiagrammaticProof {
        val newLeftDiagrammaticProof = leftDiagrammaticProof.deepCopy()
        newLeftDiagrammaticProof.diagrams.add(
            hypothesisService.addHypothesis(
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

    companion object {
        const val MAX_LOOPS = 5
    }
}
