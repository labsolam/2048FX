import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController
{
	private static final int DEFAULT_GRID_HEIGHT = 4;
	private static final int DEFAULT_GRID_WIDTH = 4;

	private final GameUI gameUI;
	private final Map<Location, Tile> tiles;

	GameController()
	{
		this(DEFAULT_GRID_HEIGHT, DEFAULT_GRID_WIDTH);
	}

	GameController(int gridSize)
	{
		this(gridSize, gridSize);
	}

	GameController(int height, int width)
	{
		this.tiles = new HashMap<>();
		IntStream.range(0, 4).forEach(column ->
				IntStream.range(0, 4).forEach(
						row -> tiles.put(new Location(column, row), null)
				)
		);
		this.gameUI = new GameUI(height, width);
		this.startGame();
	}

	private void startGame()
	{
		Tile tile1 = this.generateRandomTile();
		Tile tile2 = this.generateRandomTile();

		this.tiles.put(tile1.getLocation(), tile1);
		this.tiles.put(tile2.getLocation(), tile2);

		this.gameUI.addTile(tile1);
		this.gameUI.addTile(tile2);
	}

	VBox getGame()
	{
		return this.gameUI.getGameView();
	}

	private Tile generateRandomTile()
	{
		List<Location> emptyLocations = this.getEmptyLocations();
		Collections.shuffle(emptyLocations);
		Location newRandom = emptyLocations.get(new Random().nextInt(emptyLocations.size()));

		// 90% chance of 2
		int value = new Random().nextDouble() < 0.9d ? 2 : 4;

		return new Tile(value, newRandom.getX(), newRandom.getY());
	}

	private List<Location> getEmptyLocations()
	{
		return this.tiles.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == null)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}
}
