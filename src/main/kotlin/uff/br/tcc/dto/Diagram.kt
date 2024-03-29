package uff.br.tcc.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Diagram(

    val nodes: MutableList<Node>,

    val edges: MutableList<Edge>,

    @JsonProperty("step_description")
    var stepDescription: String,

    @JsonProperty("removed_edge")
    var removedEdge: Edge? = null,

    @JsonProperty("created_edges")
    var createdEdges: List<Edge>? = null,

    @JsonProperty("created_node")
    var createdNode: Node? = null,

    @JsonProperty("countermodel_relations")
    var countermodelRelations: Map<String, List<Pair<Int, Int>>>? = null
)
