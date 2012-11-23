package solver;

/** An object of this class represents a literal (signed symbol) */
public class Literal {

    /** constructor for creating a literal with the given sign
     *  and symbol index
     */
    public Literal(int symbol, boolean sign) {
	this.symbol = symbol;
	this.sign = sign;
    }

    /** sign of the literal: true = unnegated, false = negated */
    public boolean sign;

    /** index of proposition symbol */
    public int symbol;
}
