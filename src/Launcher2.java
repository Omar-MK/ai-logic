/**
 * Runs the game with a chosen map using an agent that utalises the single
 * point and random probing strategies aswell as using SAT solvers to solve
 * difficult Gold and Daggers games.
 */
public class Launcher2
{
	public static void main(String[] args)
	{
		checkArgs(args);
		String[][] map = World.valueOf(args[0]).getMap();

		Agent2 a = new Agent2(new Game(map));
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
			System.out.println("Usage: java -cp .:../libraries/antlr-4.7.1-complete.jar:../libraries/logicng-1.4.0.jar:../libraries/sat4j-pb.jar Launcher2 <World_Name>");
			System.out.println("For-example: java -cp .:../libraries/antlr-4.7.1-complete.jar:../libraries/logicng-1.4.0.jar:../libraries/sat4j-pb.jar Launcher2 Lanucher2 EASY1");
			System.exit(1);
		}
	}
}
