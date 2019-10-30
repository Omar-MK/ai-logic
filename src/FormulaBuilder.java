import java.util.ArrayList;

/**
 * Formula builder class that returns a propositional logic formula for a list
 * of cells given the number of daggers contained in the list
 */
public class FormulaBuilder {

	/**
	 * List of covered cells that contain a number of daggers
	 */
	private ArrayList<Cell> cells;

	/**
	 * The propositional formula field
	 */
	private String formula = "";

	/**
	 * Constructor to create a FormulaBuilder Object.
	 *
	 * @param cells - a list of covered cell objects to be included in the
	 * formula
	 */
	public FormulaBuilder(ArrayList<Cell> cells)
	{
		this.cells = cells;
	}

	/**
	 * Returns a forumla of all the possible combinations given a combination
	 * size.
	 *
	 * @param combSize - the combination size (number of dagger in cells)
	 * @return a forumla of all the possible combinations
	 */
	public String getFormula(int combSize)
	{
		// create temp list of combinations
		ArrayList<Cell> data = new ArrayList<Cell>();
		// initialise list with empty placeholders
		for (int i = 0; i < combSize; i++)
		{
			data.add(null);
		}
		// add opening bracket to contain formula
		formula += "(";
		// concatinate combinations to formula
		concatCombinations(data, 0, cells.size() - 1, 0, combSize);
		// remove last "\ "
		formula = formula.substring(0, formula.length() - 2);
		// add closing bracket
		formula += ")";
		return formula;
	}

	/**
	 * This is the getFormula helper method. This method calls itself
	 * recursively to produce all the combinations given the combSize parameter
	 *
	 * @param data - the temp combination list
	 * @param start - the first element in cells to be included as part of the
	 * combinations
	 * @param end - the final element in cells to be included as part of the
	 * combinations
	 * @param index - used as part of the logic (set to 0)
	 * @param combSize - number of elements being permuted (number of daggers)
	 */
    private void concatCombinations(ArrayList<Cell> data, int start,
    int end, int index, int combSize)
    {
        if (index == combSize)
        {
            for (int i = 0; i < combSize; i++)
                formula += data.get(i).getDaggerLabel() + " & ";
			// remove last " & "
			formula = formula.substring(0, formula.length() - 3);
			// iterate through cells list and check which cells are not contained in data, add those to the formula but with a no dagger label
			for (int i = 0; i < cells.size(); i++)
			{
				if (!data.contains(cells.get(i)))
				{
					formula += " & " + cells.get(i).getNoDaggerLabel();
				}
			}
			// add or operator
            formula += "| ";
            return;
        }

		// get next combination
        for (int i = start; i <= end && end - i + 1 >= combSize - index; i++)
        {
			// replace cell(i) with the cell in data(index)
            data.set(index, cells.get(i));
            concatCombinations(data, i + 1, end, index + 1, combSize);
        }

    }


}
