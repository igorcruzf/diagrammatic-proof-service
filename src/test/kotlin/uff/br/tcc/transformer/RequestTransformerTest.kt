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
                "\\capA", OperationEnum.INTERSECTION
            )
        )
        assertEquals(
            "(A\\capB)",
            requestTransformer.getRightTerm(
                "\\cap(A\\capB)", OperationEnum.INTERSECTION
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "\\cap", OperationEnum.INTERSECTION
            )
        )
    }

    @Test
    fun `should get term in inverse plus term`() {
        assertEquals(
            "A",
            requestTransformer.getRightTerm(
                "\\invA", OperationEnum.INVERSE
            )
        )
        assertEquals(
            "(A\\invB)",
            requestTransformer.getRightTerm(
                "\\inv(A\\invB)", OperationEnum.INVERSE
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "\\inv", OperationEnum.INVERSE
            )
        )
    }

    @Test
    fun `should get term in composition plus term`() {
        assertEquals(
            "A",
            requestTransformer.getRightTerm(
                "\\circA", OperationEnum.COMPOSITION
            )
        )
        assertEquals(
            "(A\\circB)",
            requestTransformer.getRightTerm(
                "\\circ(A\\circB)", OperationEnum.COMPOSITION
            )
        )
        assertNull(
            requestTransformer.getRightTerm(
                "\\circ", OperationEnum.COMPOSITION
            )
        )
    }

    @Test
    fun `should get operation and term in expression after left parenthesis`() {
        val expression = "(A\\capB)\\circC"
        assertEquals(
            "\\circC",
            requestTransformer.getOperationAndRightTerm(expression, 7)
        )
    }

    @Test
    fun `should get operation in expression after left parenthesis`() {
        val expression = "(A\\capB)\\inv"
        assertEquals(
            "\\inv",
            requestTransformer.getOperationAndRightTerm(expression, 7)
        )
    }

    @Test
    fun `should return null in get operation and right term with nothing after left parenthesis`() {
        val expression = "(A\\capB)"
        assertNull(requestTransformer.getOperationAndRightTerm(expression, 7))
    }

    @Test
    fun `should transform to term with intersection inside parenthesis`() {
        val expression = "(A\\capB)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INTERSECTION,
            rightTerm = AtomicTerm("B")
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with inverse inside parenthesis`() {
        val expression = "(A\\inv)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.INVERSE,
            rightTerm = null
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with composition inside parenthesis`() {
        val expression = "(A\\circB)"
        val term = NonAtomicTerm(
            leftTerm = AtomicTerm("A"),
            operation = OperationEnum.COMPOSITION,
            rightTerm = AtomicTerm("B")
        )
        assertEquals(term, requestTransformer.transformTermInParenthesis(expression))
    }

    @Test
    fun `should transform to term with non atomics terms inside parenthesis`() {
        val expression = "((A\\circB)\\capC)\\inv"
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
        val expression = "A\\cap(((A\\circB)\\capC)\\inv)"
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
        val expression = "A\\circB\\capC\\inv"
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
        val expression = "A\\cap(((A\\circB)\\capC)\\inv)"
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

        assertEquals(term, diagrammaticProof.diagrams.first().edges.first().term)
        assertEquals(NodeTypeEnum.INPUT, diagrammaticProof.diagrams.first().edges.first().leftNode.type)
        assertEquals(NodeTypeEnum.OUTPUT, diagrammaticProof.diagrams.first().edges.first().rightNode.type)
        assertEquals(StepDescriptionEnum.BEGIN.name, diagrammaticProof.diagrams.first().stepDescription)
        assertEquals(1, diagrammaticProof.diagrams.count())
        assertEquals(1, diagrammaticProof.diagrams.first().edges.count())
    }

    @Test
    fun `should split into two expressions`() {
        val oneExpression = "A\\cap(((A\\circB)\\capC)\\inv)"
        val expression = "A\\cap(((A\\circB)\\capC)\\inv)\\subseteqA\\cap(((A\\circB)\\capC)\\inv)"
        val splattedExpressions = requestTransformer.splitToDiagrams(expression)
        assertEquals(oneExpression, splattedExpressions.first())
        assertEquals(oneExpression, splattedExpressions.last())
    }

    @Test
    fun `should throw IllegalArgumentException for having one expression`() {
        val oneExpression = "A\\cap(((A\\circB)\\capC)\\inv)"

        assertThrows<IllegalArgumentException> {
            requestTransformer.splitToDiagrams(oneExpression)
        }
    }

    @Test
    fun `should throw IllegalArgumentException for having more than two expressions`() {
        val expression = "A\\cap(((A\\circB)\\capC)\\inv)\\subseteqA\\cap(((A\\circB)\\capC)\\inv)\\subseteqA\\cap(((A\\circB)\\capC)\\inv)"
        assertThrows<IllegalArgumentException> {
            requestTransformer.splitToDiagrams(expression)
        }
    }

    private fun buildIntersectionExpressions(): List<String> {
        val expression = "A\\capB\\invC\\circc"
        val anotherExpression = "cap\\capcap"
        val anotherExpression2 = "inv\\capcirc"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildInverseExpressions(): List<String> {
        val expression = "A\\inv\\capc\\circc"
        val anotherExpression = "inv\\invinv"
        val anotherExpression2 = "cap\\invcirc"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildCompositionExpressions(): List<String> {
        val expression = "A\\circ\\inv\\capc"
        val anotherExpression = "circ\\circcirc"
        val anotherExpression2 = "cap\\circinv"
        return listOf(expression, anotherExpression, anotherExpression2)
    }

    private fun buildEmptyExpressions(): List<String> {
        return listOf(
            "A\\cir\\iv\\cac",
            "capinvcirc",
            "",
            "A",
            "banana"
        )
    }

    private fun buildEmptyExpressionsWithParenthesis(): List<String> {
        return listOf(
            "(A\\cir\\iv\\cac)",
            "(capinvcirc)",
            "()",
            "A()",
            "(A)",
            "(banana"

        )
    }
}
