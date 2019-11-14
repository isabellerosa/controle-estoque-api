package rosa.isabelle.inventorycontrol.utils;

import java.security.SecureRandom;

public final class IdBuilder {
    private int idLength;
    private char[] validCharacters;

    public static final char[] NUMBERS_ONLY = "0123456789".toCharArray();
    public static final char[] ALPHABET_NO_CAPS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] ALPHABET_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public IdBuilder() {
        this.validCharacters = NUMBERS_ONLY;
        this.idLength = 8;
    }

    public String build(){
        SecureRandom random = new SecureRandom();
        char[] id = new char[idLength];

        for(int i = 0; i < id.length; i++)
            id[i] = validCharacters[random.nextInt(validCharacters.length)];

        return String.valueOf(id);
    }

    public IdBuilder setIdLength(int idLength) {
        this.idLength = idLength;
        return this;
    }

    public IdBuilder setValidCharacters(char[] validCharacters) {
        this.validCharacters = validCharacters;
        return this;
    }

    public IdBuilder appendValidCharacters(char[] validCharacters){
        this.validCharacters = (String.valueOf(this.validCharacters) +
                String.valueOf(validCharacters)).toCharArray();
        return this;
    }
}