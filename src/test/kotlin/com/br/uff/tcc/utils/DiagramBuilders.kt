package com.br.uff.tcc.utils

import com.br.uff.tcc.enum.NodeTypeEnum
import com.br.uff.tcc.enum.Operation
import com.br.uff.tcc.enum.StepDescriptionEnum
import com.br.uff.tcc.model.AtomicTerm
import com.br.uff.tcc.model.Diagram
import com.br.uff.tcc.model.DiagrammaticProof
import com.br.uff.tcc.model.Edge
import com.br.uff.tcc.model.ITerm
import com.br.uff.tcc.model.Node
import com.br.uff.tcc.model.Term

val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

fun atomicDiagram(name: String) = Diagram(nodes = mutableListOf(inputNode, outputNode),
    edges = mutableListOf(
        atomicEdge("R")),
    stepDescription = "")

fun normalIntersectionDiagram(leftName: String, rightName: String) = Diagram(
    nodes = mutableListOf(inputNode, outputNode),
    edges = mutableListOf(
        atomicEdge(leftName), atomicEdge(rightName)),
    stepDescription = ""
)

fun atomicEdge(name: String) =
    Edge(leftNode = inputNode, term = AtomicTerm(name), rightNode = outputNode)

fun buildDiagrammaticProofWithIntersection(leftName: String, rightName: String): DiagrammaticProof {

    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdgeWithIntersection(inputNode, outputNode, leftName, rightName)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            ))
    )
}

fun buildDiagrammaticProof2(): DiagrammaticProof {
    val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)
    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdge2(inputNode, outputNode)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            ))
    )
}


fun buildDiagrammaticProof(): DiagrammaticProof {
    val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)
    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdge(inputNode, outputNode)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            ))
    )
}

fun buildEdgeWithIntersection(inputNode: Node, outputNode: Node, leftName: String, rightName: String) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = Term(leftTerm = AtomicTerm(leftName), operation = Operation.INTERSECTION, rightTerm = AtomicTerm(rightName))
)

fun buildEdge2(inputNode: Node, outputNode: Node) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = AtomicTerm("R")
)

fun buildEdge(inputNode: Node, outputNode: Node) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = buildTermWithTwoNonAtomicsTerms()
)

fun buildTermWithTwoNonAtomicsTerms(): Term {
    val leftTerm = buildTerm(rightTerm = null, operation = Operation.INVERSE)
    val rightTerm = buildTerm(operation = Operation.COMPOSITION)
    return buildTerm(leftTerm = leftTerm, rightTerm = rightTerm)
}

fun buildTerm(
    leftTerm: ITerm = AtomicTerm("A"),
    operation: Operation = Operation.INTERSECTION,
    rightTerm: ITerm? = AtomicTerm("B"),
) = Term(
    leftTerm = leftTerm,
    operation = operation,
    rightTerm = rightTerm
)