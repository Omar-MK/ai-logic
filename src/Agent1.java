import java.util.ArrayList;
import java.util.Random;

/**
 * An agent class that contains the code for a simple agent that can play the
 * Gold and Daggers game using random probing and single point stategies
 */
public class Agent1
{
	/**
	 * Reference to game Objects utalised by the agent.
	 */
	protected Game g;

	/**
	 * List of Cell objects that represent the cells still covered in the game.
	 */
	protected ArrayList<Cell> coveredCells;

	/**
	 * The number of daggers in the map.
	 */
	protected int daggerCount;

	/**
	 * Global field used by the updateAdjCount method to record the number of
	 * unmarked (or unflagged cells) adjacent to the cell passed as a parameter.
	 */
	protected int unmarkedAdjCount;

	/**
	 * Global field used by the updateAdjCount method to record the number of
	 * daggers + flags adjacent to the cell passed as a parameter.
	 */
	protected int daggerStoneCount;

	/**
	 * Field used to record the number of random probes made while solving the
	 * map.
	 */
	protected int randomProbes = 0;

	/**
	 * Field used to record the number of SPS cycles made while solving the map.
	 */
	protected int spsIterations = 0;

	/**
	 * The Random Object used by the randomProbe method to pick a random cell
	 * to probe from the list of covered cells.
	 */
	private Random rand;

	/**
	 * Constructor for Agent1.
	 * @param g a Game Object of a Daggers and Golds game instance.
	 */
	public Agent1(Game g)
	{
		this.g = g;
		rand = new Random();
		coveredCells = new ArrayList<Cell>();
		daggerCount = g.getDaggerCount();
		fillCoveredList();
	}

