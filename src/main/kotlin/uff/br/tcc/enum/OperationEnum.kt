package uff.br.tcc.enum

enum class OperationEnum {

    INTERSECTION {
        override fun symbol() = "int"
    },
    COMPOSITION {
        override fun symbol() = "comp"
    },
    INVERSE {
        override fun symbol() = "inv"
    };

    abstract fun symbol(): String
}
