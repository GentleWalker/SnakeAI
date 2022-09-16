package Mode;

import java.util.ArrayList;
import java.util.HashSet;

public class Snake {
	/**
	 * Counter anti-cycle
	 */
	public int c = 0;
	/**
	 * Serpent size
	 */
	public final static int size = 10;
	/**
	 * Map size
	 */
	public final static int map_size = 300;
	/**
	 * Snake head
	 */
	private Node first;
	/**
	 * The tail of a snake is actually the last node to go on a tail
	 */
	private Node tail;
	/**
	 * Tail tail
	 */
	private Node last;
	/**
	 * Snake data structure
	 */
	private ArrayList<Node> s = new ArrayList<Node>();
	/**
	 * There are snake nodes on the map, Snake String store
	 */
	private HashSet<String> map = new HashSet<String>();

	/**
	 * Obstacles
	 */

	private HashSet<String> obstacles = new HashSet<String>();

	/**
	 * Direction
	 */
	private int dir;// 8 6 2 4
	/**
	 * Food
	 */
	private Node food;

	public Snake() {
		//GenerateObstacles((int) Math.floor(Math.random() * 15) + 1); //Vanilla!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	public Snake(Node first, Node last, Node food, Node tail) {
		this.first = first;
		this.last = last;
		this.food = food;
		this.tail = tail;
	}

	/*
	 * * Add n to s
	 * 
	 * @param n
	 */
	private void add_Node(Node n) {
		s.add(0, n);

		first = s.get(0);
		// If the added node is not food, remove the tail

		if (!n.toString().equals(food.toString())) {
			tail = last;// record the tail
			s.remove(last);
			last = s.get(s.size() - 1);
		} else {// If yes, random food,
			food = RandomFood();
			GenerateObstacles((int) Math.floor(Math.random() * 15) + 1); // Samo za hrabre //!!!!!!!!!!!!!!
			
		}
	}

	/**
	 * Move
	 */
	public void move() {
		// Move towards top
		if (dir == 8) {
			Node n = new Node(first.getX(), first.getY() - 10);
			add_Node(n);
		}

		// Move towards right
		if (dir == 6) {
			Node n = new Node(first.getX() + 10, first.getY());
			add_Node(n);
		}

		// Move towards bottom
		if (dir == 2) {
			Node n = new Node(first.getX(), first.getY() + 10);
			add_Node(n);
		}

		// Move towards left
		if (dir == 4) {
			Node n = new Node(first.getX() - 10, first.getY());
			add_Node(n);
		}

		updataMap(s);
	}

	/**
	 * You can set the direction of the move
	 * 
	 * @param dir
	 */
	public void move(int dir) {
		this.dir = dir;
		move();
	}

	/**
	 * Determine the direction of dir can go
	 * 
	 * @param snake
	 * @param dir
	 * @return
	 */
	public boolean canMove(int dir) {
		if (dir == 8) {
			int X = first.getX();
			int Y = first.getY() - 10;

			if (Y < 10 || map.contains(X + "-" + Y) || isObstacleInFront(X, Y)) {
				return false;
			} else
				return true;
		}
		if (dir == 6) {
			int X = first.getX() + 10;
			int Y = first.getY();
			if (X > Snake.map_size || map.contains(X + "-" + Y) || isObstacleInFront(X, Y)) {
				return false;
			} else
				return true;
		}
		if (dir == 2) {
			int X = first.getX();
			int Y = first.getY() + 10;
			if (Y > Snake.map_size || map.contains(X + "-" + Y) || isObstacleInFront(X, Y)) {
				return false;
			} else
				return true;
		}
		if (dir == 4) {
			int X = first.getX() - 10;
			int Y = first.getY();
			if (X < 10 || map.contains(X + "-" + Y) || isObstacleInFront(X, Y)) {
				return false;
			} else
				return true;
		}
		return false;
	}

	/**
	 * Updated the location visited on the map
	 * 
	 * @param s
	 */
	public void updataMap(ArrayList<Node> s) {
		map.clear();// first remove the old station site
		for (Node n : s) {
			map.add(n.toString());
		}
	}

	/**
	 * Random appearance of food
	 */
	public Node RandomFood() {
		c = 0;
		while (true) {
			int x = 0, y;
			x = Snake.size * (int) (Math.random() * Snake.map_size / Snake.size) + 10; // + 10 da ne promasi marginu x
																						
			y = Snake.size * (int) (Math.random() * Snake.map_size / Snake.size) + 10; // + 10 da ne promasi marginu y
																						
			Node n = new Node(x, y);
			
			//Ovo je da ne moze da napravi hranu tamo gde je zmija niti tamo gde je prepreka niti odmah pored nje da(izbegavamo slucaj da se zakuca odmah posto pojede)
			if (obstacles.contains(n.toString()))
				continue;

			if (isNextToTheObstacle(n))
				continue;

			if (!s.contains(n)) {
				return n;
			}

		}
	}

	public String RandomObstacle() {
		while (true) {
			int x = 0, y;
			x = Snake.size * (int) (Math.random() * Snake.map_size / Snake.size) + 10; //  + 10 da ne promasi marginu x
																						
			y = Snake.size * (int) (Math.random() * Snake.map_size / Snake.size) + 10; //  + 10 da ne promasi marginu x
																						
			Node n = new Node(x, y);
			//Da ne moze da stavi prepreku u zmijino telo u slucaju da je ukljuceno generisanje zidova
			if (!s.contains(n)) {
				return n.toString();
			}
		}
	}
    //Generise random broj prepreka do 15 npr
	private void GenerateObstacles(int number) {
		obstacles.clear();

		int i = 0;
		while (i < number) {
			obstacles.add(RandomObstacle());

			i++;
		}
	}

	/**
	 * 
	 * @return snake long
	 */
	public int getLen() {
		return s.size();
	}

	/**
	 * @return the last node of the tail lsat
	 */
	public Node getTail() {
		return tail;
	}

	public void setTail(Node tail) {
		this.tail = tail;
	}

	public HashSet<String> getMap() {
		return map;
	}

	public Node getFirst() {
		return first;
	}

	public Node getLast() {
		return last;
	}

	public ArrayList<Node> getS() {
		return s;
	}

	public void setFirst(Node first) {
		this.first = first;
	}

	public void setLast(Node last) {
		this.last = last;
	}

	public void setS(ArrayList<Node> s) {
		this.s = s;
	}

	public void setMap(HashSet<String> map) {
		this.map = map;
	}

	public void setFood(Node food) {
		this.food = food;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public Node getFood() {
		return food;
	}

	public HashSet<String> getObstacles() {
		return obstacles;
	}
	// kako je obstacles string hashset ovde pravimo to sa x i y koridnatama
	public boolean isObstacleInFront(int x, int y) {
		return obstacles.contains(x + "-" + y);
	}

	public boolean isObstacleInFront(String pos) {
		return obstacles.contains(pos);
	}
    // 10 je velicina kvadrata pa samo gleda da li je odmah pored prepreke
	public boolean isNextToTheObstacle(Node node) {
		for (String current : obstacles) {
			String[] cordinates = current.split("-");
			int x = Integer.parseInt(cordinates[0]);
			int y = Integer.parseInt(cordinates[1]);

			
			if (node.getX() == x && node.getY() - 10 == y)
				return true;

		
			if (node.getX() + 10 == x && node.getY() == y)
				return true;

		
			if (node.getX() == x && node.getY() + 10 == y)
				return true;

		
			if (node.getX() - 10 == x && node.getY() == y)
				return true;
		}

		return false;
	}
}
