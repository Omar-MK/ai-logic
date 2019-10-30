/**
 * A class for creating Cell Objects which are objects containing two integer
 * values an x and a y coordinate. Additional methods are provided for specific
 * use with the Gold and Daggers game to be used with logic solvers.
 *
 */
public class Cell
{
	/**
	 * X coordinate (column number) of the cell.
	 */
	private int xCoordinate;

	/**
	 * Y coordinate (row number) of the cell.
	 */
	private int yCoordinate;

	/**
	 * Cell content
	 */
	private String content;

	/**
	 * Constructs a cell object with x and y coordinates.
	 *
	 * @param xCoordinate the x coordinate of the cell
	 * @param yCoordinate the y coordinate of the cell
	 */
	public Cell(int xCoordinate, int yCoordinate)
	{
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}

	/**
	 * Constructs a cell object with x and y coordinates and a content value.
	 *
	 * @param xCoordinate the x coordinate of the cell
	 * @param yCoordinate the y coordinate of the cell
	 * @param content the content of the cell
	 */
	public Cell(int xCoordinate, int yCoordinate, String content)
	{
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.content = content;
	}

	/**
	 * Returns the x coordinate of the cell object.
	 *
	 * @return the x coordinate of the cell object
	 */
	public int getXCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Returns the y coordinate of the cell object.
	 *
	 * @return the y coordinate of the cell object
	 */
	public int getYCoordinate()
	{
		return yCoordinate;
	}

	/**
	 * Returns the content of the cell.
	 *
	 * @return the content of the cell
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Returns a label (String) in the form of "xy". Where x and y are the
	 * coordinates of the cell. Useful for use in logic solvers.
	 *
	 * @return a label (String) in the form of "xy"
	 */
	public String getDaggerLabel()
	{
		return "" + xCoordinate + "_" + yCoordinate;
	}

	/**
	 * Returns a label (String) in the form of "~xy". Where x and y are the
	 * coordinates of the cell. Useful for use in logic solvers.
	 *
	 * @return a label (String) in the form of "xy"
	 */
	public String getNoDaggerLabel()
	{
		return "~" + xCoordinate + "_" + yCoordinate;
	}

	/**
	 * Returns a boolean indicating if the x and y coordinates of a cell object
	 * are equal to the x and y coordinates of another cell object.
	 *
	 * @return true if the coordinates of two cell objects are equal
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Cell && xCoordinate == ((Cell) o).xCoordinate && yCoordinate == ((Cell) o).yCoordinate)
			return true;
		return false;
	}

	/**
	 * Returns a string containing the x and y coordinates of this Cell Object.
	 *
	 * @return a string containing the x and y coordinates of this Cell Object
	 */
	@Override
	public String toString()
	{
		return "[x: " + xCoordinate + ", y: " + yCoordinate + "]";
	}

}
