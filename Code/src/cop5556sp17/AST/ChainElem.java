package cop5556sp17.AST;

import cop5556sp17.Scanner;
import cop5556sp17.Scanner.Token;

public abstract class ChainElem extends Chain {

    public Scanner.Kind kind;

    public ChainElem(Token firstToken) {
        super(firstToken);
    }

    public Scanner.Kind getKind() {
        return kind;
    }

    public void setKind(Scanner.Kind kind) {
        this.kind = kind;
    }
}
