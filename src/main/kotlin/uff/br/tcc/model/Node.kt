package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonInclude
import uff.br.tcc.enum.NodeTypeEnum

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Node(
    val name: String,
    val type: NodeTypeEnum,
    var imageName: String? = null
)
