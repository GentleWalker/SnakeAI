package AI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import Mode.Node;
import Mode.Snake;

public class SnakeAI {
	/**
	 * @param s snake
	 * @param f target, where the target may not be food so separate parameters
	 * @return can return to the path to the first step, can not return -1;
	 */

	public int play1(Snake s, Node f) {
		Queue<Node> q = new LinkedList<Node>();
		Set<String> vis = new HashSet<String>();// Record the visited node
		Map<String, String> path = new HashMap<String, String>();
		// Record the path of the visit, and later use the A * algorithm to add the
		// father node in Node, this can be removed
		Stack<String> stack = new Stack<String>();// Snake to eat the path

		q.add(s.getFirst());
		while (!q.isEmpty()) {
			Node n = q.remove();

			if (n.getX() == f.getX() && n.getY() == f.getY()) {
				// Slucaj kada se nalazi na hrani ili kada se ""
				String state = f.toString();
				while (state != null && !state.equals(s.getFirst().toString())) {
					stack.push(state);
					state = path.get(state);
				}

				String[] str;
				if (stack.isEmpty()) {
					str = state.split("-");
				} else
					str = stack.peek().split("-");

				int x = Integer.parseInt(str[0]);
				int y = Integer.parseInt(str[1]);

				if (x > s.getFirst().getX() && y == s.getFirst().getY()) {
					return 6;
				}
				if (x < s.getFirst().getX() && y == s.getFirst().getY()) {
					return 4;
				}
				if (x == s.getFirst().getX() && y > s.getFirst().getY()) {
					return 2;
				}
				if (x == s.getFirst().getX() && y < s.getFirst().getY()) {
					return 8;
				}
			}

			String nstring = n.toString();

			Node up = new Node(n.getX(), n.getY() - Snake.size);
			Node right = new Node(n.getX() + Snake.size, n.getY());
			Node down = new Node(n.getX(), n.getY() + Snake.size);
			Node left = new Node(n.getX() - Snake.size, n.getY());

			String upString = up.toString();
			String rightString = right.toString();
			String downString = down.toString();
			String leftString = left.toString();

			if (!s.getMap().contains(upString) && !vis.contains(upString) && up.getX() <= Snake.map_size
					&& up.getX() >= 10 && up.getY() <= Snake.map_size && up.getY() >= 10
					&& !s.isObstacleInFront(upString)) {
				q.add(up);
				vis.add(upString);
				path.put(upString, nstring);
			}
			if (!s.getMap().contains(rightString) && !vis.contains(rightString) && right.getX() <= Snake.map_size
					&& right.getX() >= 10 && right.getY() <= Snake.map_size && right.getY() >= 10
					&& !s.isObstacleInFront(rightString)) {
				q.add(right);
				vis.add(rightString);
				path.put(rightString, nstring);
			}
			if (!s.getMap().contains(downString) && !vis.contains(downString) && down.getX() <= Snake.map_size
					&& down.getX() >= 10 && down.getY() <= Snake.map_size && down.getY() >= 10
					&& !s.isObstacleInFront(downString)) {
				q.add(down);
				vis.add(downString);
				path.put(downString, nstring);
			}
			if (!s.getMap().contains(leftString) && !vis.contains(leftString) && left.getX() <= Snake.map_size
					&& left.getX() >= 10 && left.getY() <= Snake.map_size && left.getY() >= 10
					&& !s.isObstacleInFront(leftString)) {
				q.add(left);
				vis.add(leftString);
				path.put(leftString, nstring);
			}
		}
		return -1;
	}

	/**
	 * Ako ne moze da se hrani zbog svog repa ili prepreke onda koristimo virSnake koji pokusava da nadje put
	 * Ako uspe salje pravu zmiju preko a* najboljim putem ili najboljom virZmijom
	 * Ako to ne uspe onda prati svoj rep u nadi da ce se osloboditi
	 * 
	 * @param s snake
	 * @param f target
	 * @return direction
	 */

