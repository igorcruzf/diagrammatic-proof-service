package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonProperty

class DiagrammaticProofResponse(

    @JsonProperty("left_diagrammatic_proof")
    val leftDiagrammaticProof: DiagrammaticProof,

    @JsonProperty("right_diagrammatic_proof")
    val rightDiagrammaticProof: DiagrammaticProof,

    val isHomomorphic: Boolean
)
