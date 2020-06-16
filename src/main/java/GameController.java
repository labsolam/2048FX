import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController
{
	private static final int DEFAULT_GRID_HEIGHT = 4;
	private static final int DEFAULT_GRID_WIDTH = 4;

	private int height;
	private int width;
	private final GameUI gameUI;
	private final Map<Location, Tile> tiles;

	private final int score = 0;

	GameController()
	{
		this(DEFAULT_GRID_HEIGHT, DEFAULT_GRID_WIDTH);
	}

	GameController(int height, int width)
	{
		this.height = height;
		this.width = width;
		this.tiles = new HashMap<>();
		IntStream.range(0, height).forEach(column ->
				IntStream.range(0, width).forEach(
						row -> tiles.put(new Location(column, row), null)
				)
		);
		this.gameUI = new GameUI(height, width);
		this.addKeyHandlers();
		this.startGame();
	}

	private void startGame()
	{
		//Generate 2 tiles to start
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

	private void addKeyHandlers()
	{
		this.getGame().setOnKeyPressed(e ->
		{
			if (e.getCode().isArrowKey())
			{
				move(Direction.valueOf(e.getCode().name()));
			}
		});
	}

	private void move(Direction direction)
	{
		Comparator<Integer> comparator = direction == Direction.UP || direction == Direction.LEFT ?
				Comparator.naturalOrder() : Collections.reverseOrder();

		IntStream.range(0, width)
				.boxed()
				.sorted(comparator)
				.forEach(col ->
						IntStream.range(0, height)
								.boxed()
								.sorted(comparator)
								.forEach(row ->
										{
											Location currentLocation = new Location(col, row);
											Location furthestLocation = findFurthestCell(currentLocation, direction);

											if (!currentLocation.equals(furthestLocation))
											{
												System.out.println("Current: " + currentLocation + " Furthest: " + furthestLocation);
											}
										}
								)
				);
	}

	private Location findFurthestCell(Location location, Direction direction)
	{
		Location furthestCell = location;
		Location offsetLocation = Location.offsetLocation(furthestCell, direction);

		while (isLocationValid(offsetLocation))
		{
			if (canCellMove(location, offsetLocation))
			{
				furthestCell = offsetLocation;
			}
			offsetLocation = Location.offsetLocation(offsetLocation, direction);
		}

		return furthestCell;
	}

	private boolean isLocationValid(Location location)
	{
		return location.getX() >= 0 && location.getX() < this.width && location.getY() >= 0 && location.getY() < this.height;
	}

	private boolean canCellMove(Location locationToMove, Location locationToMoveTo)
	{
		//Don't move empty tiles
		if (getOptionalTile(locationToMove).isEmpty())
		{
			return false;
		}

		if (getOptionalTile(locationToMoveTo).isEmpty())
		{
			return true;
		}

		return this.tiles.get(locationToMove).getValue() == this.tiles.get(locationToMoveTo).getValue();
	}

	private Optional<Tile> getOptionalTile(Location location)
	{
		return Optional.ofNullable(this.tiles.get(location));
	}
}
