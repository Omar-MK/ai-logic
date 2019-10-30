import java.util.Arrays;
import java.util.ArrayList;

/**
 * A simple game class that allows an agent to play the Gold and Daggers game
 * inspired by minesweeper
 *
 */
public class Game
{
	/**
	 * The map of the D&G board.
	 */
	private final String[][] MAP;

	/**
	 * The map the agent playing the game can see at any one time.
	 */
	private String[][] currentMap;

	/**
	 * A counter for the number of lives left while playing the game.
	 */
	private int lives = 1;

	/**
	 * The number of daggers in MAP.
	 */
	private int daggers;

	/**
	 * A counter for the number of cells still covered.
	 */
	private int coveredCellCount;

	/**
	 * The number of total gold mines contained in the map.
	 */
	private int goldMines;

	/**
	 * The number of gold mines collected throughout the game.
	 */
	private int goldMinesCollected;

	/**
	 * A list of the cells uncovered after each probe call.
	 * This field is required as a recursive function is used to uncover the
	 * cells after a probe call.
	 */
	private ArrayList<Cell> uncoveredCells;


	/**
	 * Constructor. Takes a map in the form of a 2d String array.
	 * Array contains n by n cells each containing either: a number (as a
	 * string) form "0" - "8" indicating the number of daggers adjacent to any
	 * cell, a string "g" indicating a gold mine, and a string "d" indicating a
	 * dagger.
	 *
	 */
	public Game(String[][] map)
	{
		this.MAP = map;
		currentMap = new String[MAP.length][MAP[0].length];
		uncoveredCells = new ArrayList<Cell>();
		populateCurrentMap("?");
		countGoldAndDaggers();
		coveredCellCount = (MAP.length * MAP.length) - daggers;
	}

	/**
	 * Method to probe a cell on the map.
	 *
	 * @param xCoordinate the xCoordinate (column number) of the cell to be
	 * probed
	 * @param yCoordinate the yCoordinate (row number) of the cell to be probed
	 * @return an ArrayList of Cell Objects containing the cell that was probed
	 * as well as any cells autoprobed. Cells are autoprobed if a 0 or g cell
	 * is probed.
	 */
	public ArrayList<Cell> probe(int xCoordinate, int yCoordinate)
	{
		// clear the uncoveredCells list from the last probe call
		uncoveredCells.clear();
		// call the probeHelper method
		return probeHelper(xCoordinate, yCoordinate);
	}

	/**
	 * Helper method to probe a cell on the map.
	 * Warning... Uses recursion to add new uncovered cells to the returned
	 * ArrayList, thus does not clear uncoveredCells before returning.
	 *
	 * @param xCoordinate the xCoordinate (column number) of the cell to be
	 * probed
	 * @param yCoordinate the yCoordinate (row number) of the cell to be probed
	 * @return an ArrayList of Cell Objects containing the cell that was probed
	 * as well as any cells autoprobed. Cells are autoprobed if a 0 or g cell
	 * is probed.
	 */
	private ArrayList<Cell> probeHelper(int xCoordinate, int yCoordinate)
	{
		/* in cases where the cell does not contain a "0" or "g"
		 one cell is uncovered */
		if (!MAP[yCoordinate][xCoordinate].equals("0") &&
		 !MAP[yCoordinate][xCoordinate].equals("g"))
		{
			// cell is uncovered in currentMap (i.e. the value is copied over from the origonal MAP field)
			currentMap[yCoordinate][xCoordinate] = MAP[yCoordinate][xCoordinate];
			// Cell object containing uncovered cell is constructed and added to uncoveredCells
			Cell uncoveredCell = new Cell(xCoordinate, yCoordinate, MAP[yCoordinate][xCoordinate]);
			uncoveredCells.add(uncoveredCell);
			// If the cell is a dagger, reduce the number of lives, and print a message indicating the event.
			if (MAP[yCoordinate][xCoordinate].equals("d"))
			{
				lives--;
				System.out.println("dagger-in " + uncoveredCell + " , new life count " + lives);
			}
			else coveredCellCount--;
		}
		/* in cases where the cell contains a "0" or "g"
		 recursively uncover all cells surrounding the chosen cell and the
		 "0" or "g" cells uncovered in the chosen cell's surroundings */
		else
		{
			// uncovers top row: [x-1, y-1], [x, y-1], [x+1, y-1] then,
			// uncovers middle row: [x-1, y], [x, y], [x+1, y] then,
			// uncovers bottom row: [x-1, y+1], [x, y+1], [x+1, y+1]
			for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
			{
				// skips out-of-bounds y coordinates
				if (j < 0 || j > MAP.length - 1) continue;
				for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
				{
					// skips out-of-bounds x coordinates or previously uncovered cells
					if (i < 0 || i > MAP[j].length - 1 || !currentMap[j][i].equals("?")) continue;
					// if reached then currentMap[j][i] is a covered cell
					currentMap[j][i] = MAP[j][i];
					Cell uncoveredCell = new Cell(i, j, MAP[j][i]);
					uncoveredCells.add(uncoveredCell);
					coveredCellCount--;
					// if the cell uncovered contained a gold mine, increase lives, and goldMinesCollected count
					if (MAP[j][i].equals("g"))
					{
						lives++;
						goldMinesCollected++;
						System.out.println("goldmine-in " + uncoveredCell + " , new life count " + lives);
						// probe this cell using recursive call
						probeHelper(i, j);
					}
					else if (MAP[j][i].equals("0"))
					{
						// probe this cell using recursive call
						probeHelper(i, j);
					}
				}
			}
		}
		return uncoveredCells;
	}

