package uff.br.tcc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uff.br.tcc.enum.NodeTypeEnum

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Node(
    val name: String,
    val type: NodeTypeEnum,
    @JsonProperty("image_name")
    var imageName: String? = null
)
