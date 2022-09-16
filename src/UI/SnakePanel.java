package UI;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;

import javax.swing.JPanel;

import AI.SnakeAI;
import Mode.Node;
import Mode.Snake;

@SuppressWarnings("serial")
public class SnakePanel extends JPanel implements Runnable {
	Snake snake;
	SnakeAI ai;

	public SnakePanel() {
		snake = new Snake();
		Node n = new Node(10, 10); // the initial position of the snake
		snake.getS().add(n);
		snake.setFirst(n);
		snake.setLast(n);
		snake.setTail(new Node(0, 10)); // last node
		snake.setFood(new Node(80, 80)); // the initial position of the food
		ai = new SnakeAI();

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.ORANGE);
		g.drawRect(10, 10, Snake.map_size, Snake.map_size); // map range

		g.setColor(Color.CYAN);
		paintObstacles(g, snake.getObstacles());

		g.setColor(Color.WHITE);
		paintSnake(g, snake);

		paintFood(g, snake.getFood());

		g.setColor(Color.CYAN);
		g.drawString("Score: " + snake.getLen(), 10, 10);

		int dir = ai.play2(snake, snake.getFood());
		if (dir == -1) {
			g.setColor(Color.CYAN);
			g.drawString("GG! Rezultat je " + snake.getLen(), Snake.map_size / 2, Snake.map_size / 2); //kraj igre 
		} else {
			snake.move(dir);
		}
	}

	@SuppressWarnings("static-access")
	public void paintSnake(Graphics g, Snake snake) {
		for (Node n : snake.getS()) {
			if (n.toString().equals(snake.getFirst().toString())) {
				g.setColor(Color.GREEN); 
			}
			if (n.toString().equals(snake.getLast().toString())
					&& !snake.getFirst().toString().equals(snake.getLast().toString())) {
				g.setColor(Color.BLUE); 
			}
			g.fillRect(n.getX(), n.getY(), snake.size, snake.size);
			g.setColor(Color.WHITE);
		}
	}


	@SuppressWarnings("static-access")
	public void paintFood(Graphics g, Node food) {
		g.setColor(Color.RED);
		g.fillRect(food.getX(), food.getY(), snake.size, snake.size);
	}

	@SuppressWarnings("static-access")
	public void paintObstacles(Graphics g, HashSet<String> obstacles) {
		for (String n : obstacles) {
			g.setColor(Color.CYAN);

			String[] cords = n.split("-");

			g.fillRect(Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), snake.size, snake.size);
		}

		g.setColor(Color.WHITE);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(3); // Brzina 40 je dobra //1 za test
				this.repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
