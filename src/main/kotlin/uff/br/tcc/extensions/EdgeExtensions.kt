package uff.br.tcc.extensions

import uff.br.tcc.model.Edge
import uff.br.tcc.model.term.NonAtomicTerm

fun MutableList<Edge>.hasAnyNonAtomicTerm(): Boolean {
    return this.any {
        it.label is NonAtomicTerm
    }
}

fun MutableList<Edge>.getFirstEdgeWithNonAtomicTerm(): Edge {
    return this.first {
        it.label is NonAtomicTerm
    }
}
