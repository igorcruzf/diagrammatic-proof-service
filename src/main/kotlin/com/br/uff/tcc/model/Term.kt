package com.br.uff.tcc.model

import com.br.uff.tcc.enum.Operation

sealed interface ITerm

data class AtomicTerm(val name: String) : ITerm {
    override fun toString() = name
}

data class Term(
    val leftTerm: ITerm,
    val operation: Operation,
    val rightTerm: ITerm? = null): ITerm {

    override fun toString(): String {
        val stringRightTerm = rightTerm?.let { " $rightTerm" } ?: ""
        return "($leftTerm" + operation.symbol() + "$stringRightTerm)"
    }
}