/**
 * A simple main program used to test the Solver class implementation
 */

public class SolverTester
{
	public static void main(String[] args)
	{
		// hand-written logical formula for figure 3 in the report
		String test = "(~2_0 & 2_1 | 2_0 & ~2_1) & (2_0 & ~2_1& ~2_2 | ~2_0 & 2_1& ~2_2 | ~2_0 & ~2_1& 2_2) & (~2_1 & 2_2 | 2_1 & ~2_2) & ~2_1 ";

		try
		{
			Solver s = new Solver(test);
			if (!s.isSatisfiable())
			{
				// this should be printed 
				System.out.println("Dagger here");
			}
			else
			{
				System.out.println("No Dagger here");
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
}
