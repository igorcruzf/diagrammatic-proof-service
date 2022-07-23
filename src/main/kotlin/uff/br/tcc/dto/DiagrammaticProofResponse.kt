package uff.br.tcc.dto

import com.fasterxml.jackson.annotation.JsonProperty

class DiagrammaticProofResponse(

    @JsonProperty("left_diagrammatic_proof")
    val leftDiagrammaticProof: DiagrammaticProof,

    @JsonProperty("right_diagrammatic_proof")
    val rightDiagrammaticProof: DiagrammaticProof,

    @JsonProperty("countermodel")
    val countermodel: CountermodelResponse
)

data class DiagrammaticProof(
    val diagrams: MutableList<Diagram>
)

data class CountermodelResponse(
    val universe: Map<String, Int>,

    val relations: Map<String, List<Pair<Int, Int>>>,

    @JsonProperty("is_homomorphic")
    var isHomomorphic: Boolean? = null
)
