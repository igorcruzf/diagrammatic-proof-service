package com.br.uff.tcc.model

import com.br.uff.tcc.enum.NodeTypeEnum

data class Node(
    val name: String,
    val nodeType: NodeTypeEnum
)