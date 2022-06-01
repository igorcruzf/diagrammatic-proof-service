package uff.br.tcc.transformer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.term.AtomicTerm
import uff.br.tcc.model.term.NonAtomicTerm

class RequestTransformerTest {

    private val requestTransformer = RequestTransformer()

    @Test
    fun `should get intersection operation in string`() {
        val expressionList = buildIntersectionExpressions()
        expressionList.forEach {
            assertEquals(OperationEnum.INTERSECTION, requestTransformer.getFirstOperation(it))
        }
    }

    @Test
    fun `should get inverse operation in string`() {
        val expressionList = buildInverseExpressions()
        expressionList.forEach {
            assertEquals(OperationEnum.INVERSE, requestTransformer.getFirstOperation(it))
        }
    }

    @Test
    fun `should get composition operation in string`() {
        val expressionList = buildCompositionExpressions()
        expressionList.forEach {
            assertEquals(OperationEnum.COMPOSITION, requestTransformer.getFirstOperation(it))
        }
    }

    @Test
    fun `should not get an operation in string`() {
        val expressionList = buildEmptyExpressions()
        expressionList.forEach {
            assertNull(requestTransformer.getFirstOperation(it))
        }
    }

    @Test
    fun `should return true in contains any operator in string`() {
        val expressions = buildIntersectionExpressions()
            .plus(buildCompositionExpressions())
            .plus(buildInverseExpressions())
        expressions.forEach {
            assertTrue(requestTransformer.containsAnyOperator(it))
        }
    }

    @Test
    fun `should return false in contains any operator in string`() {
        val expressions = buildEmptyExpressions()
        expressions.forEach {
            assertFalse(requestTransformer.containsAnyOperator(it))
        }
    }

    @Test
    fun `should transform expression to atomic term`() {
        val expressions = buildEmptyExpressions()
        expressions.forEach {
            assertEquals(AtomicTerm(it), requestTransformer.transformToAtomicTerm(it))
        }
        val expressionsWithParenthesis = buildEmptyExpressionsWithParenthesis()
        expressionsWithParenthesis.forEach {
            val atomicTerm = requestTransformer.transformToAtomicTerm(it)
            assertNotEquals(AtomicTerm(it), atomicTerm)
            assertTrue(it.contains(atomicTerm.name))
        }
    }

    @Test
    fun `should get term in intersection plus term`() {
        assertEquals(
            "A",
            requestTransformer.getRightTerm(
                "intA", OperationEnum.INTERSECTION
            )
        )
        assertEquals(
            "(AintB)",
            requestTransformer.getRightTerm(
                "int(AintB)", OperationEnum.INTERSECTION
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "int", OperationEnum.INTERSECTION
            )
        )
    }

    @Test
    fun `should get term in inverse plus term`() {
        assertEquals(
            "A",
            requestTransformer.getRightTerm(
                "invA", OperationEnum.INVERSE
            )
        )
        assertEquals(
            "(AinvB)",
            requestTransformer.getRightTerm(
                "inv(AinvB)", OperationEnum.INVERSE
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "inv", OperationEnum.INVERSE
            )
        )
    }

    @Test
    fun `should get term in composition plus term`() {
        assertEquals(
            "A",
            requestTransformer.getRightTerm(
                "compA", OperationEnum.COMPOSITION
            )
        )
        assertEquals(
            "(AcompB)",
            requestTransformer.getRightTerm(
                "comp(AcompB)", OperationEnum.COMPOSITION
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "comp", OperationEnum.COMPOSITION
            )
        )
    }

    @Test
    fun `should get operation and term in expression after left parenthesis`() {
        val expression = "(AintB)compC"
        assertEquals(
            "compC",
            requestTransformer.getOperationAndRightTerm(expression, expression.length - 6)
        )
    }

    @Test
    fun `should get operation in expression after left parenthesis`() {
        val expression = "(AintB)inv"
        assertEquals(
            "inv",
            requestTransformer.getOperationAndRightTerm(expression, expression.length - 4)
        )
    }

    @Test
    fun `should return null in get operation and right term with nothing after left parenthesis`() {
        val expression = "(AintB)"
        assertNull(requestTransformer.getOperationAndRightTerm(expression, expression.length - 1))
    }