	@SuppressWarnings("unchecked")
	public int play2(Snake snake, Node f) {
		Snake virSnake = new Snake(snake.getFirst(), snake.getLast(), snake.getFood(), snake.getTail());
		virSnake.setS((ArrayList<Node>) snake.getS().clone());
		virSnake.setMap((HashSet<String>) snake.getMap().clone());

		// Samo probaj da nadjes hranu preko play 1 
		int realGoTofoodDir = play1(snake, f);
		// Ako moze da pojede
		if (realGoTofoodDir != -1) {
			// Salje pravu zmiju
			while (!virSnake.getFirst().toString().equals(f.toString())) {
				virSnake.move(play1(virSnake, f));
			}
			// salje virSnake
			int goToDailDir = Asearch(virSnake, virSnake.getTail());
			// Posalji pravu zmiju
			if (goToDailDir != -1)
				return realGoTofoodDir;
			else {
				snake.c++;
				/**
				 * Kada se zakuca u loop i ne vrti se jer prati svoj rep onda ponovo zovemo Asearch
				 */
				if (snake.c < 100)
					return Asearch(snake, snake.getTail());
				else {
					return realGoTofoodDir;
				}
			}
			//Kada ne moze da se hrani
		} else {
			
			int realGoToDailDir = Asearch(snake, snake.getTail());
			if (realGoToDailDir == -1) {
				//pokusaj da ides bilo gde
				realGoToDailDir = randomDir();
				int i = 0;
				while (!snake.canMove(realGoToDailDir)) {
					// pogledaj da li moze negde da ide u nekom momentu ako ne moze i++
					realGoToDailDir = randomDir();
					i++;

					if (i > 100)
						return -1;// namerno izgubi
				}
				return realGoToDailDir;
			}
			return realGoToDailDir;
		}
	}

	/**
	 * A Find the furthest path
	 * 
	 * @param s
	 * @param f
	 * @return
	 */
	public int Asearch(Snake s, Node f) {
		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closeList = new ArrayList<Node>();
		Stack<Node> stack = new Stack<Node>();// Snake to eat the path
		openList.add(s.getFirst());// Place the start node in the open list;
		s.getFirst().setH(dis(s.getFirst(), f));

		while (!openList.isEmpty()) {
			Node now = null;
			int max = -1;
			for (Node n : openList) {//Nadji najgoru putanju, najudaljeniju ako su iste samo uzmi poslednju
				if (n.getF() >= max) {
					max = n.getF();
					now = n;
				}
			}
			// Remove the current node from the open list and add it to the closed list
			openList.remove(now);
			closeList.add(now);
			// Neighbor in four directions
			Node up = new Node(now.getX(), now.getY() - Snake.size);
			Node right = new Node(now.getX() + Snake.size, now.getY());
			Node down = new Node(now.getX(), now.getY() + Snake.size);
			Node left = new Node(now.getX() - Snake.size, now.getY());
			ArrayList<Node> temp = new ArrayList<Node>(4);
			temp.add(up);
			temp.add(right);
			temp.add(down);
			temp.add(left);
			for (Node n : temp) {
				//Ako adjacent node nije dobar ili ako je vec u listi onda nastavi dalje
				if (s.getMap().contains(n.toString()) || closeList.contains(n) || n.getX() > Snake.map_size
						|| n.getX() < 10 || n.getY() > Snake.map_size || n.getY() < 10
						|| s.isObstacleInFront(n.toString())) {
					continue;
				}

				if (!openList.contains(n)) {
					//System.out.println("Test test2 za A*");
					n.setFather(now);
					n.setG(now.getG() + 10);
					n.setH(dis(n, f));
					openList.add(n);
					//Kada nadjemo dobar node on se dodaje u openList kao putanja koja treba da se proveri
					//Ako je nadjen put prekida se loop
					//
					if (n.equals(f)) {

						Node node = openList.get(openList.size() - 1);
						while (node != null && !node.equals(s.getFirst())) {
							stack.push(node);
							node = node.getFather();
						}
						int x = stack.peek().getX();
						int y = stack.peek().getY();
						if (x > s.getFirst().getX() && y == s.getFirst().getY()) {
							return 6;
						}
						if (x < s.getFirst().getX() && y == s.getFirst().getY()) {
							return 4;
						}
						if (x == s.getFirst().getX() && y > s.getFirst().getY()) {
							return 2;
						}
						if (x == s.getFirst().getX() && y < s.getFirst().getY()) {
							return 8;
						}
					}
				}
				//Ako je adjacent u openList pogledaj da li je G veci za 10 jer gleda za ovu pored 
				//Ako jeste stavi to u parent
				if (openList.contains(n)) {
					if (n.getG() > (now.getG() + 10)) {
						n.setFather(now);
						n.setG(now.getG() + 10);
					}
				}
			}
		}
		// Kada nema drugih opcija prekini
		return -1;
	}

	/**
	 * Calculate Manhattan distance
	 * 
	 * @param src
	 * @param des
	 * @return
	 */
	public int dis(Node src, Node des) {
		return Math.abs(src.getX() - des.getX()) + Math.abs(src.getY() - des.getY());
	}

	/**
	 * Random production direction
	 * 
	 * @return
	 */
	public int randomDir() {
		int dir = (int) Math.random() * 4;
		if (dir == 0)
			return 8;
		else if (dir == 1)
			return 6;
		else if (dir == 2)
			return 2;
		else
			return 4;
	}
}
