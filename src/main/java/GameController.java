import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.nio.charset.IllegalCharsetNameException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

	private boolean test = false;

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
		this.generateRandomTile();
		this.generateRandomTile();
	}

	VBox getGame()
	{
		return this.gameUI.getGameView();
	}

	private Tile generateRandomTile()
	{
		List<Location> emptyLocations = this.getEmptyLocations();
		Location newRandom = emptyLocations.get(new Random().nextInt(emptyLocations.size()));

		// 90% chance of 2
		int value = new Random().nextDouble() < 0.9d ? 2 : 4;
		Tile tile = new Tile(value, newRandom.getX(), newRandom.getY());

		this.tiles.put(tile.getLocation(), tile);
		this.gameUI.addTile(tile);

		return tile;
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
		synchronized (tiles)
		{
			if (test)
			{
				return;
			}
		}

		ParallelTransition parallelTransition = new ParallelTransition();
		List<Tile> tilesToRemove = new ArrayList<>();

		//Each move updates the score, but you can only use final variables in the lambda
		//I could use AtomicInteger, but  it's not necessary as this will never be parallel
		//Little trick to update the score using a final array
		final int[] score = new int[1];

		this.boardNavigator(direction, x ->
		{
			Location currentLocation = new Location(col, row);
			Location furthestLocation = findFurthestCell(currentLocation, direction);

			if (!currentLocation.equals(furthestLocation))
			{
				Optional<Tile> optionalTile = getOptionalTile(furthestLocation);
				if (optionalTile.isPresent())
				{
					parallelTransition.getChildren()
							.add(animateMergeTiles(currentLocation, furthestLocation));
					tilesToRemove.add(this.tiles.get(currentLocation));
					this.tiles.replace(currentLocation, null);
					this.tiles.get(furthestLocation).merge();
					score[0] += this.tiles.get(furthestLocation).getValue();
				} else
				{
					parallelTransition.getChildren().add(animateMoveTiles(currentLocation, furthestLocation));
					//Tile is empty, move
					Tile tile = this.tiles.replace(currentLocation, null);
					this.tiles.put(furthestLocation, tile);
				}
			}
		}
				);

		parallelTransition.setOnFinished(e -> {
			this.tiles.values().stream().filter(Objects::nonNull).forEach(Tile::unMerge);

			if (this.getEmptyLocations().size() == 0 && movementsAvailable())
			{

			}

			synchronized (this.tiles)
			{
				this.test = false;
			}
		});

		synchronized (this.tiles)
		{
			this.test = true;
		}

		parallelTransition.play();
		this.gameUI.getBoard().getChildren().removeAll(tilesToRemove);


//		parallelTransition.getChildren().add(animateAddingNewTile(this.generateRandomTile()));
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

		return this.tiles.get(locationToMove).canMerge(this.tiles.get(locationToMoveTo));
	}

	private Optional<Tile> getOptionalTile(Location location)
	{
		return Optional.ofNullable(this.tiles.get(location));
	}

	private ParallelTransition animateMergeTiles(Location toMove, Location toMoveTo)
	{
		//First move the tile
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().add(animateMoveTiles(toMove, toMoveTo));

		//Scale the merge
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(80));
		scaleTransition.setNode(this.tiles.get(toMoveTo));
		scaleTransition.setAutoReverse(true);
		scaleTransition.setCycleCount(2);
		scaleTransition.setByX(0.1f);
		scaleTransition.setByY(0.1f);
		scaleTransition.setInterpolator(Interpolator.EASE_IN);

		parallelTransition.getChildren().add(scaleTransition);

		return parallelTransition;
	}

	private TranslateTransition animateMoveTiles(Location toMove, Location toMoveTo)
	{
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(65));
		translateTransition.setNode(this.tiles.get(toMove));
		translateTransition.setByX((toMoveTo.getX() - toMove.getX()) * (GameUI.MIN_CELL_SIZE));
		translateTransition.setByY((toMoveTo.getY() - toMove.getY()) * (GameUI.MIN_CELL_SIZE));

		return translateTransition;
	}

	private ScaleTransition animateAddingNewTile(Tile tile)
	{
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(80), tile);
		scaleTransition.setFromX(1);
		scaleTransition.setFromY(1);
		scaleTransition.setToX(0.5);
		scaleTransition.setToY(0.5);
		scaleTransition.setCycleCount(2);
		scaleTransition.setAutoReverse(true);

		return scaleTransition;
	}

	private boolean movementsAvailable()
	{

	}

	private void boardNavigator(Direction direction, BiConsumer<Integer, Integer> consumer)
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
								.forEach(consumer));
	}
}