    @Test
    fun `should transform to term with intersection inside parenthesis`() {
        val expression = "(AintB)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INTERSECTION,
            rightTerm = AtomicTerm("B")
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with inverse inside parenthesis`() {
        val expression = "(Ainv)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INVERSE,
            rightTerm = null
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with composition inside parenthesis`() {
        val expression = "(AcompB)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.COMPOSITION,
            rightTerm = AtomicTerm("B")
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with non atomics terms inside parenthesis`() {
        val expression = "((AcompB)intC)inv"
        val term = NonAtomicTerm(
            leftTerm = NonAtomicTerm(
                leftTerm = NonAtomicTerm(
                    leftTerm = AtomicTerm("A"),
                    operation = OperationEnum.COMPOSITION,
                    rightTerm = AtomicTerm("B")
                ),
                operation = OperationEnum.INTERSECTION,
                rightTerm = AtomicTerm("C")
            ),
            operation = OperationEnum.INVERSE,
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with non atomics terms with parenthesis`() {
        val expression = "Aint(((AcompB)intC)inv)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INTERSECTION,
            rightTerm = NonAtomicTerm(
                leftTerm = NonAtomicTerm(
                    leftTerm = NonAtomicTerm(
                        leftTerm = AtomicTerm("A"),
                        operation = OperationEnum.COMPOSITION,
                        rightTerm = AtomicTerm("B")
                    ),
                    operation = OperationEnum.INTERSECTION,
                    rightTerm = AtomicTerm("C")
                ),
                operation = OperationEnum.INVERSE
            )
        )
        assertEquals(term, requestTransformer.transformToTerms(expression))
    }

    @Test
    fun `should transform to term with non atomics terms without parenthesis`() {
        val expression = "AcompBintCinv"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.COMPOSITION,
            rightTerm = NonAtomicTerm(
                leftTerm = AtomicTerm("B"),
                operation = OperationEnum.INTERSECTION,
                rightTerm = NonAtomicTerm(
                    leftTerm = AtomicTerm("C"),
                    operation = OperationEnum.INVERSE
                )
            )
        )
        assertEquals(term, requestTransformer.transformToTerms(expression))
    }

    @Test
    fun `should transform to diagrammatic proof`() {
        val expression = "Aint(((AcompB)intC)inv)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INTERSECTION,
            rightTerm = NonAtomicTerm(
                leftTerm = NonAtomicTerm(
                    leftTerm = NonAtomicTerm(
                        leftTerm = AtomicTerm("A"),
                        operation = OperationEnum.COMPOSITION,
                        rightTerm = AtomicTerm("B")
                    ),
                    operation = OperationEnum.INTERSECTION,
                    rightTerm = AtomicTerm("C")
                ),
                operation = OperationEnum.INVERSE
            )
        )
        val diagrammaticProof = requestTransformer.transformToDiagrammaticProof(expression)

        assertEquals(term, diagrammaticProof.diagrams.first().edges.first().label)
        assertEquals(NodeTypeEnum.INPUT, diagrammaticProof.diagrams.first().edges.first().leftNode.type)
        assertEquals(NodeTypeEnum.OUTPUT, diagrammaticProof.diagrams.first().edges.first().rightNode.type)
        assertEquals(StepDescriptionEnum.BEGIN.name, diagrammaticProof.diagrams.first().stepDescription)
        assertEquals(1, diagrammaticProof.diagrams.count())
        assertEquals(1, diagrammaticProof.diagrams.first().edges.count())
    }

    @Test
    fun `should split into two expressions`() {
        val oneExpression = "Aint(((AcompB)intC)inv)"
        val expression = "Aint(((AcompB)intC)inv)incAint(((AcompB)intC)inv)"
        val splattedExpressions = requestTransformer.splitToDiagrams(expression)
        assertEquals(oneExpression, splattedExpressions.first())
        assertEquals(oneExpression, splattedExpressions.last())
    }

    @Test
    fun `should throw IllegalArgumentException for having one expression`() {
        val oneExpression = "Aint(((AcompB)intC)inv)"

        assertThrows<IllegalArgumentException> {
            requestTransformer.splitToDiagrams(oneExpression)
        }
    }

    @Test
    fun `should throw IllegalArgumentException for having more than two expressions`() {
        val expression = "Aint(((AcompB)intC)inv)incAint(((AcompB)intC)inv)incAint(((AcompB)intC)inv)"
        assertThrows<IllegalArgumentException> {
            requestTransformer.splitToDiagrams(expression)
        }
    }

    private fun buildIntersectionExpressions(): List<String> {
        val expression = "AintBinvCcompc"
        val anotherExpression = "inintin"
        val anotherExpression2 = "inaint"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildInverseExpressions(): List<String> {
        val expression = "Ainvintccompc"
        val anotherExpression = "invinvinv"
        val anotherExpression2 = "capinvcirc"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildCompositionExpressions(): List<String> {
        val expression = "Acompinvintc"
        val anotherExpression = "circcompcirc"
        val anotherExpression2 = "capcompinv"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildEmptyExpressions(): List<String> {
        return listOf(
            "Ainavinatac",
            "capinsvcirc",
            "",
            "A",
            "banana"
        )
    }

    private fun buildEmptyExpressionsWithParenthesis(): List<String> {
        return listOf(
            "(Ainavinatac)",
            "(capinsvcirc)",
            "()",
            "A()",
            "(A)",
            "(banana"

        )
    }
}
