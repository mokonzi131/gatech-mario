package dk.itu.mario.level;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;

public class MyLevel extends Level {
	private static enum PLAYER_CLASS {
		ASSASSIN,
		BEGINNER,
		COLLECTOR,
		RACER
	}
	
	private static Random levelSeedRandom = new Random();
	public static long lastSeed;
	
	// Store information about the level
	public int ENEMIES = 0; // the number of enemies the level contains
	public int BLOCKS_EMPTY = 0; // the number of empty blocks
	public int BLOCKS_COINS = 0; // the number of coin blocks
	public int BLOCKS_POWER = 0; // the number of power blocks
	public int COINS = 0; // These are the coins in boxes that Mario collect

	Random random;

	private int difficulty;
	private int type;
	private int gaps;

	public MyLevel(int width, int height) {
		super(width, height);
	}

	public MyLevel(
			int width,
			int height,
			long seed,
			int difficulty,
			int type,
			GamePlay playerMetrics) {
		
		this(width, height);
		
		PLAYER_CLASS playerClass = determinePlayerClass(playerMetrics);
		creat(seed, difficulty, type, playerClass);
	}

	public void creat(long seed, int difficulty, int type, PLAYER_CLASS playerClass) {
		System.out.println("PLAYER CLASS: " + playerClass);

		this.type = type;
		this.difficulty = difficulty;

		lastSeed = seed;
		random = new Random(seed);
		
		double randomFlavor=Math.random();

		//get Player. RANDOM SELECT FOR NOW
				int player=random.nextInt(4);
				

			        //create the start location
			        int length = 0;
			        length += buildStraight(0, width, true);

				int startLength=length;
				int cliffHeight=10;
			        //create all of the medium sections
			        while (length < width - 64)
			        {
			            //length += buildZone(length, width - length);
						if(player==0){
						//assassin
							length +=buildEnemyDitch(length,width-length);
							length +=buildJump(length,width-length);
						        length += buildStraight(length, width-length, false);
							length += buildHillStraight(length, width-length);
						}
						else if(player==2){
						//racer
							if(randomFlavor<.5){
								length += buildCliff(length,width-length,cliffHeight);
								cliffHeight=cliffHeight-random.nextInt(2);
								if(cliffHeight<2){
							 	cliffHeight=2;
								}
							}
							else{
								length += buildCliff(length,width-length,3);
								
							}
						}
						else if(player==1){
						//coin collector
							length +=buildCoinCage(length,width-length);
							length += buildHillStraight(length, width-length);
							length +=buildCoinWall(length,width-length);
							length += buildHillStraight(length, width-length);
						}
						else{
						//explorer=3
						     //length += buildTubes(length, width-length);
							length += buildStraight(length, width-length, false);
							length += buildHillStraight(length, width-length);
							//length += buildJump(length, width-length);
						}
					
			        }

				
				
				int brickLength=14;
				//free powerup
				setBlock(5, height - 5, BLOCK_POWERUP);
			          BLOCKS_POWER++;
				setBlock(4, height - 5, BLOCK_POWERUP);
			          BLOCKS_POWER++;

				setBlock(5, height - 8, BLOCK_POWERUP);
			          BLOCKS_POWER++;
				setBlock(4, height - 8, BLOCK_POWERUP);
			          BLOCKS_POWER++;		
				

				//staircase if explorer
				if(player==3){
					setBlock(startLength-1, height - 6, BLOCK_EMPTY);
			                    BLOCKS_EMPTY++;
				}
				if(player==2){
				//staircase for racer
					setBlock(startLength-4, height - 6, BLOCK_POWERUP);
			                    BLOCKS_POWER++;

					setBlock(startLength-2, height - 8, BLOCK_POWERUP);
			                    BLOCKS_POWER++;
				}
				//floating objects
				while(brickLength < width - 64){
					if(player==0){
						//assassin
					brickLength +=buildGoombaWaterfall(brickLength,width-brickLength,10,randomFlavor);
					}
					else if(player==1){
						//coin collector
					brickLength +=buildCoinBox(brickLength,width-brickLength,7,randomFlavor);
					}
					else if(player==3){
						//explorer
						if(randomFlavor<.5){
						brickLength +=buildBrickCeiling(brickLength,width-brickLength,8);
						brickLength +=buildRoom(brickLength,width-brickLength,6);
						}
						else{
						int oldBrickLength=brickLength;
						brickLength +=buildBrickCeiling(brickLength+random.nextInt(3),width-brickLength,random.nextInt(9)+5);
						brickLength +=buildBrickCeiling(oldBrickLength+random.nextInt(3),width-oldBrickLength,random.nextInt(8)+5);
						}
					}
					else{ //racer
						if(randomFlavor<.5){
						brickLength +=buildBrickCeiling(brickLength,width-brickLength,10);
					        brickLength = brickLength + random.nextInt(10);
						}
						else{
						brickLength +=buildBrickCeiling(brickLength,width-brickLength,5);
					        brickLength = brickLength + random.nextInt(10);
						}
					}
				}

			        //set the end piece
			        int floor = height - 1 - random.nextInt(4);

			        xExit = length + 8;
			        yExit = floor;

			        // fills the end piece
			        for (int x = length; x < width; x++)
			        {
			            for (int y = 0; y < height; y++)
			            {
			                if (y >= floor)
			                {
			                    setBlock(x, y, GROUND);
			                }
			            }
			        }

			        if (type == LevelInterface.TYPE_CASTLE || type == LevelInterface.TYPE_UNDERGROUND)
			        {
			            int ceiling = 0;
			            int run = 0;
			            for (int x = 0; x < width; x++)
			            {
			                if (run-- <= 0 && x > 4)
			                {
			                    ceiling = random.nextInt(4);
			                    run = random.nextInt(4) + 4;
			                }
			                for (int y = 0; y < height; y++)
			                {
			                    if ((x > 4 && y <= ceiling) || x < 1)
			                    {
			                        setBlock(x, y, GROUND);
			                    }
			                }
			            }
			        }

			        fixWalls();
	}

