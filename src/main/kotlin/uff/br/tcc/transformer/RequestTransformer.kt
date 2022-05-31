package uff.br.tcc.transformer

import org.springframework.stereotype.Component
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.model.term.AtomicTerm
import uff.br.tcc.model.term.ITerm
import uff.br.tcc.model.term.NonAtomicTerm

@Component
class RequestTransformer {

    companion object {
        const val SUB_SET_EQUALS = "\\subseteq"
    }

    fun splitToDiagrams(expression: String): List<String> {
        val diagrams = expression.split(SUB_SET_EQUALS)
        if (diagrams.count() != 2) {
            throw IllegalArgumentException("Should have 1 inclusion in expression but has ${diagrams.count() - 1}")
        }
        return diagrams
    }

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
                            term = transformToTerms(diagramStringRequest.filter { !it.isWhitespace() }),
                            rightNode = outputNode
                        )
                    ),
                    stepDescription = StepDescriptionEnum.BEGIN.name
                )
            )
        )
    }

    fun containsAnyOperator(expression: String) =
        OperationEnum.values().any {
            expression.contains(it.symbol())
        }

    fun getFirstOperation(expression: String): OperationEnum? {
        return OperationEnum.values().map {
            it to expression.indexOf(it.symbol())
        }.sortedBy {
            it.second
        }.firstOrNull {
            it.second != -1
        }?.first
    }

    fun transformToTerms(expression: String): ITerm {
        val term = if (expression.first() == '(') {
            transformTermInParenthesis(expression)
        } else {
            splitInTerms(expression)
        }
        return term
    }

    fun getIndexOfLeftParenthesis(expression: String): Int {
        var rightParenthesisCount = 1
        var leftParenthesisIndex = 1
        expression.drop(1).takeWhile {
            if (it == '(') {
                rightParenthesisCount += 1
            }
            if (it == ')') {
                rightParenthesisCount -= 1
            }
            if (rightParenthesisCount != 0) {
                leftParenthesisIndex += 1
            }
            rightParenthesisCount != 0
        }

        return leftParenthesisIndex
    }

    fun transformTermInParenthesis(expression: String): ITerm {
        val leftParenthesisIndex = getIndexOfLeftParenthesis(expression)
        val leftTerm = expression.take(leftParenthesisIndex).drop(1)
        val operationAndRightTerm = getOperationAndRightTerm(expression, leftParenthesisIndex)
        val operation = operationAndRightTerm?.let {
            getFirstOperation(operationAndRightTerm)
        }
        return operation?.let {
            val rightTerm = getRightTerm(operationAndRightTerm, operation)
            transformToNonAtomicTerm(leftTerm, operation, rightTerm)
        } ?: transformToTerms(leftTerm)
    }

    fun getRightTerm(
        operationAndRightTerm: String,
        operation: OperationEnum,
    ) = operationAndRightTerm
        .removeRange(0 until operation.symbol().length)
        .ifEmpty {
            null
        }

    fun getOperationAndRightTerm(expression: String, leftParenthesisIndex: Int): String? {
        val operationAndRightTerm = if (expression.length != leftParenthesisIndex + 1) {
            expression.removeRange(0..leftParenthesisIndex)
        } else null
        return operationAndRightTerm
    }

    private fun splitInTerms(term: String): ITerm {
        val operator = getFirstOperation(term)
        return operator?.let {
            val (leftTerm, rightTerm) = term.split(operator.symbol(), limit = 2)
            transformToNonAtomicTerm(leftTerm, operator, rightTerm)
        } ?: transformToAtomicTerm(term)
    }

    fun transformToAtomicTerm(expression: String) = AtomicTerm(
        expression.replace("(", "").replace(")", "")
    )

    private fun transformToTerm(expression: String) =
        if (containsAnyOperator(expression)) {
            transformToTerms(expression)
        } else {
            transformToAtomicTerm(expression)
        }

    fun transformToNonAtomicTerm(leftTerm: String, operationEnum: OperationEnum, rightTerm: String?): NonAtomicTerm {
        return NonAtomicTerm(
            transformToTerm(leftTerm),
            operationEnum,
            rightTerm?.let {
                if (rightTerm.isEmpty()) {
                    null
                } else {
                    transformToTerm(rightTerm)
                }
            }
        )
    }
}
