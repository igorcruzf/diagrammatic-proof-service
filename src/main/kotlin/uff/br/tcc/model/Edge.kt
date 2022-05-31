package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Edge(
    @JsonProperty("left_node") val leftNode: Node,
    @JsonProperty("right_node") val rightNode: Node,
    val term: ITerm,

    @get:JsonIgnore
    var isMappedInLeftDiagram: Boolean = false
)

fun MutableList<Edge>.hasAnyNonAtomicTerm(): Boolean {
    return this.any {
        it.term is Term
    }
}

fun MutableList<Edge>.getFirstEdgeWithNonAtomicTerm(): Edge {
    return this.first {
        it.term is Term
    }
}
