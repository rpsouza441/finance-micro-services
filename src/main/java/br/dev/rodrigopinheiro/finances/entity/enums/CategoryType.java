package br.dev.rodrigopinheiro.finances.entity.enums;

public enum CategoryType {
    STATEMENT_CLOSED(1, "Statement Closed"), TRANSFER(2, "Transfer");


    private int code;

    private CategoryType(int code, String Type) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CategoryType valueOf(int code) {
        for (CategoryType value : CategoryType.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        throw new IllegalArgumentException("Category Code not valid");
    }
}
