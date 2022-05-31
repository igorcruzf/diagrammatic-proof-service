package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import uff.br.tcc.model.term.ITerm

data class Edge(

    @JsonProperty("left_node")
    val leftNode: Node,

    @JsonProperty("right_node")
    val rightNode: Node,

    val term: ITerm,

    @get:JsonIgnore
    var isMappedInLeftDiagram: Boolean = false
)