	/**
	 * Method used to initate the agent to begin solving the D&G world.
	 */
	public void start()
	{
		// first step is to probe 0,0
		targetProbe(coveredCells.get(0));
		while (g.getGameState() == 2)
		{
			spsProbe();
			// this if statement checks the game state as determined by the game class. I.e. once all covered safe cells have been uncovered 2 will change. This also takes into account the case where the number of covered cells is the same as the number of daggers remaining.
			if (g.getGameState() != 2) break;
			// if the number of stoned cells is the same as the number of daggers in the map, then the game must be complete, all that is left is to probe the reamining covered cells.
			else if (daggerCount == 0)
			{
				probeAllRemaining();
				break;
			}
			randomProbe();
			randomProbes++;
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
	}

	/**
	 * Method used to randomly probe a cell from the list of covered cells.
	 */
	public void randomProbe()
	{
		System.out.print("Using random probe: ");
		// choose a random index from coveredCells
		int index = rand.nextInt(coveredCells.size());
		// targetProbe the cell corresponding to this index
		targetProbe(coveredCells.get(index));
	}

	/**
	 * Method used by all probe methods to actually probe a cell and update the
	 * Agent fields depending on the results of the probe.
	 *
	 * @param c - the cell to be probed in the D&G Game instance
	 */
	public void targetProbe(Cell c)
	{
		// print the operation to be carried out
		System.out.println("reveal " + c);
		//  probe the cell and get a list of new uncovered cells
		ArrayList<Cell> uncoveredCells;
		uncoveredCells = g.probe(c.getXCoordinate(), c.getYCoordinate());
		// check if the uncovered cell is a dagger
		if (uncoveredCells.get(0).getContent().equals("d"))
		{
			daggerCount--;
		}
		else
		{
			// remove uncovered cells from the coveredCells list
			for (int i = 0; i < uncoveredCells.size(); i++)
			{
				coveredCells.remove(uncoveredCells.get(i));
			}
		}

		System.out.println(g);
	}

	/**
	 * Method contains SPS algorithm.
	 */
	public void spsProbe()
	{
		String[][] map = g.getCurrentMap();
		boolean changeMade;
		// keep looping until single point strategy cannot make any more changes
		do
		{
			spsIterations++;
			changeMade = false;
			// loop through each row
			for (int j = 0; j < map.length; j++)
			{
				// loop through each column
				for (int i = 0; i < map[j].length; i++)
				{
					// update number of covered (unmarked) neighbours and stone or dagger neighbours
					updateAdjCount(i, j);
					// if the cell has a number 0-8 and there are more than 0 covered cells around it enter this if statement
					if (map[j][i].charAt(0) < '9' && unmarkedAdjCount > 0)
					{
						// if # clue = # of daggers already marked = uncover all
						if (Integer.parseInt(map[j][i]) == daggerStoneCount)
						{
							System.out.println("Uncovering all adj");
							uncoverAllCoveredAdj(i, j);
							changeMade = true;
						}
						// mark a dagger if # unmarked & covered cells = #clue - # of daggers already marked
						else if (unmarkedAdjCount == Integer.parseInt(map[j][i]) - daggerStoneCount)
						{
							System.out.println("Flagging all adj");
							stoneAllCoveredAdj(i, j);
							changeMade = true;
						}
					}
				}
			}
		} while (changeMade);
	}

	/**
	 * Method used to flag a cell.
	 *
	 * @param c the cell Object to be flagged in the D&G Game instance
	 */
	public void stone(Cell c)
	{
		System.out.println("stone-in " + c);
		g.stone(c.getXCoordinate(), c.getYCoordinate());
		coveredCells.remove(c);
		daggerCount--;
	}

	/**
	 * Method used to probes all remaining covered cells in coveredCells.
	 */
	public void probeAllRemaining()
	{
		System.out.println("All daggers found.\nProbing the rest of the cells");
		// uncover all cells in coveredCells
		for (int i = 0; i < coveredCells.size(); i++)
		{
			Cell currentCell = coveredCells.get(i);
			System.out.println("reveal " + currentCell);
			g.probe(currentCell.getXCoordinate(), currentCell.getYCoordinate());
		}
		// empty coveredCells
		coveredCells.clear();
	}

	/**
	 * Updates the unmarkedAdjCount and daggerStoneCount fields and returns an
	 * ArrayList of covered cells around this cell. The List returned can be
	 * used to reduce extra computation as is the case in Agent2
	 *
	 * @param xCoordinate - the x coordinate of the cell who's adjacent cells
	 * are to be checked.
	 * @param yCoordinate - the y coordinate of the cell who's adjacent cells
	 * are to be checked.
	 * @return ArrayList of covered cells around the coordinates passed
	 */
	protected ArrayList<Cell> updateAdjCount(int xCoordinate, int yCoordinate)
	{
		String[][] map = g.getCurrentMap();
		unmarkedAdjCount = 0;
		daggerStoneCount = 0;
		// initiate ArrayList of covered cells around the cell passed
		ArrayList<Cell> coveredCells = new ArrayList<Cell>();
		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// skips if out-of-bounds y coordinate
			if (j < 0 || j > map.length - 1) continue;
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// skips if out-of-bounds x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (map[j][i].equals("?"))
				{
					unmarkedAdjCount++;
					coveredCells.add(new Cell(i, j));
				}
				else if (map[j][i].equals("s") || map[j][i].equals("d"))
					daggerStoneCount++;
			}
		}
		return coveredCells;
	}

	// uncovers all adjacent covered cells to a given cell
	private void uncoverAllCoveredAdj(int xCoordinate, int yCoordinate)
	{
		String[][] map = g.getCurrentMap();

		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// skips if out-of-bounds y coordinate
			if (j < 0 || j > map.length - 1) continue;
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// skips if out-of-bounds x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (map[j][i].equals("?")) targetProbe(new Cell(i, j));
			}
		}
	}

	// flags all adjacent cells to a given cell
	private void stoneAllCoveredAdj(int xCoordinate, int yCoordinate)
	{
		String[][] map = g.getCurrentMap();

		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// skips if out-of-bounds y coordinate
			if (j < 0 || j > map.length - 1) continue;
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// skips if out-of-bounds x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (map[j][i].equals("?")) stone(new Cell(i, j));
			}
		}
	}

	// populates the coveredCells list with all the cells in the grid
	private void fillCoveredList()
	{
		coveredCells = new ArrayList<Cell>();
		for (int j = 0; j < g.getCurrentMap().length; j++)
		{
			for (int i = 0; i < g.getCurrentMap()[j].length; i++)
			{
				coveredCells.add(new Cell(i, j));
			}
		}
	}


}
