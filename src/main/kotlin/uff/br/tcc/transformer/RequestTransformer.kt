package uff.br.tcc.transformer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.Node
import uff.br.tcc.dto.term.AtomicTerm
import uff.br.tcc.dto.term.ITerm
import uff.br.tcc.dto.term.NonAtomicTerm
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import java.security.InvalidParameterException

@Component
class RequestTransformer {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        const val SUB_SET_EQUALS = "inc"
    }

    fun splitToDiagrams(expression: String): List<String> {
        logger.info("Splitting expression $expression.")
        val diagrams = expression.split(SUB_SET_EQUALS)
        if (diagrams.count() != 2) {
            throw InvalidParameterException(
                "Expression $expression should have 1 inclusion in expression " +
                    "but has ${diagrams.count() - 1}."
            )
        }
        logger.info("Left expression: '${diagrams.first()}', right expression: '${diagrams.last()}'.")
        return diagrams
    }

    fun validateParenthesisCount(expression: String) {
        val openingParenthesis = expression.count {
            it == '('
        }
        val closingParenthesis = expression.count {
            it == ')'
        }
        if (openingParenthesis != closingParenthesis) {
            throw InvalidParameterException(
                "Expression '$expression' " +
                    "should have pair parenthesis, but has $openingParenthesis '(' and $closingParenthesis ')'."
            )
        }
        logger.info("Expression '$expression' has $openingParenthesis parenthesis.")
    }

    fun transformToDiagrammaticProof(expression: String): DiagrammaticProof {
        logger.info("Transforming expression $expression in a diagrammatic proof.")
        val inputNode = Node("input", NodeTypeEnum.INPUT)
        val outputNode = Node("output", NodeTypeEnum.OUTPUT)
        validateParenthesisCount(expression)
        return DiagrammaticProof(
            mutableListOf(
                Diagram(
                    nodes = mutableListOf(inputNode, outputNode),
                    edges = mutableListOf(
                        Edge(
                            leftNode = inputNode,
                            label = transformToTerms(expression.filter { !it.isWhitespace() }),
                            rightNode = outputNode
                        )
                    ),
                    stepDescription = StepDescriptionEnum.BEGIN.name
                )
            )
        ).also {
            logger.info("Diagrammatic proof = $it")
        }
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
        logger.info("Transforming $expression to terms.")
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
        logger.info("Index of left parenthesis for first parenthesis in expression $expression = $leftParenthesisIndex")
        return leftParenthesisIndex
    }

    fun transformTermInParenthesis(expression: String): ITerm {
        logger.info("Expression $expression starts with parenthesis.")
        val leftParenthesisIndex = getIndexOfLeftParenthesis(expression)
        val leftTerm = expression.take(leftParenthesisIndex).drop(1)
        logger.info("Left term = $leftTerm")
        val operationAndRightTerm = getOperationAndRightTerm(expression, leftParenthesisIndex)
        val operation = operationAndRightTerm?.let {
            getFirstOperation(operationAndRightTerm)?.also {
                logger.info("Operation ${it.name} is the first operation in $operationAndRightTerm.")
            } ?: throw InvalidParameterException(
                "There is a term $operationAndRightTerm in expression $expression" +
                    " but was expected to be an operation first."
            )
        }
        return operation?.let {
            val rightTerm = getRightTerm(operationAndRightTerm, operation)
            transformToNonAtomicTerm(leftTerm, operation, rightTerm)
        } ?: transformToTerms(leftTerm)
    }

    fun getRightTerm(operationAndRightTerm: String, operation: OperationEnum) = operationAndRightTerm
        .removeRange(0 until operation.symbol().length)
        .ifEmpty {
            null
        }?.also {
            logger.info("Right term in $operationAndRightTerm is $it.")
        }

    fun getOperationAndRightTerm(expression: String, leftParenthesisIndex: Int): String? {
        val operationAndRightTerm = if (expression.length != leftParenthesisIndex + 1) {
            expression.removeRange(0..leftParenthesisIndex)
        } else null
        logger.info("Operation and right term of expression $expression = $operationAndRightTerm.")
        return operationAndRightTerm
    }

    private fun splitInTerms(term: String): ITerm {
        logger.info("Splitting $term in one or more terms.")
        val operation = getFirstOperation(term)
        return operation?.let {
            logger.info("Operation $operation is the first operation in $term.")
            val (leftTerm, rightTerm) = term.split(operation.symbol(), limit = 2)
            transformToNonAtomicTerm(leftTerm, operation, rightTerm)
        } ?: transformToAtomicTerm(term)
    }

    fun transformToAtomicTerm(expression: String): AtomicTerm {
        logger.info(("Transforming $expression in an atomic term."))
        return AtomicTerm(
            expression.replace("(", "").replace(")", "")
        )
    }

    private fun transformToTerm(expression: String): ITerm {
        logger.info("Transforming $expression in a term.")
        return if (containsAnyOperator(expression)) {
            logger.info("$expression isn't atomic.")
            transformToTerms(expression)
        } else {
            transformToAtomicTerm(expression)
        }
    }

    fun transformToNonAtomicTerm(leftTerm: String, operationEnum: OperationEnum, rightTerm: String?): NonAtomicTerm {
        logger.info("Transforming $leftTerm, ${operationEnum.name}, $rightTerm in a non atomic term.")
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
