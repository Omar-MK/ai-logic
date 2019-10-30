import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.sat4j.core.VecInt;
import org.sat4j.pb.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * This solver class implements the logicng and sat4j libraries to allow
 * propositional formulas to be tested for satisfiability.
 * to use, a solver object is created by passing a propositional formula to the
 * constructor, and the isSatisfiable method is used to check if the formula is
 * satisfiable
 */
public class Solver
{
	/**
	 * HashMap used to translate literals into DIMACS form.
	 */
	private HashMap<String, Integer> literalDict = new HashMap<>();

	/**
	 * Formula Object containing cnf equation.
	 */
	private final Formula cnf;

	public Solver(String propFormula) throws ParserException
	{
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		// parse the prop formula
		final Formula formula = p.parse(propFormula);
		// convert to cnf and set cnf field
		cnf = formula.cnf();
	}

	public boolean isSatisfiable() throws ContradictionException, TimeoutException
	{
		// could either use getMaxVar method written in this class or just use cnf.numberOfAtoms since the nubmer of literals in a clause cannot be bigger than the number of atomic formulas as you cannot have A and ~A in the same clause. getMaxVar is costly to use.

		// set max number of variables
		final int MAXVAR = (int) cnf.numberOfAtoms();
		// set number of clauses as numberOfOperands
		final int NBCLAUSES = cnf.numberOfOperands();
		// convert literals to DIMACS
		fillLiteralDict();
		// create a solver
		ISolver solver = SolverFactory.newDefault();
		// prepare solver to accept MAXVAR variables (over-estimate) and set number of expected clauses
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
		feedSolver(solver);
		// now check for satisfiability
		IProblem problem = solver;
		if (problem.isSatisfiable())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// loads arrays of clauses in DIMACS form into the solver
	public void feedSolver(ISolver solver) throws ContradictionException
	{
		Iterator<Formula> iterator = cnf.iterator();
		while (iterator.hasNext())
		{
			Formula clause = iterator.next();
			int[] literalArr = new int[clause.literals().size()];
			int i = 0;
			for (Literal literal : clause.literals())
			{
				literalArr[i] = literalDict.get(literal.toString());
				i++;
			}
			// adapt Array to IVecInt
			solver.addClause(new VecInt(literalArr));
		}
	}

	// fills the literalDict with literals and their corresponding values
	private void fillLiteralDict()
	{
		Iterator<Literal> iterator = cnf.literals().iterator();
		int value = 0;
		while (iterator.hasNext())
		{
			String literal = iterator.next().toString();
			// does the dict contain A given literal = ~A
			if(literalDict.containsKey(literal.substring(1)))
			{
				// if it does, then give it a negative value
				literalDict.put(literal, -value);
			}
			// the dict does not contain A, but the value starts with ~
			else if (literal.startsWith("~"))
			{
				// increment the value (its a new literal)
				value++;
				// store the literal as the negative of the new value
				literalDict.put(literal, -value);
			}
			// else given a positive literal
			else
			{
				value++;
				literalDict.put(literal, value);
			}
		}
	}

	// returns the size of the largest clause in cnf
	private int getMaxVar()
	{
		// iterate through clauses
		Iterator<Formula> iterator = cnf.iterator();
		int maxVar = 0;
		while (iterator.hasNext())
		{
			Formula clause = iterator.next();
			// retrieve a literal array in a formula
			if (clause.literals().size() > maxVar)
				maxVar = clause.literals().size();
		}
		return maxVar;
	}
}
