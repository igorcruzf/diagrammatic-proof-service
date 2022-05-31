package uff.br.tcc.transformer

import org.springframework.stereotype.Component
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.AtomicTerm
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.Edge
import uff.br.tcc.model.ITerm
import uff.br.tcc.model.Node
import uff.br.tcc.model.Term

@Component
class RequestTransformer {

    fun transformToDiagrammaticProof(diagramStringRequest: String): DiagrammaticProof {
        val inputNode = Node("input", NodeTypeEnum.INPUT)
        val outputNode = Node("output", NodeTypeEnum.OUTPUT)
        return DiagrammaticProof(
            mutableListOf(
                Diagram(
                    nodes = mutableListOf(inputNode, outputNode),
                    edges = mutableListOf(
                        Edge(
                            leftNode = inputNode,
                            term = diagramStringRequest.transformToTerms(),
                            rightNode = outputNode
                        )
                    ),
                    stepDescription = StepDescriptionEnum.BEGIN.name
                )
            )
        )
    }

    private fun String.containsAnyOperator() =
        OperationEnum.values().any {
            this.contains(it.symbol())
        }

    private fun String.getFirstOperator(): OperationEnum? {
        return OperationEnum.values().map {
            it to this.indexOf(it.symbol())
        }.sortedBy {
            it.second
        }.firstOrNull {
            it.second != -1
        }?.first
    }

    fun String.transformToTerms(): ITerm {
        val term = if (this.first() == '(') {
            transformTermInParenthesis()
        } else {
            splitInTerms(this)
        }
        return term
    }

    private fun String.transformTermInParenthesis(): ITerm {
        val leftParenthesisIndex = this.lastIndexOf(')')
        val leftTerm = this.take(leftParenthesisIndex).drop(1)
        val operationAndRightTerm = getOperationAndRightTerm(leftParenthesisIndex)
        val operation = operationAndRightTerm?.getFirstOperator()
        return operation?.let {
            val rightTerm = getRightTerm(operationAndRightTerm, operation)
            transformToNonAtomicTerm(leftTerm, operation, rightTerm)
        } ?: splitInTerms(leftTerm)
    }

    private fun getRightTerm(
        operationAndRightTerm: String,
        operation: OperationEnum,
    ) = operationAndRightTerm
        .removeRange(0 until operation.symbol().length)
        .ifEmpty {
            null
        }

    private fun String.getOperationAndRightTerm(leftParenthesisIndex: Int): String? {
        val operationAndRightTerm = if (this.length != leftParenthesisIndex + 1) {
            this.removeRange(0..leftParenthesisIndex)
        } else null
        return operationAndRightTerm
    }

    private fun splitInTerms(term: String): ITerm {
        val operator = term.getFirstOperator()
        return operator?.let {
            val (leftTerm, rightTerm) = term.split(operator.symbol(), limit = 2)
            transformToNonAtomicTerm(leftTerm, operator, rightTerm)
        } ?: term.transformToAtomicTerm()
    }

    fun String.transformToAtomicTerm() = AtomicTerm(
        this.replace("(", "").replace(")", "")
    )

    fun String.transformToTerm() =
        if (this.containsAnyOperator()) {
            this.transformToTerms()
        } else {
            this.transformToAtomicTerm()
        }

    fun transformToNonAtomicTerm(leftTerm: String, operationEnum: OperationEnum, rightTerm: String?): Term {
        return Term(leftTerm.transformToTerm(), operationEnum, rightTerm?.transformToTerm())
    }
}
