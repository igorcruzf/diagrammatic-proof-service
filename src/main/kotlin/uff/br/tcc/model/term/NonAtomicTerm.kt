package uff.br.tcc.model.term

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import uff.br.tcc.enum.OperationEnum

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NonAtomicTerm(

    @JsonProperty("left_term")
    val leftTerm: ITerm,

    val operation: OperationEnum,

    @JsonProperty("right_term")
    val rightTerm: ITerm? = null

) : ITerm {

    override fun name(): String {
        val stringRightTerm = rightTerm?.let { " $rightTerm" } ?: ""
        return "($leftTerm" + operation.symbol() + "$stringRightTerm)"
    }
}
