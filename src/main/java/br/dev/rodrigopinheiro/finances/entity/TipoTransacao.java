package br.dev.rodrigopinheiro.model;

public enum TipoTransacao {
    DEBIT(1, "Débito"),
    CREDIT(2, "Crédito");

    private int codigo;

    private TipoTransacao(int codigo, String tipo) {
        this.codigo = codigo;
    }

    public int getCodigo(){
        return codigo;
    }

    public static TipoTransacao valueOf(int codigo){
        for (TipoTransacao valor : TipoTransacao.values()){
            if (codigo == valor.getCodigo()){
                return valor;
            }
        }
        throw  new IllegalArgumentException("Codigo de Tipo Transacao invalido");
    }
}
