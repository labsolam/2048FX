import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		this.generateRandomTile();
	}

	VBox getGame()
	{
		return this.gameUI.getGameView();
	}

	private void generateRandomTile()
	{
//		Random
		this.getEmptyLocations().forEach(System.out::println);
	}

	private List<Location> getEmptyLocations()
	{
		return this.tiles.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == null)
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
