package uff.br.tcc.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uff.br.tcc.enum.OperationEnum

sealed interface ITerm {
    fun name(): String
}

data class AtomicTerm(val name: String) : ITerm {
    override fun name() = name
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Term(
    @JsonProperty("left_term") val leftTerm: ITerm,
    val operation: OperationEnum,
    @JsonProperty("right_term") val rightTerm: ITerm? = null
) : ITerm {

    override fun name(): String {
        val stringRightTerm = rightTerm?.let { " $rightTerm" } ?: ""
        return "($leftTerm" + operation.symbol() + "$stringRightTerm)"
    }
}
