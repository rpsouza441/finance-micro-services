package br.dev.rodrigopinheiro.finances.entity.enums;

public enum TransactionType {
    DEBIT(1, "Debit"),
    CREDIT(2, "Credit"),
    TRANSFER(3, "Transfer");

    private int code;

    private TransactionType(int code, String Type) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TransactionType valueOf(int code) {
        for (TransactionType value : TransactionType.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        throw new IllegalArgumentException("Transaction Code not valid");
    }
}
