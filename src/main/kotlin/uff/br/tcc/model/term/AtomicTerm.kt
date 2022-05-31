package uff.br.tcc.model.term

data class AtomicTerm(val name: String) : ITerm {
    override fun name() = name
}
