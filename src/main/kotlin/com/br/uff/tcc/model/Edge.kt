package com.br.uff.tcc.model

data class Edge(val leftNode: Node, val rightNode: Node, val term: ITerm, var isMapped: Boolean = false)

fun MutableList<Edge>.hasAnyNonAtomicTerm(): Boolean {
    var hasNonAtomicTermFlag = false
    for(edge in this) {
        if(edge.term is Term)
            hasNonAtomicTermFlag = true
    }
    return hasNonAtomicTermFlag
}

fun MutableList<Edge>.getFirstEdgeWithNonAtomicTerm(): Edge {
    for(edge in this) {
        if(edge.term is Term)
            return edge
    }

    throw Exception("Tem que ter pelo menos um termo não atomico")
}

fun List<Edge>.plus(edge: Edge): List<Edge> {
    return mutableListOf<Edge>().also { it.addAll(this) && it.add(edge) }
}