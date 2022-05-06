package uff.br.tcc.enum

enum class Operation {

    INTERSECTION {
        override fun symbol() = " ∩"
    },
    COMPOSITION {
        override fun symbol() = " o"
    },
    INVERSE {
        override fun symbol() = "-¹"
    };

    abstract fun symbol(): String
}
