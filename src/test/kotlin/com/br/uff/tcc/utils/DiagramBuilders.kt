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

fun atomicDiagram(name: String): Diagram {
    val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

    return Diagram(nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(leftNode = inputNode,
                term = AtomicTerm(name),
                rightNode = outputNode
            )),
        stepDescription = "")
}

fun normalIntersectionDiagram(leftName: String, rightName: String):Diagram {
    val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

    return Diagram(
        nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(leftNode = inputNode,
                term = AtomicTerm(leftName),
                rightNode = outputNode
            ),
            Edge(leftNode = inputNode,
                term = AtomicTerm(rightName),
                rightNode = outputNode
            )),
        stepDescription = ""
    )
}

fun buildDiagrammaticProofWithIntersection(leftName: String, rightName: String): DiagrammaticProof {
    val inputNode = Node(name = "input", nodeType = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", nodeType = NodeTypeEnum.OUTPUT)

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