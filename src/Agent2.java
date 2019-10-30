import java.util.ArrayList;
import java.util.Arrays;

/**
 * An agent class that contains the code for an agent that can play the
 * Gold and Daggers game using logical reasoning and the single point strategy.
 * This agent will resort to random probing if it cannot solve the current state
 * of the game using the afformentioned strategies.
 */
public class Agent2 extends Agent1
{

	/**
	 * Field used to record the number of satisfiability tests carried out.
	 */
	private int satTestCount = 0;

	/**
	 * Field used to record the number of satisfiability tests that lead to
	 * flagged cells.
	 */
	private int satTestCompletions = 0;

	/**
	 * Constructor for Agent2.
	 * @param g a Game Object of a Daggers and Golds game instance.
	 */
	public Agent2(Game g)
	{
		super(g);
	}

	/**
	 * Method used to initate the agent to begin solving the D&G world.
	 */
	@Override
	public void start()
	{
		// first step is to probe 0,0
		targetProbe(coveredCells.get(0));
		while (g.getGameState() == 2)
		{
			// use single point strategy until it can no longer make any changes
			spsProbe();
			// this if statement checks the game state as determined by the game class. I.e. once all covered safe cells have been uncovered 2 will change. This also takes into account the case where the number of covered cells is the same as the number of daggers remaining.
			if (g.getGameState() != 2) break;
			// if the number of stoned cells is the same as the number of daggers in the map, then the game must be complete, all that is left is to probe the reamining covered cells.
			else if (daggerCount == 0)
			{
				probeAllRemaining();
				break;
			}
			// use solver to probe a cell, if a cell is successfully probed, the logicProbe method returns true, and we can return to sps. If false is returned, the only solution left is to use a random probe
			if (!logicProbe())
			{
				randomProbe();
				randomProbes++;
			}
		}

		if (g.getGameState() == 1)
		{
			System.out.println("*** Game Won! ***");
		}
		else
		{
			System.out.println("*** Game Lost! ***");
		}
		System.out.println("Random Probes = " + randomProbes);
		System.out.println("SPS Cycles = " + spsIterations);
		System.out.println("Sat Tests = " + satTestCount);
		System.out.println("Sat Test Solutions = " + satTestCompletions);
	}

	/**
	 * Method contains logic probe algorithm that performs the multi-cell
	 * analysis strategy.
	 * Uses the sat4j solver to determine if a cell in the knowledge-base
	 * contains a dagger, and if so, flags the cell in question.
	 *
	 * @return true if any cell within the knowledge-base is proven to contain
	 * a dagger and false otherwise.
	 */
	public boolean logicProbe()
	{
		String kb = getKBForumla();

		// get the current map of the game
		String[][] map = g.getCurrentMap();

		// iterate over all the rows
		for (int j = 0; j < map.length; j++)
		{
			// iterate over the columns
			for (int i = 0; i < map[j].length; i++)
			{
				// find a covered cell that has an uncovered adjacent cell
				if (map[j][i].equals("?") && hasUncoveredAdj(i, j))
				{
					// test this cell for satisfiability

					// step 1 is to concatenate a no dagger label to the KB formula the easiest way to do this is by creating a cell object and using its getNoDaggerLabel() method. This allows the label format to be changed without editing this code while still matching the format used to construct the KB formula.
					Cell testCell = new Cell(i, j);
					String test = kb + " & " + testCell.getNoDaggerLabel();
					// now the test can be made
					try
					{
						Solver solver = new Solver(test);
						satTestCount++;

						if (!solver.isSatisfiable())
						{
							// place a stone in the cell tested
							System.out.print("Using solver: ");
							stone(testCell);
							satTestCompletions++;
							return true;
						}
					}
					catch (Exception e)
					{
						System.err.println(e.getMessage());
						System.exit(1);
					}
				}
			}
		}
		return false;
	}

	// returns the knowledge-base formula for a particular game state
	private String getKBForumla()
	{
		// get the current map of the game
		String[][] map = g.getCurrentMap();
		String kbFormula = "";

		// The first step is to find all the uncovered cells that have covered cells adjacent to them
		for (int j = 0; j < map.length; j++)
		{
			for (int i = 0; i < map[j].length; i++)
			{
				// if the cell has a number label the number of covered adjacent cells is checked
				if (map[j][i].charAt(0) < '9')
				{
					// update number of covered (unmarked) neighbours and stone or dagger neighbours and get a list of the covered cells around them
					ArrayList<Cell> coveredCells = updateAdjCount(i, j);
					// if the cell (i,j) has more than 0 covered adjacents then we need to add the information about the adjacents to this cell to KB
					if (coveredCells.size() > 0)
						kbFormula += getSubFormula(i, j, coveredCells) + "&";
				}

			}
		}
		// remove last &
		kbFormula = kbFormula.substring(0, kbFormula.length() - 1);
		return kbFormula;
	}

	// returns the sub-knowledge-base for a single clue givig cell.
	// e.g. (A & ~B | ~A & B)
	private String getSubFormula(int xCoordinate, int yCoordinate, ArrayList<Cell> coveredCells)
	{
		// get the number of daggers still undiscovered around this cell
		int daggersAdj = Integer.parseInt(g.getCell(xCoordinate, yCoordinate)) - daggerStoneCount;
		// get logical formula for this cell
		FormulaBuilder formula = new FormulaBuilder(coveredCells);
		return formula.getFormula(daggersAdj);
	}

	// checks if a cells has 1 or more uncovered adjacent cells
	private boolean hasUncoveredAdj(int xCoordinate, int yCoordinate)
	{
		// get the current map of the game
		String[][] map = g.getCurrentMap();
		// check the rows at yCoordinate -1 , yCoordinate, and yCoordinate +1
		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// skips if out-of-bounds y coordinate
			if (j < 0 || j > map.length - 1) continue;
			// check the columns at xCoordinate -1 , xCoordinate, and xCoordinate +1
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// skips if out-of-bounds x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (!map[j][i].equals("?"))
				{
					// uncovered adjacent cell found!
					return true;
				}
			}
		}
		// no uncovered adjacent cells found!
		return false;
	}
}
