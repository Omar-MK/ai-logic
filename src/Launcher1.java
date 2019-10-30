/**
 * Runs the game with a chosen map using an agent that utalises the single
 * point and random probing strategies.
 */
public class Launcher1
{
	public static void main(String[] args)
	{
		checkArgs(args);
		String[][] map = World.valueOf(args[0]).getMap();

		Agent1 a = new Agent1(new Game(map));
		a.start();
	}

	private static void checkArgs(String[] args)
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
			System.out.println("Usage: java Launcher1 <World_Name>");
			System.out.println("For-example: java Lanucher1 EASY1");
			System.exit(1);
		}
	}
}
