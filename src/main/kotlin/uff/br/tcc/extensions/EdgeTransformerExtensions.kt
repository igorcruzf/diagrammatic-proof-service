package uff.br.tcc.extensions

import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.model.term.NonAtomicTerm
import java.util.UUID

fun Edge.transformInverse(): Edge {
    term as NonAtomicTerm
    return Edge(leftNode = rightNode, rightNode = leftNode, term = term.leftTerm)
}

fun Edge.transformComposition(): Triple<Edge, Edge, Node> {
    term as NonAtomicTerm
    val node = Node(name = UUID.randomUUID().toString(), NodeTypeEnum.INTERMEDIATE)
    val firstEdge = Edge(leftNode = leftNode, rightNode = node, term = term.leftTerm)
    val secondEdge = Edge(leftNode = node, rightNode = rightNode, term = term.rightTerm!!)
    return Triple(firstEdge, secondEdge, node)
}

fun Edge.transformIntersection(): Pair<Edge, Edge> {
    term as NonAtomicTerm
    val firstEdge = Edge(leftNode = leftNode, rightNode = rightNode, term = term.leftTerm)
    val secondEdge = Edge(leftNode = leftNode, rightNode = rightNode, term = term.rightTerm!!)
    return Pair(firstEdge, secondEdge)
}
