package uff.br.tcc.utils

import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.AtomicTerm
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.Edge
import uff.br.tcc.model.ITerm
import uff.br.tcc.model.Node
import uff.br.tcc.model.Term

fun atomicDiagram(name: String): Diagram {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return Diagram(
        nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(
                leftNode = inputNode,
                term = AtomicTerm(name),
                rightNode = outputNode
            )
        ),
        stepDescription = ""
    )
}

fun normalIntersectionDiagram(leftName: String, rightName: String): Diagram {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return Diagram(
        nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(
                leftNode = inputNode,
                term = AtomicTerm(leftName),
                rightNode = outputNode
            ),
            Edge(
                leftNode = inputNode,
                term = AtomicTerm(rightName),
                rightNode = outputNode
            )
        ),
        stepDescription = ""
    )
}

fun buildDiagrammaticProofWithIntersection(leftName: String, rightName: String): DiagrammaticProof {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdgeWithIntersection(inputNode, outputNode, leftName, rightName)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            )
        )
    )
}

fun buildDiagrammaticProof2(): DiagrammaticProof {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)
    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdge2(inputNode, outputNode)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            )
        )
    )
}

fun buildDiagrammaticProof(): DiagrammaticProof {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)
    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdge(inputNode, outputNode)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            )
        )
    )
}

fun buildEdgeWithIntersection(inputNode: Node, outputNode: Node, leftName: String, rightName: String) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = Term(leftTerm = AtomicTerm(leftName), operation = OperationEnum.INTERSECTION, rightTerm = AtomicTerm(rightName))
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
    val leftTerm = buildTerm(rightTerm = null, operationEnum = OperationEnum.INVERSE)
    val rightTerm = buildTerm(operationEnum = OperationEnum.COMPOSITION)
    return buildTerm(leftTerm = leftTerm, rightTerm = rightTerm)
}

fun buildTerm(
    leftTerm: ITerm = AtomicTerm("A"),
    operationEnum: OperationEnum = OperationEnum.INTERSECTION,
    rightTerm: ITerm? = AtomicTerm("B"),
) = Term(
    leftTerm = leftTerm,
    operation = operationEnum,
    rightTerm = rightTerm
)
