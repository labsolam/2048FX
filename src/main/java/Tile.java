import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class Tile extends Label
{
	private static final String CSS_PREFIX = "tile-";
	private int value;
	private final Location location;
	private boolean merged;

	Tile(int value, int x, int y)
	{
		this.value = value;
		this.location = new Location(x, y);
		setText(Integer.toString(value));
		setAlignment(Pos.CENTER);
		getStyleClass().add((CSS_PREFIX + this.value));
	}

	int getValue()
	{
		return value;
	}

	boolean isMerged()
	{
		return merged;
	}

	public Location getLocation()
	{
		return location;
	}

	void setMerged(boolean merged)
	{
		this.merged = merged;
	}

	boolean canMerge(Tile other)
	{
		return this.value == other.value && !other.isMerged() && !this.isMerged();
	}

	void merge()
	{
		this.merged = true;
		this.value *= 2;
		getStyleClass().remove(CSS_PREFIX + this.value);
		getStyleClass().add(CSS_PREFIX +  this.value);
	}

	@Override
	public String toString()
	{
		return "Tile{" +
				"score=" + value +
				", location=" + location +
				", merged=" + merged +
				'}';
	}
}
