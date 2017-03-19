package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {

    public Dec dec;

    public IdentLValue(Token firstToken) {
        super(firstToken);
    }

    public Dec getDec() {
        return dec;
    }

    public void setDec(Dec dec) {
        this.dec = dec;
    }

    @Override
    public String toString() {
        return "IdentLValue [firstToken=" + firstToken + "]";
    }

    @Override
    public Object visit(ASTVisitor v, Object arg) throws Exception {
        return v.visitIdentLValue(this, arg);
    }

    public String getText() {
        return firstToken.getText();
    }

}
