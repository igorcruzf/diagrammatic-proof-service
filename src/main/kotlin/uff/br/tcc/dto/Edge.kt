package uff.br.tcc.dto

import com.fasterxml.jackson.annotation.JsonProperty
import uff.br.tcc.dto.term.ITerm
import java.util.UUID

data class Edge(

    @JsonProperty("left_node")
    val leftNode: Node,

    @JsonProperty("right_node")
    val rightNode: Node,

    val label: ITerm,

    val id: String = UUID.randomUUID().toString(),
)
