package uff.br.tcc.extensions

import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.model.term.NonAtomicTerm
import java.security.InvalidParameterException
import java.util.UUID

fun Edge.transformInverse(): Edge {
    label as NonAtomicTerm

    if (label.rightTerm != null) {
        throw InvalidParameterException(
            "Error transforming label ${label.leftTerm} with inverse operation because " +
                "right term is ${label.rightTerm} instead of null."
        )
    }

    return Edge(leftNode = rightNode, rightNode = leftNode, label = label.leftTerm)
}

fun Edge.transformComposition(): Triple<Edge, Edge, Node> {
    label as NonAtomicTerm
    val node = Node(name = UUID.randomUUID().toString(), NodeTypeEnum.INTERMEDIATE)
    val firstEdge = Edge(leftNode = leftNode, rightNode = node, label = label.leftTerm)
    val secondEdge = Edge(leftNode = node, rightNode = rightNode, label = label.rightTerm!!)
    return Triple(firstEdge, secondEdge, node)
}

fun Edge.transformIntersection(): Pair<Edge, Edge> {
    label as NonAtomicTerm
    val firstEdge = Edge(leftNode = leftNode, rightNode = rightNode, label = label.leftTerm)
    val secondEdge = Edge(leftNode = leftNode, rightNode = rightNode, label = label.rightTerm!!)
    return Pair(firstEdge, secondEdge)
}
