package uff.br.tcc.extensions

import uff.br.tcc.dto.Edge
import uff.br.tcc.dto.Node
import uff.br.tcc.dto.term.NonAtomicTerm
import uff.br.tcc.enum.NodeTypeEnum
import java.security.InvalidParameterException
import java.util.UUID

fun Edge.validateUnaryOperation() {
    label as NonAtomicTerm
    label.rightTerm?.let {
        throw InvalidParameterException(
            "Error transforming label ${label.leftTerm.name()} with operation " +
                "${label.operation.symbol()} because right term is ${label.rightTerm.name()} instead of null."
        )
    }
}

fun Edge.validateBinaryOperation() {
    label as NonAtomicTerm
    label.rightTerm ?: throw InvalidParameterException(
        "Error transforming label ${label.leftTerm.name()} " +
            "with operation ${label.operation.symbol()} because right term is null."
    )
}

fun Edge.transformInverse(): Edge {
    label as NonAtomicTerm
    validateUnaryOperation()
    return Edge(leftNode = rightNode, rightNode = leftNode, label = label.leftTerm)
}

fun Edge.transformComposition(): Triple<Edge, Edge, Node> {
    label as NonAtomicTerm
    validateBinaryOperation()
    val node = Node(name = UUID.randomUUID().toString(), NodeTypeEnum.INTERMEDIATE)
    val firstEdge = Edge(leftNode = leftNode, rightNode = node, label = label.leftTerm)
    val secondEdge = Edge(leftNode = node, rightNode = rightNode, label = label.rightTerm!!)
    return Triple(firstEdge, secondEdge, node)
}

fun Edge.transformIntersection(): Pair<Edge, Edge> {
    label as NonAtomicTerm
    validateBinaryOperation()
    val firstEdge = Edge(leftNode = leftNode, rightNode = rightNode, label = label.leftTerm)
    val secondEdge = Edge(leftNode = leftNode, rightNode = rightNode, label = label.rightTerm!!)
    return Pair(firstEdge, secondEdge)
}
