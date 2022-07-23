package uff.br.tcc.dto.term

data class AtomicTerm(val name: String) : ITerm {
    override fun name() = name

    override fun toString(): String {
        return name
    }
}
