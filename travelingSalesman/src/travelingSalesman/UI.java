package travelingSalesman;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UI extends JFrame implements Runnable {

	static UI frame;
	Container container;
	private JLabel percentDone;
	JPanel panel;
	private JLabel DistanceLabel;

	static Thread solver;
	static Runnable solverRun;

	Timer timer;
	int delay = 5;
	MyPanel mp;
	int number = 5;
	String DistanceTxt = "Shortest Distance: ";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Runnable swingFrame = new UI();
		solverRun = new Solver();

		Thread mainThread = new Thread(swingFrame);
		solver = new Thread(solverRun);

		mainThread.run();
	}

	public UI() {

		container = this.getContentPane();
		container.setLayout(new BorderLayout());
		mp = new MyPanel();
		mp.setBackground(Color.black);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JSpinner numberSpinner = new JSpinner();
		numberSpinner.setValue(3);
		numberSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if ((int) numberSpinner.getValue() < 3) {
					numberSpinner.setValue(3);
				}
				if ((int) numberSpinner.getValue() > 200) {
					numberSpinner.setValue(200);
				}
			}

		});

		JButton plotButton = new JButton("Plot");
		JButton GoButton = new JButton("Run");
		JLabel textLabel = new JLabel("Number of points: ");
		DistanceLabel = new JLabel("ShortestDistance: ");
		percentDone = new JLabel("%");

		plotButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				number = Integer.parseInt(numberSpinner.getValue().toString());
				if (number < 3)
					return;
				mp.points = new ArrayList<point>();
				mp.shortestLines = new ArrayList<line>();
				mp.currentLines = new ArrayList<line>();
				for (int index = 0; index < number; index++) {
					int x = (int) (Math.random() * 650 + 1);
					int y = (int) (Math.random() * 525 + 1);
					mp.points.add(new point(x, y));
				}

				Solver.setPoints(mp.points);
				updatePercent();
				updateDistance();

				mp.repaint();

			}

		});

		GoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				updateFrame();
			}

		});

		panel.add(Box.createHorizontalGlue());
		panel.add(textLabel);
		panel.add(numberSpinner);
		panel.add(Box.createHorizontalGlue());
		panel.add(DistanceLabel);
		panel.add(Box.createHorizontalGlue());
		panel.add(percentDone);
		panel.add(Box.createHorizontalGlue());
		panel.add(plotButton);
		panel.add(GoButton);

		container.add(panel, BorderLayout.NORTH);
		container.add(mp, BorderLayout.CENTER);

	}

	public void updateFrame() {
		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if (Solver.getPercentDone() == 100 || !solver.isAlive()) {
					timer.stop();
					if (Solver.getPercentDone() == 100)
						createShortestLines();

					mp.currentLines = new ArrayList<line>();
					solver = new Thread(solverRun);

				} else {
					createCurrentLines();
					createShortestLines();
				}

				updatePercent();
				updateDistance();
				mp.repaint();
			}

		};

		timer = new Timer(delay, action);
		timer.setInitialDelay(0);
		solver.start();
		timer.start();

	}

	private ArrayList<line> createLines(int[] path) {
		if (path == null)
			return null;
		ArrayList<line> lines = new ArrayList();

		for (int index = 0; index < path.length - 1; index++) {
			point p1 = mp.points.get(path[index]);
			point p2 = mp.points.get(path[index + 1]);
			// point p1 = new point(mp.points.get(path[index]).x,
			// mp.points.get(path[index]).y);
			// point p2 = new point(mp.points.get(path[index + 1]).x,
			// mp.points.get(path[index + 1]).y);

			lines.add(new line(p1.x, p1.y, p2.x, p2.y));
		}
		return lines;
	}

	private void createShortestLines() {
		mp.shortestLines = new ArrayList<line>();
		int[] shortestPath = Solver.getShortestPath();
		if (shortestPath == null)
			return;
		for (int index = 0; index < shortestPath.length - 1; index++) {
			point p1 = mp.points.get(shortestPath[index]);
			point p2 = mp.points.get(shortestPath[index + 1]);
			mp.shortestLines.add(new line(p1.x, p1.y, p2.x, p2.y));
		}
	}

	private void createCurrentLines() {
		mp.currentLines = new ArrayList<line>();
		int[] path = Solver.getCurrentPath();
		if (path == null)
			return;
		for (int index = 0; index < path.length - 1; index++) {
			point p1 = mp.points.get(path[index]);
			point p2 = mp.points.get(path[index + 1]);
			mp.currentLines.add(new line(p1.x, p1.y, p2.x, p2.y));
		}
	}

	private void clearLines() {
		mp.currentLines = new ArrayList<line>();
		mp.shortestLines = new ArrayList<line>();
	}

	private void updatePercent() {
		int percent = Solver.getPercentDone();
		System.out.println("shortestPath -- updatePercent -- " + percent);
		percentDone.setText(percent + "/" + 100 + "%");
		percentDone.update(getGraphics());

	}

	private void updateDistance() {
		int totalDist = Solver.getRecord();
		if (totalDist == Integer.MAX_VALUE)
			DistanceLabel.setText(DistanceTxt + 0);
		else
			DistanceLabel.setText(DistanceTxt + totalDist);
		DistanceLabel.update(getGraphics());

	}

	private void clearDistance() {
		DistanceLabel.setText(DistanceTxt + 0);
		DistanceLabel.update(getGraphics());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		frame = new UI();
		frame.setTitle("Traveling Salesman");
		frame.setBounds(50, 50, 700, 600);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}

class MyPanel extends JPanel {

	ArrayList<point> points = new ArrayList();
	ArrayList<line> shortestLines = new ArrayList();
	ArrayList<line> currentLines = new ArrayList();

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.CYAN);
		if (points.size() > 0) {
			for (int index = 0; index < points.size(); index++) {
				g.fillOval(points.get(index).x - 3, points.get(index).y - 3, 6, 6);
				g.drawString("(" + points.get(index).x + ", " + points.get(index).y + ")", points.get(index).x + 5,
						points.get(index).y + 5);
			}
			g.setColor(Color.white);
			for (int index = 0; index < points.size(); index++) {
				g.drawString("(" + points.get(index).x + ", " + points.get(index).y + ")", points.get(index).x + 5,
						points.get(index).y + 5);
			}
		}

		g.setColor(Color.CYAN);
		if (shortestLines.size() > 0) {
			for (int index = 0; index < shortestLines.size(); index++) {
				g.drawLine(shortestLines.get(index).x1, shortestLines.get(index).y1, shortestLines.get(index).x2,
						shortestLines.get(index).y2);
			}
		}
		g.setColor(Color.white);
		if (currentLines.size() > 0) {
			for (int index = 0; index < currentLines.size(); index++) {
				g.drawLine(currentLines.get(index).x1, currentLines.get(index).y1, currentLines.get(index).x2,
						currentLines.get(index).y2);
			}
		}

	}
}

class point {
	int x = 0;
	int y = 0;

	public point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class line {
	int x1 = 0;
	int y1 = 0;
	int x2 = 0;
	int y2 = 0;

	public line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
