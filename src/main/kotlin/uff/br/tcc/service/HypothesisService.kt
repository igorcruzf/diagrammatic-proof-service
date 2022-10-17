package uff.br.tcc.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.HomomorphismRequest
import uff.br.tcc.dto.INPUT_NODE_NAME
import uff.br.tcc.dto.OUTPUT_NODE_NAME
import uff.br.tcc.extensions.deepCopy

@Service
class HypothesisService : RelaxedHomomorphismFinder() {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun find(homomorphismRequest: HomomorphismRequest): Boolean {
        logger.debug(
            "Validating leftDiagram ${homomorphismRequest.leftDiagram} and " +
                "rightDiagram ${homomorphismRequest.rightDiagram}."
        )

        return homomorphismRequest.leftDiagram.nodes.any {
            find(
                homomorphismRequest = homomorphismRequest,
                leftDiagramNode = it,
                rightDiagramNode = homomorphismRequest.rightDiagram.nodes.first(),
                edgesPath = listOf()
            )
        }
    }

    fun addHypothesis(diagram: Diagram, hypothesisDiagram: Pair<DiagrammaticProof, DiagrammaticProof>): Diagram {
        val diagramWithHypothesis = diagram.deepCopy()

        diagramWithHypothesis.edges.add(
            Edge(
                leftNode = diagramWithHypothesis.nodes.first { node ->
                    node.name == hypothesisDiagram.first.diagrams.last().nodes.first {
                        it.name == INPUT_NODE_NAME
                    }.imageName
                },
                rightNode = diagramWithHypothesis.nodes.first { node ->
                    node.name == hypothesisDiagram.first.diagrams.last().nodes.first {
                        it.name == OUTPUT_NODE_NAME
                    }.imageName
                },
                label = hypothesisDiagram.second.diagrams.first().edges.first().label.deepCopy()
            )
        )

        diagramWithHypothesis.stepDescription = "Adding ${hypothesisDiagram.second.diagrams.first().edges
            .first().label.name()} with hypothesis"

        return diagramWithHypothesis
    }
}
