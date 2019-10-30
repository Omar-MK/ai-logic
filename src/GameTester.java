/**
 * This is a main class used to test the Game class functionalities.
 */
public class GameTester
{
	public static void main(String[] args)
	{
		checkArgs(args);
		String[][] map = World.valueOf(args[0]).getMap();
		Game g = new Game(map);

		// Test inital map is filled with question marks
		System.out.println("Testing inital map generation...");
		String[][] currentMap = g.getCurrentMap();
		boolean similar = true;
		int totCells = 0;
		for (int j = 0; j < map.length; j++)
		{
			for (int i = 0; i < map[j].length; i++)
			{
				if (!currentMap[j][i].equals("?")) similar = false;
				totCells++;
			}
		}
		if (similar)
		{
			System.out.println("Initial map generation test: PASS\n");
		}
		else
		{
			System.out.println("Initial map generation test: FAIL\n");
		}
		System.out.println(g);

		// Test inital game state (lives == 1 && goldMinesCollected == 0 && covered cells == map.length * map.length
		System.out.println("Testing initial game state...");
		if (g.getGameState() == 2 && g.getLivesRemaining() == 1 && g.getGoldMinesCollected() == 0 && g.getCoveredCellCount() == totCells)
			System.out.println("Initial game states test: PASS\n");
		else
			System.out.println("Initial game states test: FAIL\n");

		// Test probing a 0 or gold cell
		System.out.println("Testing output of probing of 0 or g cell...");
		// each cell uncovered should be a neighbour of a "0" or "g" cell, each "0" or "g" cell must have a "0" or "g" neighbour/
		// each cell covered that is next to an uncovered cell should not be adjacent to a "0" or "g"
		g.probe(0, 0);
		currentMap = g.getCurrentMap();
		boolean pass = false;
		for (int j = 0; j < map.length; j++)
		{
			for (int i = 0; i < map[j].length; i++)
			{
				if (!currentMap[j][i].equals("?"))
				{
					pass = hasValidAdj(currentMap, i, j);
				}
				else if (currentMap[j][i].equals("?"))
				{
					pass = !hasInvalidUncoveredAdj(currentMap, i, j);
				}
			}
		}
		if (pass)
			System.out.println("Output of probe: PASS\n");
		else
			System.out.println("Output of probe: FAIL\n");

		System.out.println(g);

		// Test game state
		System.out.println("Testing game state updated correctly...");
		int gameState = 2, lives = 1, goldMines = 0, coveredCells = 0;

		for (int j = 0; j < currentMap.length; j++)
		{
			for (int i = 0; i < currentMap[j].length; i++)
			{
				if(currentMap[j][i].equals("g"))
				{
					goldMines++;
					lives++;
				}
				else if (currentMap[j][i].equals("d"))
					lives--;
				else if(currentMap[j][i].equals("?"))
					coveredCells++;
			}
		}
		gameState = getGameState(lives, coveredCells);
		if (g.getGameState() == gameState && g.getLivesRemaining() == lives && g.getGoldMinesCollected() == goldMines && g.getCoveredCellCount() == coveredCells)
			System.out.println("Game state update test: PASS\n");
		else
			System.out.println("Game state update test: FAIL\n");

		// Test probing a dagger
		System.out.println("Testing output of probing a dagger...");
		for (int j = 0; j < map.length; j++)
		{
			for (int i = 0; i < map[j].length; i++)
			{
				if(map[j][i].equals("d"))
				{
					g.probe(i, j);
					j = i = currentMap.length - 1;
				}
			}
		}

		System.out.println(g);
		// Test game state
		System.out.println("Testing game state updated correctly...");
		gameState = 2; lives = 1; goldMines = 0; coveredCells = 0;

		for (int j = 0; j < currentMap.length; j++)
		{
			for (int i = 0; i < currentMap[j].length; i++)
			{
				if(currentMap[j][i].equals("g"))
				{
					goldMines++;
					lives++;
				}
				else if (currentMap[j][i].equals("d"))
					lives--;
				else if(currentMap[j][i].equals("?") && !map[j][i].equals("d"))
					coveredCells++;
			}
		}
		gameState = getGameState(lives, coveredCells);
		if (g.getGameState() == gameState && g.getLivesRemaining() == lives && g.getGoldMinesCollected() == goldMines && g.getCoveredCellCount() == coveredCells)
			System.out.println("Game state update test: PASS\n");
		else
			System.out.println("Game state update test: FAIL\n");

		// Test probing a dagger
		System.out.println("Testing output of flagging a cell...");
		g.stone(0,0);
		int stoneCount = 0;
		for (int j = 0; j < currentMap.length; j++)
		{
			for (int i = 0; i < currentMap[j].length; i++)
			{
				if(currentMap[j][i].equals("s"))
				{
					stoneCount++;
				}
			}
		}
		if (stoneCount == 1 && g.getCurrentMap()[0][0].equals("s"))
		{
			System.out.println("Flagging a cell test: PASS\n");
		}
		else
		{
			System.out.println("Flagging a cell test: FAIL\n");
		}

	}

	static boolean hasValidAdj(String[][] map, int xCoordinate, int yCoordinate)
	{
		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// checks valid y coordinate
			if (j < 0 || j > map.length - 1) continue;
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// checks valid x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (map[j][i].equals("d") || map[j][i].equals("0")) return true;
			}
		}
		return false;
	}

	static boolean hasInvalidUncoveredAdj(String[][] map, int xCoordinate, int yCoordinate)
	{
		for (int j = yCoordinate - 1; j < yCoordinate + 2; j++)
		{
			// checks valid y coordinate
			if (j < 0 || j > map.length - 1) continue;
			for (int i = xCoordinate - 1; i < xCoordinate + 2; i++)
			{
				// checks valid x coordinate
				if (i < 0 || i > map[j].length - 1) continue;
				if (!map[j][i].equals("?") && (map[j][i].equals("g") || map[j][i].equals("0"))) return true;
			}
		}
		return false;
	}

	static int getGameState(int lives, int coveredCells)
	{
		if (lives > 0 && coveredCells > 0) return 2;
		else if (lives > 0 && coveredCells == 0) return 1;
		else return 0;
	}

	static void checkArgs(String[] args)
	{
		try
		{
			if (args.length != 1)
			{
				throw new IllegalArgumentException();
			}
			World.valueOf(args[0]);
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Usage: java GameTester <World_Name>");
			System.out.println("For-example: java GameTester EASY1");
			System.exit(1);
		}
	}
}
