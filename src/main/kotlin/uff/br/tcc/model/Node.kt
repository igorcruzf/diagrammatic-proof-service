package uff.br.tcc.model

import uff.br.tcc.enum.NodeTypeEnum

data class Node(
    val name: String,
    val nodeType: NodeTypeEnum,
    var nodeImageName: String? = null
)