	/**
	 * Places a stone flag/marker at a given cell.
	 *
	 * @param xCoordinate the xCoordinate (column number) of the cell to marked
	 * @param yCoordinate the yCoordinate (row number) of the cell to be marked
	 * @return returns false if invalid coordinates are given and true otherwise
	 */
	public boolean stone(int xCoordinate, int yCoordinate)
	{
		// checks valid coordinates
		if (yCoordinate < 0 || yCoordinate > MAP.length - 1 || xCoordinate < 0 || xCoordinate > MAP[yCoordinate].length - 1)
			return false;
		currentMap[yCoordinate][xCoordinate] = "s";
		return true;
	}

	/**
	 * Returns the currentMap of the game at the current step in the game.
	 *
	 * @return the currentMap of the game at the current step
	 */
	public String[][] getCurrentMap()
	{
		return currentMap;
	}

	/**
	 * Returns the current String in the cell who's coordinates are passed.
	 *
	 * @param xCoordinate the xCoordinate (column number) of the cell
	 * @param yCoordinate the yCoordinate (row number) of the cell
	 * @return the content of the cell at the specified coordinates
	 */
	public String getCell(int xCoordinate, int yCoordinate)
	{
		return currentMap[yCoordinate][xCoordinate];
	}

	/**
	 * Returns if the game was won, lost, or ongoing
	 * 2 means the game is still not finished
	 * 1 means the game is won
	 * 0 means the game was lost
	 *
	 * @return the current state of the game (won == 1/lost == 0/ongoing == 2)
	 */
	public int getGameState()
	{
		if (lives > 0 && coveredCellCount > 0)
			return 2;
		else if (lives > 0 && coveredCellCount == 0)
			return 1;
		else
			return 0;
	}

	/**
	 * Returns the number of lives remaining at the current game step.
	 *
	 * @return the number of lives remaining at the current game step
	 */
	public int getLivesRemaining()
	{
		return lives;
	}

	/**
	 * Returns the total gold mines collected up until the current game step.
	 *
	 * @return the total gold mines collected up until the current game step
	 */
	public int getGoldMinesCollected()
	{
		return goldMinesCollected;
	}

	/**
	 * Returns the number of cells still covered.
	 *
	 * @return the number of cells still covered
	 */
	public int getCoveredCellCount()
	{
		return coveredCellCount;
	}

	/**
	 * Returns the dagger count in the map.
	 *
	 * @return the dagger count in the map
	 */
	public int getDaggerCount()
	{
		return daggers;
	}

	/**
	 * Counts the number of Gold mines and daggers in the map.
	 *
	 */
	private void countGoldAndDaggers()
	{
		// iterate through rows
		for (int j = 0; j < MAP.length; j++)
		{
			// iterate through columns
			for (int i = 0; i < MAP[j].length; i++)
			{
				if (MAP[j][i].equals("d")) daggers++;
				else if (MAP[j][i].equals("g")) goldMines++;
			}
		}
	}

	/**
	 * Fills currentMap with a String s.
	 *
	 * @param s the string to fill the currentMap with
	 */
	private void populateCurrentMap(String s)
	{
		for (int j = 0; j < currentMap.length; j++)
		{
			for (int i = 0; i < currentMap[j].length; i++)
			{
				currentMap[j][i] = s;
			}
		}
	}

	/**
	 * Returns a string containing the current game map and stats.
	 *
	 * @return a string containing the current game map and stats
	 */
	@Override
	public String toString()
	{
		String gameGrid = "\n";
		for (int j = 0; j < currentMap.length; j++)
		{
			gameGrid += "[ " + currentMap[j][0];
			for (int i = 1; i < currentMap[j].length; i++)
			{
				gameGrid += ", " + currentMap[j][i];
			}
			gameGrid += "]\n";
		}
		// lives left game state number of daggers / gold mine not found underneath
		String gameStats = "Lives left: " + lives + "\nGold mines collected: " + goldMinesCollected + "/" + goldMines + "\nCovered Cells: " + coveredCellCount + "\n";
		return gameGrid + gameStats;
	}
}
