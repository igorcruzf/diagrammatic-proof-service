package uff.br.tcc.enum

enum class OperationEnum {

    INTERSECTION {
        override fun symbol() = "\\cap"
    },
    COMPOSITION {
        override fun symbol() = "\\circ"
    },
    INVERSE {
        override fun symbol() = "\\inv"
    };

    abstract fun symbol(): String
}
