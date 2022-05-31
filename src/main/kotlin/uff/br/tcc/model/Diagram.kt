package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Diagram(

    val nodes: MutableList<Node>,

    val edges: MutableList<Edge>,

    @JsonProperty("step_description")
    var stepDescription: String
)
