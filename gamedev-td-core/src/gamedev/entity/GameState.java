package gamedev.entity;

import gamedev.entity.Enemy.EnemyType;
import gamedev.entity.Tile.TileType;
import gamedev.entity.TowerFactory.TowerType;
import gamedev.level.Level;
import gamedev.td.Config;
import gamedev.td.GDSprite;
import gamedev.td.SpriteManager;
import gamedev.td.helper.MathHelper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameState {
	private static GameState instance;

	public static final int GRIDX = 17, GRIDY = 12;

	/**
	 * One full round lasts thirty (30) seconds.
	 */
	private static final float ROUND_DURATION = 30;

	private static final float PRE_ROUND_WAIT_DURATION = 5;

	private Level currentLevel;

	private int score = 0;
	private int money = 0;
	private int playerLife = 10;
	private int spawnedEnemies;

	private TileType grid[][];
	private float roundTime, beginRoundWaitTime = 5, spawnDelay;

	private List<Enemy> enemies;
	private List<Integer> enemiesToBeSpawned;
	private List<Tower> deployedTowers;

	private boolean roundHasStarted;

	public static GameState getInstance() {
		if (instance == null)
			instance = new GameState();
		return instance;
	}

	/**
	 * Game state cannot be instantiated outside of the class. To get a reference to this object, call the static method getInstance().
	 */
	private GameState() {
		instance = this;
		roundHasStarted = false;
		enemiesToBeSpawned = new ArrayList<Integer>();
		enemies = new ArrayList<Enemy>();
		deployedTowers = new ArrayList<Tower>();
		createMap();
	}

	private void createMap() {
		grid = new TileType[GRIDX][GRIDY];
		for (int i = 0; i < GRIDX; i++) {
			for (int j = 0; j < GRIDY; j++) {
				grid[i][j] = TileType.Used;
			}
		}
	}

	public void initialize() {
		currentLevel = Level.generateLevel(1);
		score = 0;
		money = 100;
		playerLife = 10;
		spawnDelay = 0;
		spawnedEnemies = 0;
		roundTime = PRE_ROUND_WAIT_DURATION;
		roundHasStarted = false;
	}

	public void update(float delta) {
		updateRoundTimer(delta);

		if (roundHasStarted) {
			checkForEnemySpawn(delta);

			for (Enemy enemy : enemies)
				enemy.update(delta);

			for (Tower tower : deployedTowers)
				tower.update(delta);
		}
	}

	private void updateRoundTimer(float delta) {
		if (roundTime > 0)
			roundTime -= delta;
		else {
			roundHasStarted = true;
			roundTime = ROUND_DURATION;
			prepareEnemies();
		}
	}

	public void render(SpriteBatch spriteBatch) {
		displayMap(spriteBatch);
	}

	private void displayMap(SpriteBatch spriteBatch) {
		spriteBatch.begin();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				TileType type = grid[i][j];

				Tile newTile = Tile.create(type);
				Vector2 position = new Vector2(i * Config.tileSize, j * Config.tileSize);
				newTile.setPosition(position);
				newTile.draw(spriteBatch);
			}
		}
		spriteBatch.end();
	}

	public boolean checkProjectileCollision() {

		return false;
	}

	public void prepareLevel(int lvl) {
		String level[] = Level.getInstance().getGrid();

		for (int y = 0; y < level.length; y++) {
			for (int x = 0; x < level[y].length(); x++) {
				grid[x][y] = Tile.interpretType(Character.getNumericValue(level[y].charAt(x)));
			}
		}

	}

	/*
	 * TODO prepare enemies gets the list of enemies, instances e.g. { {1,2} , {2,1} , {1,2} } 2 spiders, 1 skeleton, 2 spiders in order
	 */

	public void checkForEnemySpawn(float delta) {
		spawnDelay += delta;

		if (spawnDelay >= .5 && spawnedEnemies < enemiesToBeSpawned.size()) {

			Enemy enemy = Enemy.createEnemy(EnemyType.Spider);
			enemies.add(enemy);
			spawnDelay = 0;
			++spawnedEnemies;
		}
	}

	/**
	 * This should be called after every round.
	 */
	public void prepareEnemies() {
		enemiesToBeSpawned = Level.getInstance().getEnemiesToBeSpawned();
	}

	public void deployTower(Tower tower) {
		if (canBuyTower(tower)) {
			money -= tower.getCost();
			deployedTowers.add(tower);
		}
	}

	public boolean canBuyTower(Tower tower) {
		return money >= tower.getCost();
	}

	public void setWaveSpawnTime(float waveSpawnTime) {
		if (waveSpawnTime < 0)
			this.roundTime = 30;
		else
			this.roundTime = waveSpawnTime;
	}

	public void getDamaged() {
		playerLife--;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public int getPlayerLife() {
		return playerLife;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public List<Tower> getDeployedTowers() {
		return deployedTowers;
	}

	public boolean isTowerPlaceable(Point point) {
		try {
			return point.x > 0 && point.y > 0 && grid[point.x / 40][point.y / 40] != TileType.Dirt;			
		}catch (Exception e){
			
		}
		return false;
	}

	public void buildTower(Tower towerToBuild, Point point) {
		grid[point.x / 40][point.y / 40] = TileType.Used;

		Vector2 position = MathHelper.PointToVector2(point);
		towerToBuild.setPosition(position);

		towerToBuild.setCenter((float) point.x + Config.tileSize / 2, (float) point.y + Config.tileSize / 2);
		deployTower(towerToBuild);
	}
}