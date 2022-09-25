package uff.br.tcc.transformer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uff.br.tcc.dto.Diagram
import uff.br.tcc.dto.DiagrammaticProof
import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.INPUT_NODE_NAME
import uff.br.tcc.dto.Node
import uff.br.tcc.dto.OUTPUT_NODE_NAME
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

    fun transformToDiagrammaticProof(expression: String): DiagrammaticProof {
        validateParenthesisCount(expression)
        logger.info("Transforming expression $expression in a diagrammatic proof.")
        val inputNode = Node(INPUT_NODE_NAME, NodeTypeEnum.INPUT)
        val outputNode = Node(OUTPUT_NODE_NAME, NodeTypeEnum.OUTPUT)
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

    private fun validateParenthesisCount(expression: String) {
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

    private fun getIndexOfClosingParenthesis(expression: String): Int {
        var openingParenthesisCount = 1
        var closingParenthesisIndex = 1
        expression.drop(1).takeWhile {
            if (it == '(') {
                openingParenthesisCount += 1
            }
            if (it == ')') {
                openingParenthesisCount -= 1
            }
            if (openingParenthesisCount != 0) {
                closingParenthesisIndex += 1
            }
            openingParenthesisCount != 0
        }
        logger.info(
            "Index of closing parenthesis for first opening parenthesis in expression $expression = " +
                "$closingParenthesisIndex"
        )
        return closingParenthesisIndex
    }

    fun transformTermInParenthesis(expression: String): ITerm {
        logger.info("Expression $expression starts with parenthesis.")
        val closingParenthesisIndex = getIndexOfClosingParenthesis(expression)
        val leftTerm = expression.take(closingParenthesisIndex).drop(1)
        logger.info("Left term = $leftTerm")
        val operationAndRightTerm = getOperationAndRightTerm(expression, closingParenthesisIndex)
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

    fun getOperationAndRightTerm(expression: String, closingParenthesisIndex: Int): String? {
        val operationAndRightTerm = if (expression.length != closingParenthesisIndex + 1) {
            expression.removeRange(0..closingParenthesisIndex)
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

    private fun transformToNonAtomicTerm(
        leftTerm: String,
        operationEnum: OperationEnum,
        rightTerm: String?
    ): NonAtomicTerm {
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

    companion object {
        const val SUB_SET_EQUALS = "inc"
    }
}
