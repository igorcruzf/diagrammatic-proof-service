package uff.br.tcc.utils

import uff.br.tcc.enum.NodeTypeEnum
import uff.br.tcc.enum.OperationEnum
import uff.br.tcc.enum.StepDescriptionEnum
import uff.br.tcc.model.Diagram
import uff.br.tcc.model.DiagrammaticProof
import uff.br.tcc.model.Edge
import uff.br.tcc.model.Node
import uff.br.tcc.model.term.AtomicTerm
import uff.br.tcc.model.term.NonAtomicTerm

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

fun normalInverseDiagram(name: String): Diagram {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return Diagram(
        nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(
                leftNode = outputNode,
                term = AtomicTerm(name),
                rightNode = inputNode
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

fun normalCompositionDiagram(leftName: String, rightName: String): Diagram {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val intermediateNode = Node("a", type = NodeTypeEnum.INTERMEDIATE)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return Diagram(
        nodes = mutableListOf(inputNode, outputNode),
        edges = mutableListOf(
            Edge(
                leftNode = inputNode,
                term = AtomicTerm(leftName),
                rightNode = intermediateNode
            ),
            Edge(
                leftNode = intermediateNode,
                term = AtomicTerm(rightName),
                rightNode = outputNode
            )
        ),
        stepDescription = ""
    )
}

fun buildDiagrammaticProofWithInverse(name: String): DiagrammaticProof {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdgeWithInverse(inputNode, outputNode, name)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            )
        )
    )
}

fun buildDiagrammaticProofWithComposition(leftName: String, rightName: String): DiagrammaticProof {
    val inputNode = Node(name = "input", type = NodeTypeEnum.INPUT)
    val outputNode = Node(name = "output", type = NodeTypeEnum.OUTPUT)

    return DiagrammaticProof(
        diagrams = mutableListOf(
            Diagram(
                nodes = mutableListOf(inputNode, outputNode),
                edges = mutableListOf(
                    buildEdgeWithComposition(inputNode, outputNode, leftName, rightName)
                ),
                stepDescription = StepDescriptionEnum.BEGIN.name
            )
        )
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

fun buildEdgeWithInverse(inputNode: Node, outputNode: Node, name: String) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = NonAtomicTerm(leftTerm = AtomicTerm(name), operation = OperationEnum.INVERSE)
)

fun buildEdgeWithComposition(inputNode: Node, outputNode: Node, leftName: String, rightName: String) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = NonAtomicTerm(leftTerm = AtomicTerm(leftName), operation = OperationEnum.COMPOSITION, rightTerm = AtomicTerm(rightName))
)

fun buildEdgeWithIntersection(inputNode: Node, outputNode: Node, leftName: String, rightName: String) = Edge(
    leftNode = inputNode,
    rightNode = outputNode,
    term = NonAtomicTerm(leftTerm = AtomicTerm(leftName), operation = OperationEnum.INTERSECTION, rightTerm = AtomicTerm(rightName))
)