	private void buildEndPiece(int length) {
		// set the end piece
		int floor = height - 1 - random.nextInt(4);

		xExit = length + 8;
		yExit = floor;

		// fills the end piece
		for (int x = length; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y >= floor) {
					setBlock(x, y, GROUND);
				}
			}
		}
	}

	private int buildJump(int xo, int maxLength) {
		gaps++;
		// jl: jump length
		// js: the number of blocks that are available at either side for free
		int js = random.nextInt(4) + 2;
		int jl = random.nextInt(2) + 2;
		int length = js * 2 + jl;

		boolean hasStairs = random.nextInt(3) == 0;

		int floor = height - 1 - random.nextInt(4);
		// run from the start x position, for the whole length
		for (int x = xo; x < xo + length; x++) {
			if (x < xo + js || x > xo + length - js - 1) {
				// run for all y's since we need to paint blocks upward
				for (int y = 0; y < height; y++) { // paint ground up until the
													// floor
					if (y >= floor) {
						setBlock(x, y, GROUND);
					}
					// if it is above ground, start making stairs of rocks
					else if (hasStairs) { // LEFT SIDE
						if (x < xo + js) { // we need to max it out and level
											// because it wont
											// paint ground correctly unless two
											// bricks are side by side
							if (y >= floor - (x - xo) + 1) {
								setBlock(x, y, ROCK);
							}
						} else { // RIGHT SIDE
							if (y >= floor - ((xo + length) - x) + 2) {
								setBlock(x, y, ROCK);
							}
						}
					}
				}
			}
		}

		return length;
	}

	private int buildCannons(int xo, int maxLength) {
		int length = random.nextInt(10) + 2;
		if (length > maxLength)
			length = maxLength;

		int floor = height - 1 - random.nextInt(4);
		int xCannon = xo + 1 + random.nextInt(4);
		for (int x = xo; x < xo + length; x++) {
			if (x > xCannon) {
				xCannon += 2 + random.nextInt(4);
			}
			if (xCannon == xo + length - 1)
				xCannon += 10;
			int cannonHeight = floor - random.nextInt(4) - 1;

			for (int y = 0; y < height; y++) {
				if (y >= floor) {
					setBlock(x, y, GROUND);
				} else {
					if (x == xCannon && y >= cannonHeight) {
						if (y == cannonHeight) {
							setBlock(x, y, (byte) (14 + 0 * 16));
						} else if (y == cannonHeight + 1) {
							setBlock(x, y, (byte) (14 + 1 * 16));
						} else {
							setBlock(x, y, (byte) (14 + 2 * 16));
						}
					}
				}
			}
		}

		return length;
	}

	private int buildHillStraight(int xo, int maxLength) {
		int length = random.nextInt(10) + 10;
		if (length > maxLength)
			length = maxLength;

		int floor = height - 1 - random.nextInt(4);
		for (int x = xo; x < xo + length; x++) {
			for (int y = 0; y < height; y++) {
				if (y >= floor) {
					setBlock(x, y, GROUND);
				}
			}
		}

		addEnemyLine(xo + 1, xo + length - 1, floor - 1);

		int h = floor;

		boolean keepGoing = true;

		boolean[] occupied = new boolean[length];
		while (keepGoing) {
			h = h - 2 - random.nextInt(3);

			if (h <= 0) {
				keepGoing = false;
			} else {
				int l = random.nextInt(5) + 3;
				int xxo = random.nextInt(length - l - 2) + xo + 1;

				if (occupied[xxo - xo] || occupied[xxo - xo + l]
						|| occupied[xxo - xo - 1] || occupied[xxo - xo + l + 1]) {
					keepGoing = false;
				} else {
					occupied[xxo - xo] = true;
					occupied[xxo - xo + l] = true;
					addEnemyLine(xxo, xxo + l, h - 1);
					if (random.nextInt(4) == 0) {
						decorate(xxo - 1, xxo + l + 1, h);
						keepGoing = false;
					}
					for (int x = xxo; x < xxo + l; x++) {
						for (int y = h; y < floor; y++) {
							int xx = 5;
							if (x == xxo)
								xx = 4;
							if (x == xxo + l - 1)
								xx = 6;
							int yy = 9;
							if (y == h)
								yy = 8;

							if (getBlock(x, y) == 0) {
								setBlock(x, y, (byte) (xx + yy * 16));
							} else {
								if (getBlock(x, y) == HILL_TOP_LEFT)
									setBlock(x, y, HILL_TOP_LEFT_IN);
								if (getBlock(x, y) == HILL_TOP_RIGHT)
									setBlock(x, y, HILL_TOP_RIGHT_IN);
							}
						}
					}
				}
			}
		}

		return length;
	}

	private void addEnemyLine(int x0, int x1, int y) {
		for (int x = x0; x < x1; x++) {
			if (random.nextInt(35) < difficulty + 1) {
				int type = random.nextInt(4);

				if (difficulty < 1) {
					type = Enemy.ENEMY_GOOMBA;
				} else if (difficulty < 3) {
					type = random.nextInt(3);
				}

				setSpriteTemplate(x, y,
						new SpriteTemplate(type,
								random.nextInt(35) < difficulty));
				ENEMIES++;
			}
		}
	}

	private int buildTubes(int xo, int maxLength) {
		int length = random.nextInt(10) + 5;
		if (length > maxLength)
			length = maxLength;

		int floor = height - 1 - random.nextInt(4);
		int xTube = xo + 1 + random.nextInt(4);
		int tubeHeight = floor - random.nextInt(2) - 2;
		for (int x = xo; x < xo + length; x++) {
			if (x > xTube + 1) {
				xTube += 3 + random.nextInt(4);
				tubeHeight = floor - random.nextInt(2) - 2;
			}
			if (xTube >= xo + length - 2)
				xTube += 10;

			if (x == xTube && random.nextInt(11) < difficulty + 1) {
				setSpriteTemplate(x, tubeHeight, new SpriteTemplate(
						Enemy.ENEMY_FLOWER, false));
				ENEMIES++;
			}

			for (int y = 0; y < height; y++) {
				if (y >= floor) {
					setBlock(x, y, GROUND);

				} else {
					if ((x == xTube || x == xTube + 1) && y >= tubeHeight) {
						int xPic = 10 + x - xTube;

						if (y == tubeHeight) {
							// tube top
							setBlock(x, y, (byte) (xPic + 0 * 16));
						} else {
							// tube side
							setBlock(x, y, (byte) (xPic + 1 * 16));
						}
					}
				}
			}
		}

		return length;
	}

	private int buildStraight(int xo, int maxLength, boolean safe) {
		int length = random.nextInt(10) + 2;

		if (safe)
			length = 10 + random.nextInt(5);

		if (length > maxLength)
			length = maxLength;

		int floor = height - 1 - random.nextInt(4);

		// runs from the specified x position to the length of the segment
		for (int x = xo; x < xo + length; x++) {
			for (int y = 0; y < height; y++) {
				if (y >= floor) {
					setBlock(x, y, GROUND);
				}
			}
		}

		if (!safe) {
			if (length > 5) {
				decorate(xo, xo + length, floor);
			}
		}

		return length;
	}

	private void decorate(int xStart, int xLength, int floor) {
		// if its at the very top, just return
		if (floor < 1)
			return;

		// boolean coins = random.nextInt(3) == 0;
		boolean rocks = true;

		// add an enemy line above the box
		addEnemyLine(xStart + 1, xLength - 1, floor - 1);

		int s = random.nextInt(4);
		int e = random.nextInt(4);

		if (floor - 2 > 0) {
			if ((xLength - 1 - e) - (xStart + 1 + s) > 1) {
				for (int x = xStart + 1 + s; x < xLength - 1 - e; x++) {
					setBlock(x, floor - 2, COIN);
					COINS++;
				}
			}
		}

		s = random.nextInt(4);
		e = random.nextInt(4);

		// this fills the set of blocks and the hidden objects inside them
		if (floor - 4 > 0) {
			if ((xLength - 1 - e) - (xStart + 1 + s) > 2) {
				for (int x = xStart + 1 + s; x < xLength - 1 - e; x++) {
					if (rocks) {
						if (x != xStart + 1 && x != xLength - 2
								&& random.nextInt(3) == 0) {
							if (random.nextInt(4) == 0) {
								setBlock(x, floor - 4, BLOCK_POWERUP);
								BLOCKS_POWER++;
							} else { // the fills a block with a hidden coin
								setBlock(x, floor - 4, BLOCK_COIN);
								BLOCKS_COINS++;
							}
						} else if (random.nextInt(4) == 0) {
							if (random.nextInt(4) == 0) {
								setBlock(x, floor - 4, (byte) (2 + 1 * 16));
							} else {
								setBlock(x, floor - 4, (byte) (1 + 1 * 16));
							}
						} else {
							setBlock(x, floor - 4, BLOCK_EMPTY);
							BLOCKS_EMPTY++;
						}
					}
				}
			}
		}
	}
	
	private void fixWalls() {
		boolean[][] blockMap = new boolean[width + 1][height + 1];

		for (int x = 0; x < width + 1; x++) {
			for (int y = 0; y < height + 1; y++) {
				int blocks = 0;
				for (int xx = x - 1; xx < x + 1; xx++) {
					for (int yy = y - 1; yy < y + 1; yy++) {
						if (getBlockCapped(xx, yy) == GROUND) {
							blocks++;
						}
					}
				}
				blockMap[x][y] = blocks == 4;
			}
		}
		blockify(this, blockMap, width + 1, height + 1);
	}

	private void blockify(Level level, boolean[][] blocks, int width, int height) {
		int to = 0;
		if (type == LevelInterface.TYPE_CASTLE) {
			to = 4 * 2;
		} else if (type == LevelInterface.TYPE_UNDERGROUND) {
			to = 4 * 3;
		}

		boolean[][] b = new boolean[2][2];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int xx = x; xx <= x + 1; xx++) {
					for (int yy = y; yy <= y + 1; yy++) {
						int _xx = xx;
						int _yy = yy;
						if (_xx < 0)
							_xx = 0;
						if (_yy < 0)
							_yy = 0;
						if (_xx > width - 1)
							_xx = width - 1;
						if (_yy > height - 1)
							_yy = height - 1;
						b[xx - x][yy - y] = blocks[_xx][_yy];
					}
				}

				if (b[0][0] == b[1][0] && b[0][1] == b[1][1]) {
					if (b[0][0] == b[0][1]) {
						if (b[0][0]) {
							level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
						} else {
							// KEEP OLD BLOCK!
						}
					} else {
						if (b[0][0]) {
							// down grass top?
							level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
						} else {
							// up grass top
							level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
						}
					}
				} else if (b[0][0] == b[0][1] && b[1][0] == b[1][1]) {
					if (b[0][0]) {
						// right grass top
						level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
					} else {
						// left grass top
						level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
					}
				} else if (b[0][0] == b[1][1] && b[0][1] == b[1][0]) {
					level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
				} else if (b[0][0] == b[1][0]) {
					if (b[0][0]) {
						if (b[0][1]) {
							level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
						} else {
							level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
						}
					} else {
						if (b[0][1]) {
							// right up grass top
							level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
						} else {
							// left up grass top
							level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
						}
					}
				} else if (b[0][1] == b[1][1]) {
					if (b[0][1]) {
						if (b[0][0]) {
							// left pocket grass
							level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
						} else {
							// right pocket grass
							level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
						}
					} else {
						if (b[0][0]) {
							level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
						} else {
							level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
						}
					}
				} else {
					level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
				}
			}
		}
	}

	public RandomLevel clone() throws CloneNotSupportedException {

		RandomLevel clone = new RandomLevel(width, height);

		clone.xExit = xExit;
		clone.yExit = yExit;
		byte[][] map = getMap();
		SpriteTemplate[][] st = getSpriteTemplate();

		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++) {
				clone.setBlock(i, j, map[i][j]);
				clone.setSpriteTemplate(i, j, st[i][j]);
			}
		clone.BLOCKS_COINS = BLOCKS_COINS;
		clone.BLOCKS_EMPTY = BLOCKS_EMPTY;
		clone.BLOCKS_POWER = BLOCKS_POWER;
		clone.ENEMIES = ENEMIES;
		clone.COINS = COINS;

		return clone;

	}

	private int buildCliff(int xo, int maxLength, int cliffHeight){
		int length = random.nextInt(10) + 2;

       
        	if (length > maxLength)
        		length = maxLength;

        	int floor = height-cliffHeight;

        //runs from the specified x position to the length of the segment
        for (int x = xo; x < xo + length; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    setBlock(x, y, GROUND);
                }
            }
        }
		return length;
	}

	private int buildRoom(int xo, int maxLength, int brickHeight){
		int length = random.nextInt(8) + 2;
		int boxHeight=random.nextInt(2)+3;

        	if (length > maxLength)
        	length = maxLength;
	 	int floor = height - 1;

		for(int i=xo; i<xo+length; i++){
			for(int j=floor-brickHeight-boxHeight; j<=floor-brickHeight; j++){
				if(j==floor-brickHeight || j==floor-brickHeight-boxHeight||i==xo||i==xo+length-1){
				setBlock(i,j,BLOCK_EMPTY);
				BLOCKS_EMPTY++;
				}
				
			}
		}

		setBlock(xo+(int)(length/2), floor-brickHeight-(int)(boxHeight/2), BLOCK_POWERUP);
          	BLOCKS_POWER++;

		return length;
	}
	
	private int buildCoinBox(int xo, int maxLength, int brickHeight, double rand){
		int length = random.nextInt(10) + 2;
		int boxHeight=random.nextInt(10)+3;

        	if (length > maxLength)
        	length = maxLength;
	 	int floor = height - 1;

		for(int i=xo; i<xo+length; i++){
			for(int j=floor-brickHeight-boxHeight; j<=floor-brickHeight; j++){
				if(j==floor-brickHeight || j==floor-brickHeight-boxHeight||i==xo||i==xo+length-1){
					if(rand<.333){
					setBlock(i,j,BLOCK_EMPTY);
					BLOCKS_EMPTY++;
					}
					else if(rand<.666){
						if(i!=xo+length/2){
						setBlock(i,j,BLOCK_COIN);
						BLOCKS_EMPTY++;
						}
						else{
							setBlock(i,j,BLOCK_EMPTY);
							BLOCKS_EMPTY++;
						}
					}
					else{
						setBlock(i,j,COIN);
						COINS++;
					}
				}
				else{
				setBlock(i,j,COIN);
				COINS++;
				}
			}
		}
		return length;
	}

	private int buildCoinWall(int xo, int maxLength){
		int length = random.nextInt(10) + 2;

        	if (length > maxLength)
        	length = maxLength;
	 	int floor = height - 1;

		int coinHeight=random.nextInt(10)+2;
		for(int x=xo; x<xo+length;x++){
			for(int y=0; y<coinHeight; y++){
				if(y==0){
				setBlock(x,floor-y-1,BLOCK_EMPTY);
				BLOCKS_EMPTY++;
				}
				else{
				setBlock(x,floor-y-1,COIN);
				COINS++;
				}
				
			}
		}
		return length;
	}
	
	private int buildCoinCage(int xo, int maxLength){
		int length = random.nextInt(10) + 2;

        	if (length > maxLength)
        	length = maxLength;
	 	int floor = height - 1;

		for(int i=0; i<4; i++){
			setBlock(xo,floor-i-1,BLOCK_EMPTY);
			BLOCKS_EMPTY++;
		}

		for(int i=xo+1; i<xo+length-1;i++){
			for(int y=0; y<4; y++){
				if(y==0){
				setBlock(i,floor-y-1,BLOCK_EMPTY);
				BLOCKS_EMPTY++;
				}
				else{
				setBlock(i,floor-y-1,COIN);
				COINS++;
				}	
			}
		}

		for(int i=0; i<4; i++){
			setBlock(xo+length-1,floor-i-1,BLOCK_EMPTY);
			BLOCKS_EMPTY++;
		}

		return length;
	}

	

	private int buildGoombaWaterfall(int xo, int maxLength, int brickHeight, double rand){
		int length=20;

		if(length>maxLength)
			length=maxLength;

		 int floor = height - 1;	
		for (int x = xo; x < xo + length-1; x++)
        	{
			 if(rand<.5){
			setBlock(x, floor - brickHeight, BLOCK_EMPTY);
                    	BLOCKS_EMPTY++;
			}
			else{
				if(x%4==0){
					setBlock(x, floor - brickHeight, BLOCK_EMPTY);
                    			BLOCKS_EMPTY++;
					//setBlock(x-1, floor - brickHeight+2, BLOCK_EMPTY);
                    			//BLOCKS_EMPTY++;
				}
				else{
					setBlock(x, floor - brickHeight, COIN);
                    			COINS++;
				}
			}
		}

		setBlock(xo+length-2,floor-brickHeight-1,BLOCK_EMPTY);
		BLOCKS_EMPTY++;

		for (int x = xo+1; x < xo+length-2; x++)
        	{
                type = Enemy.ENEMY_GOOMBA;
		if(rand<.5){
                	setSpriteTemplate(x, floor-brickHeight-1, new SpriteTemplate(type,false));
                	ENEMIES++;
			}
		else{	
			if(Math.random()<.5){
			setSpriteTemplate(x, floor-brickHeight-1, new SpriteTemplate(type,true));
                	ENEMIES++;
			}
			else{
			setSpriteTemplate(x, floor-brickHeight-1, new SpriteTemplate(type,false));
                	ENEMIES++;
			}
			}
        	}

		return length;
	}

	private int buildBrickCeiling(int xo, int maxLength, int brickHeight)
    	{
        int length = random.nextInt(10) + 2;

        if (length > maxLength)
        	length = maxLength;
	 int floor = height - 1;	
	for (int x = xo; x < xo + length; x++)
        	{
		setBlock(x, floor - brickHeight, BLOCK_EMPTY);
                    BLOCKS_EMPTY++;
		}

	 int randY=random.nextInt(2);
		setBlock(xo+random.nextInt(length), floor-brickHeight-5+randY, BLOCK_POWERUP);
          BLOCKS_POWER++;
		setBlock(xo+random.nextInt(length), floor-brickHeight-5+randY, BLOCK_COIN);
          BLOCKS_POWER++;
	
		randY=random.nextInt(2);
		for(int x=0; x<3; x++){
			setBlock(xo+random.nextInt(length), floor-brickHeight-5+randY, COIN);
          		COINS++;
		}
		
		if(Math.random()<.2){
		type = Enemy.ENEMY_GREEN_KOOPA;
                setSpriteTemplate(xo, floor-brickHeight-1, new SpriteTemplate(type,false));
		}
		
		return length;
	}

	private int buildEnemyDitch(int xo, int maxLength)
    {
        int length = random.nextInt(10) + 2;

        if (length > maxLength)
        	length = maxLength;

        int floor = height - 1 - random.nextInt(4);

        //runs from the specified x position to the length of the segment
	for (int y = 0; y < height; y++)
            {
                if (y >= floor-1)
                {
                    setBlock(xo, y, BLOCK_EMPTY);
			BLOCKS_EMPTY++;
                }
            }

        for (int x = xo+1; x < xo + length-1; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    setBlock(x, y, GROUND);
                }
            }
        }

	for (int y = 0; y < height; y++)
            {
                if (y >= floor-1)
                {
                    setBlock(xo+length-1, y, BLOCK_EMPTY);
			BLOCKS_EMPTY++;
                }
            }

	//Place enemies:
	
	for (int x = xo+1; x < xo+length-1; x++)
        {
                type = Enemy.ENEMY_GOOMBA;
                setSpriteTemplate(x, floor-1, new SpriteTemplate(type,false));
                ENEMIES++;
        }

   

        return length;
    }

	// Player classification //
	private static final int NUM_CLASSES = PLAYER_CLASS.values().length;
	private static final int NUM_FEATURES = GamePlay.class.getFields().length;
	private static PLAYER_CLASS determinePlayerClass(GamePlay gameplay) {
		// array representing our current player's metrics
		double[] metrics = parseMetrics(gameplay);
		
		// array representing evidence data (as determined from our previous players)
		double[][][] evidence = parseEvidence();
		
		// compute probability of belonging to the classes
		double[] probabilities = new double[NUM_CLASSES];
		for (int c = 0; c < NUM_CLASSES; ++c) {
			probabilities[c] = 1000.0;
			
			// naive bayes
			// apply likelihoods P(e | o)
			// we can ignore P(o) because we are not matching the player over time
			// we can ignore P(e*) because it is the same for each class
			for (int f = 0; f < NUM_FEATURES; ++f) {
				double value = likelihood(metrics[f], evidence[c][f][0], evidence[c][f][1]);
				probabilities[c] *= value;
			}
		}
		
		// find the class with the max probability
		int maximum = 0;
		for (int i = 0; i < NUM_CLASSES; ++i) {
			System.out.println("CLASS: " + i + " : " + probabilities[i]);
			if (probabilities[i] > probabilities[maximum])
				maximum = i;
		}
		
		// return classification
		switch(maximum) {
		case 0:
			return PLAYER_CLASS.ASSASSIN;
		case 1:
			return PLAYER_CLASS.BEGINNER;
		case 2:
			return PLAYER_CLASS.COLLECTOR;
		case 3:
			return PLAYER_CLASS.RACER;
		default:
			return PLAYER_CLASS.BEGINNER;
		}
	}
	
	private static double likelihood(double x, double mean, double variance) {
		double exponent = -1.0 * Math.pow((x - mean), 2) / (2.0 * variance);
		double term = 1.0 / Math.sqrt(2.0 * Math.PI * variance);
		double result = term * Math.exp(exponent);
		// not correct, but ignores data that has no influence on result
		if (Double.isNaN(result)) result = 1.0;
		if (result == 0.0) result = 0.000001; 
		return result;
	}
	
	private static double[][][] parseEvidence() {
		double[][][] result = new double[NUM_CLASSES][NUM_FEATURES][2];
		
		// open player data url
		String rootDirectory = MyLevel.class.getClassLoader().getResource("./types/").getPath();
		File root = new File(rootDirectory);
		String[] subDirectories = root.list();

		// for each class of player (type)
		int currentClass = 0;
		for (String subDirectory : subDirectories) {
			// add all the data to the metrics list
			File directory = new File(rootDirectory + subDirectory);
			String[] filenames = directory.list();
			
			// create an array of metrics from our resource data for that class
			double[][] metrics = new double[NUM_FEATURES][filenames.length];
			int currentFile = 0;
			for (String filename : filenames) {
				String resource = directory + File.separator + filename;
				double[] metric = parseMetrics(GamePlay.read(resource));
				for (int i = 0; i < metric.length; ++i) {
					metrics[i][currentFile] = metric[i];
				}
				++currentFile;
			}
			
			// extract evidence from the metrics
			for (int feature = 0; feature < NUM_FEATURES; ++feature) {
				result[currentClass][feature][0] = computeMean(metrics[feature]);
				result[currentClass][feature][1] = computeVariance(metrics[feature], result[currentClass][feature][0]);
			}
			
			++currentClass;
		}
		
		return result;
	}
	
	private static double computeMean(double[] list) {
		double sum = 0.0;
		for (int i = 0; i < list.length; ++i) {
			sum += list[i];
		}
		return (sum / list.length);
	}
	
	private static double computeVariance(double[] list, double mean) {
		double sum = 0.0;
		for (int i = 0; i < list.length; ++i) {
			sum += Math.pow((list[i] - mean), 2);
		}
		return (sum / list.length);
	}
	
	private static double[] parseMetrics(GamePlay gameplay) {
		Field[] fields = GamePlay.class.getFields();
		double[] metrics = new double[NUM_FEATURES];
		for (int i = 0; i < fields.length; ++i) {
			try {
				metrics[i] = fields[i].getDouble(gameplay);
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			}
		}
		return metrics;
	}
}
