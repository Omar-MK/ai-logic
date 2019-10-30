import java.util.ArrayList;

/**
 * A simple main program used to test the FormulaBuilder class
 */
public class Combination
{
	static String combinations = "";
	public static void main(String[] args)
	{
		ArrayList<Cell> coveredCells = new ArrayList<>();

		coveredCells.add(new Cell(1, 1));
		coveredCells.add(new Cell(2, 2));
		coveredCells.add(new Cell(3, 3));
		coveredCells.add(new Cell(4, 4));
		coveredCells.add(new Cell(5, 5));

		// where i is the number of daggers
		for (int i = 1; i < 5; i++)
		{
			FormulaBuilder formula = new FormulaBuilder(coveredCells);
			System.out.println("Number of daggers: " + i);
			System.out.println("Formula: " + formula.getFormula(i) + "\n");
		}




	}
}
