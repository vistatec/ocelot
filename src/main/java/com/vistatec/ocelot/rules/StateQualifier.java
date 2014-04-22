package com.vistatec.ocelot.rules;

public enum StateQualifier {
    ID("id-match"),
    EXACT("exact-match"),
    FUZZY("fuzzy-match"),
    MT("mt-suggestion");

    private String name;

    private StateQualifier(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static StateQualifier get(String name) {
        for (StateQualifier sq : values()) {
            if (sq.getName().equals(name)) {
                return sq;
            }
        }
        return null;
    }
}