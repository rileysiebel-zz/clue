package reasoner;

/** This is a template of the <tt>MySatSolver</tt> class that must be
 * turned in.  See <tt>RandomSearchSatSolver</tt> for sample code, and
 * see <tt>SatSolver</tt> for further explanation.
 */

import java.util.*;

public class SATSolver {
	ArrayList<Literal[]> clauses = new ArrayList<Literal[]>();
	ArrayList<Literal[]> query_clauses = new ArrayList<Literal[]>();

	// The probability of choosing a random walk
	double p = 0.5;
	private Random random = new Random();

	/** A constructor for this class.  You must include a constructor
	 * such as this one taking no arguments.  (You may have other
	 * constructors that you use for your experiments, but this is the
	 * constructor that will be used as part of the class
	 * implementation challenge.)
	 */
	public SATSolver() {
	}

	public SATSolver(SATSolver solver) {
		for(Literal[] clause : solver.clauses)
			addClause((Literal[]) clause.clone());
	}

	/** This is the method for solving satifiability problems.  Each
	 * <tt>Literal</tt> of the given <tt>cnf</tt> must include symbols
	 * indexed by integers between 0 and <tt>num_symbols</tt>-1.  The
	 * method should return a solution in the form of a boolean array
	 * of length <tt>num_symbols</tt> representing an assignment to
	 * all the symbols of the given CNF that satisfies as many clauses
	 * as possible by when time runs out on the given <tt>timer</tt>
	 * object (or very soon thereafter).
	 * @param cnf the given cnf, represented as an array of arrays of <tt>Literal</tt>s
	 * @param num_symbols the number of distinct symbols in the cnf
	 * @param timer the given timer object */
	public boolean[] solve(Literal[][] cnf, int num_symbols, Timer timer) {
		// First choose a random model
		boolean[] symbols = new boolean[num_symbols];
		for(int i = 0; i < num_symbols; i++) {
			symbols[i] = random.nextBoolean();
		}

		int clause;
		//while(timer.getTimeRemaining() > 0) {
		// If Model satisfies clauses return model
		if (isSatisfied(cnf, symbols)) return symbols;
		// Pick "c" a clause that is false in model
		clause = getRandomUnsat(cnf, symbols);
		// with Prob p flip value of random symbol in c
		if(random.nextDouble() < p) {
			// Choose the random symbol
			Literal i = cnf[clause][random.nextInt(cnf[clause].length)];
			// Flip it
			symbols[i.symbol] = !symbols[i.symbol];
		}
		// else flip whichever symbol in clause maximizes # satisfied clauses
		else {
			int i = getBestSymbolFlip(cnf, clause, symbols);
			symbols[i] = !symbols[i];
		}
		//}
		return symbols;
	}

	/** This method should simply return the "author" of this program
	 * as you would like it to appear on a class website.  You can use
	 * your real name or a pseudonym of your choice.
	 */
	public String author() {
		return new String("Riley Siebel");
	}

	/** This method should return a very brief (1-3 sentence)
	 * description of the algorithm and implementational improvements
	 * that are being used, appropriate for posting on the class
	 * website.
	 */
	public String description() {
		return new String("My implementation of WalkSAT");
	}

	public boolean Satisfies(boolean[] symbols) {
		Literal[][] ar = clauses.toArray(new Literal[0][]);
		return isSatisfied(ar, symbols);
	}

	private boolean isSatisfied(Literal[][] cnf, boolean[] symbols) {
		for(Literal[] clause : cnf) {
			boolean clause_sat = false;
			for(Literal literal : clause) {
				if(literal.sign == symbols[literal.symbol]) {
					clause_sat = true;
					break;
				}
			}
			if(!clause_sat)
				return false;
		}
		return true;
	}

	// Returns the index of a random cnf that is unsatisfied given the
	// current state of symbols.  Returns -1 if the cnf is satisfiable
	public int getRandomUnsat(Literal[][] cnf, boolean[] symbols) {
		// A list of the index of all the unsatisfied clauses
		ArrayList<Integer> unsats = new ArrayList<Integer>();

		for(int i = 0; i < cnf.length; i++) {
			boolean clause_sat = false;
			for(Literal literal : cnf[i]) {
				if(literal.sign == symbols[literal.symbol]) {
					clause_sat = true;
					break;
				}
			}
			if(!clause_sat)
				unsats.add(i);
		}
		if(unsats.size() == 0)
			return -1;
		else
			// Return a random unsatisfied clause from the list
			return unsats.get(random.nextInt(unsats.size()));
	}    

	// Returns the index of the literal in cnf[clause_index] which by flipping we increase
	// by the hightest value the number of satisfied in cnf
	// Returns -1 if something goes really wrong/
	private int getBestSymbolFlip(Literal[][] cnf, int clause_index, boolean[] symbol) {
		boolean[] temp = new boolean[symbol.length];
		System.arraycopy(symbol, 0, temp, 0, temp.length);

		int best_val = Integer.MIN_VALUE;
		// If we can't find any index (weird)
		int best_lit = -1;
		// Try flipping each symbol
		for(Literal l : cnf[clause_index]) {
			temp[l.symbol] = !symbol[l.symbol];

			int num_sat = 0;
			for(Literal[] clause : cnf) {
				for(Literal literal : clause) {
					if(literal.sign == temp[literal.symbol]) {
						num_sat++;
						break;
					}
				}
				if (num_sat > best_val) {
					best_val = num_sat;
					best_lit = l.symbol;
				}
				temp[l.symbol] = symbol[l.symbol];
			}
		}

		return best_lit;
	}

	public void addClause(Literal[] clause) {
		// Check to see if this clause is already in the knowledge base
		// TODO
		clauses.add((Literal[]) clause.clone());
		// check every pair of clauses
		//for (Literal[] c1 : clauses) {
			//for(Literal[] c2 : clauses) {
				//// Do they have a complimentary literal
				//Literal[][] resolvents = resolution(c1, c2);
				//for(Literal[] c : resolvents)
					//addClause(c);
			//}
		//}
	}

	private Literal[][] resolution(Literal[] c1, Literal[] c2) {
		ArrayList<Literal> l1 = new ArrayList<Literal>(Arrays.asList(c1));
		ArrayList<Literal> l2 = new ArrayList<Literal>(Arrays.asList(c2));
		ArrayList<Literal []> newLits = new ArrayList<Literal []>();
		for(Literal m : l1) {
			for(Literal n : l2) {
				if((m.symbol == n.symbol) && (m.sign != n.sign)) {
					Literal[] temp = resolve(l1, l2, m, n);
					if(temp.length > 0)
						newLits.add(temp);	
				}
			}
		}
		return newLits.toArray(new Literal[0][]);
	}

	private Literal[] resolve(List<Literal> l1, List<Literal> l2, Literal m, Literal n) {
		ArrayList<Literal> temp = new ArrayList<Literal>();
		for(Literal a : l1) {
			if(a.symbol != m.symbol)
				temp.add(a);
		}
		for(Literal b : l2) {
			if(b.symbol != n.symbol)
				temp.add(b);
		}
		// Check and see if this statement contains conflicting settings for the same setting
		for(int i = 0; i < temp.size(); i++) {
			for(int j = i+1; i < temp.size(); i++) {
				if(temp.get(i).symbol == temp.get(j).symbol)
					if(temp.get(i).sign != temp.get(j).sign)
						return new Literal[0];
			}
		}
		return temp.toArray(new Literal[0]);
	}
}



