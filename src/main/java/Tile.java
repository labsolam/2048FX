import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class Tile extends Label
{
	private static final String CSS_PREFIX = "tile-";
	private int score;
	private final Location location;
	private boolean merged;

	Tile(int score, int x, int y)
	{
		this.score = score;
		this.location = new Location(x, y);
		setText(Integer.toString(score));
		setAlignment(Pos.CENTER);
		getStyleClass().add((CSS_PREFIX + this.score));
	}

	int getScore()
	{
		return score;
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
		return this.score == other.score && !other.isMerged() && !this.isMerged();
	}

	void merge()
	{
		this.merged = true;
		this.score *= 2;
		getStyleClass().remove(CSS_PREFIX + this.score);
		getStyleClass().add(CSS_PREFIX +  this.score);
	}

	@Override
	public String toString()
	{
		return "Tile{" +
				"score=" + score +
				", location=" + location +
				", merged=" + merged +
				'}';
	}
}
