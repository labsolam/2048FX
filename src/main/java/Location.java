public class Location
{
	private final int x;
	private final int y;

	Location(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Location location = (Location) o;

		if (x != location.x) return false;
		return y == location.y;
	}

	@Override
	public int hashCode()
	{
		int result = x;
		result = 31 * result + y;
		return result;
	}

	@Override
	public String toString()
	{
		return "Location{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
