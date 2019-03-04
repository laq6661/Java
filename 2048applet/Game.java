
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Game extends JApplet {
	private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	private static final int WID = 150, SP = 10;
	private int scores = 0;
	private JButton jb;
	private static JLabel scoreLabel;
	private boolean change, checkMode = false, gameOverFlag = false, successFlag = false;
	private int[] label = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };
	private Color[] clo = { Color.gray, Color.cyan, Color.green, Color.MAGENTA, Color.ORANGE, Color.BLUE, Color.red,
			Color.YELLOW, Color.white, Color.LIGHT_GRAY, Color.WHITE };
	private Map<Integer, Color> cmap = new HashMap<>();
	public static RectObject[][] rset = new RectObject[4][4];
	public RectObject[][] list = new RectObject[4][4];
	private My2048Panel myp;
	private MyMusic m1,bgm;

	KeyListener kl = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			if (gameOverFlag || successFlag) {
				return;
			}
			if (!aDirAble()) {
				gameOver();
			}
			int key = e.getKeyCode();
			switch (key) {
			case KeyEvent.VK_UP:
				change = false;
				moveUp(true);
				if (change) {
					m1.startMusic();
					getARandomRect();
				}
				break;
			case KeyEvent.VK_DOWN:
				change = false;
				moveDown(true);
				if (change) {
					m1.startMusic();
					getARandomRect();
				}
				break;
			case KeyEvent.VK_LEFT:
				change = false;
				moveLeft(true);
				if (change) {
					m1.startMusic();
					getARandomRect();
				}
				break;
			case KeyEvent.VK_RIGHT:
				change = false;
				moveRight(true);
				if (change) {
					m1.startMusic();
					getARandomRect();
				}
				break;
			}

		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}
	};

	
	class RectObject {
		private int value;

		public RectObject() {
			value = 0;
		}

		public RectObject(RectObject obj) {
			value = obj.value;
		}

		public boolean equals(Object inobj) {
			RectObject obj = (RectObject) inobj;
			if (obj.value == value) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return value + "";
		}
	}

	class Point {
		int x;
		int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	
	class My2048Panel extends JPanel {
		private int[] xindex = { SP, 2 * SP + WID, 3 * SP + 2 * WID, 4 * SP + 3 * WID };
		private int[] yindex = { SP, 2 * SP + WID, 3 * SP + 2 * WID, 4 * SP + 3 * WID };

		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			for (int i = 0; i < xindex.length; i++) {
				for (int j = 0; j < yindex.length; j++) {
					g.setColor(Color.WHITE);
					g.drawRect(xindex[i], yindex[j], WID, WID);
					g.setColor(Color.pink);
					g.fillRect(xindex[i], yindex[j], WID, WID);
				}
			}
			
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (rset[i][j] != null) {
						g.setColor(Color.WHITE);
						g.drawRect(yindex[j], xindex[i], WID, WID);

						g.setColor(cmap.get(rset[i][j].value));

						g.fillRect(yindex[j], xindex[i], WID, WID);
						g.setColor(Color.BLACK);

						Font font = new Font("TimesRoman", Font.BOLD, 50);
						g.setFont(font);
						FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
						int len = fm.stringWidth("" + rset[i][j].value);
						int hg = fm.getHeight();
						g.drawString("" + rset[i][j].value, yindex[j] + WID / 2 - len / 2,
								xindex[i] + WID / 2 + hg / 4);

						if (rset[i][j].value == 2048 && successFlag == false) {
							successFlag = true;
							gameSuccess();
						}
					}
				}
			}
		}
	}

	class GameOverPane extends JPanel {
		public GameOverPane(int w, int h) {
			setSize(w, h);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Font font = new Font("TimesRoman", Font.BOLD, 80);
			g.setFont(font);
			FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
			int width = fm.stringWidth("Game Over");
			int height = fm.getHeight();
			g.setColor(new Color(255, 0, 0));
			g.drawString("Game Over!", getWidth() / 2 - width / 2, getHeight() / 2 - height / 2);
		}
	}

	class SuccessPane extends JPanel {
		public SuccessPane(int w, int h) {
			setSize(w, h);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Font font = new Font("TimesRoman", Font.BOLD, 80);
			g.setFont(font);
			FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
			int width = fm.stringWidth("Success!");
			int height = fm.getHeight();
			g.setColor(new Color(255, 0, 0));
			g.drawString("Success!", getWidth() / 2 - width / 2, getHeight() / 2 - height / 2);
		}
	}

	public void restart() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				rset[i][j] = null;
			}
		}
		bgm.loopMusic();
		scoreLabel.setText("0");
		gameOverFlag = false;
		successFlag = false;
		jb.setVisible(true);
		getARandomRect();
		getARandomRect();
		repaint();
	}

	public class resetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			restart();
		}
	}

	// init
	public void init() {
		URL url = Game.class.getResource("/9439.wav");
		m1 = new MyMusic(url);
		url = Game.class.getResource("/bgm.wav");
		bgm = new MyMusic(url);
		
		for (int i = 0; i < label.length; i++) {
			cmap.put(label[i], clo[i]);
		}
		Container cp = getContentPane();
		cp.setLayout(null);
		cp.setFocusable(true);
		cp.addKeyListener(kl);

		Font font = new Font("TimesRoman", Font.BOLD, 30);
		JLabel sl = new JLabel();
		sl.setLayout(new GridLayout(2, 1));
		JLabel sllb = new JLabel("Scores");
		sllb.setFont(font);
		scoreLabel = new JLabel("0");
		scoreLabel.setFont(font);
		sl.add(sllb);
		sl.add(scoreLabel);

		myp = new My2048Panel();
		JLabel logo = new JLabel("2048");
		logo.setFont(new Font("TimesRoman", Font.BOLD, 50));
		jb = new JButton("RESET");
		jb.setFont(font);
		jb.addActionListener(new resetListener());
		jb.addKeyListener(kl);

		sl.setBounds(500, 20, 200, 80);

		logo.setBounds(10, 0, 400, 100);
		myp.setBounds(0, 90, 700, 700);

		jb.setBounds(700, 450, 150, 60);

		cp.add(sl);
		cp.add(logo);
		cp.add(myp);

		cp.add(jb);
		restart();
	}

	
	public void moveLeft(boolean flag) {
		clearList(list);
		
		for (int i = 0; i < 4; i++) {
			int k = 0;
			for (int j = 0; j < 4; j++) {
				if (rset[i][j] != null) {
					list[i][k++] = new RectObject(rset[i][j]);
				}
			}
		}

		for (int i = 0; i < 4 && flag; i++) {
			for (int j = 0; j < 3; j++) {
				if (list[i][j] != null && list[i][j + 1] != null && list[i][j].value == list[i][j + 1].value) {
					list[i][j].value *= 2;
					if (checkMode == false) {
						int sum = Integer.parseInt(scoreLabel.getText());
						sum += list[i][j].value;
						scoreLabel.setText("" + sum);
					}
					list[i][j + 1] = null;
					j++;
				}
			}
		}

		
		if (isChange()) {
			if (checkMode == false) {
				copySet(rset, list);
				moveLeft(false);
				repaint();
			}
			change = true;
		}
	}

	
	public void moveRight(boolean flag) {
		clearList(list);
		for (int i = 0; i < 4; i++) {
			int k = 3;
			for (int j = 3; j > -1; j--) {
				if (rset[i][j] != null) {
					list[i][k--] = new RectObject(rset[i][j]);
				}
			}
		}
		for (int i = 0; i < 4 && flag; i++) {
			for (int j = 3; j > 0; j--) {
				if (list[i][j] != null && list[i][j - 1] != null && list[i][j].value == list[i][j - 1].value) {
					list[i][j].value *= 2;
					if (checkMode == false) {
						int sum = Integer.parseInt(scoreLabel.getText());
						sum += list[i][j].value;
						scoreLabel.setText("" + sum);
					}
					list[i][j - 1] = null;
					j--;
				}
			}
		}

		if (isChange()) {
			if (checkMode == false) {
//				System.out.println(111);
				copySet(rset, list);
				moveRight(false);
				repaint();
			}
			change = true;
		}
	}

	
	public void moveUp(boolean flag) {
		clearList(list);
		for (int j = 0; j < 4; j++) {
			int k = 0;
			for (int i = 0; i < 4; i++) {
				if (rset[i][j] != null) {
					list[k++][j] = new RectObject(rset[i][j]);
				}
			}
		}
		for (int j = 0; j < 4 && flag; j++) {
			for (int i = 0; i < 3; i++) {
				if (list[i][j] != null && list[i + 1][j] != null && list[i][j].value == list[i + 1][j].value) {
					list[i][j].value *= 2;
					if (checkMode == false) {
						int sum = Integer.parseInt(scoreLabel.getText());
						sum += list[i][j].value;
						scoreLabel.setText("" + sum);
					}
					list[i + 1][j] = null;
					i++;
				}
			}
		}

		if (isChange()) {
			if (checkMode == false) {
				copySet(rset, list);
				moveUp(false);
				repaint();
			}
			change = true;
		}
	}

	
	public void moveDown(boolean flag) {
		clearList(list);
		for (int j = 0; j < 4; j++) {
			int k = 3;
			for (int i = 3; i > -1; i--) {
				if (rset[i][j] != null) {
					list[k--][j] = new RectObject(rset[i][j]);
				}
			}
		}
		for (int j = 0; j < 4 && flag; j++) {
			for (int i = 3; i > 0; i--) {
				if (list[i][j] != null && list[i - 1][j] != null && list[i][j].value == list[i - 1][j].value) {
					list[i][j].value *= 2;
					if (checkMode == false) {
						int sum = Integer.parseInt(scoreLabel.getText());
						sum += list[i][j].value;
						scoreLabel.setText("" + sum);
					}
					list[i - 1][j] = null;
					i--;
				}
			}
		}

		if (isChange()) {
			if (checkMode == false) {
				copySet(rset, list);
				moveDown(false);
				repaint();
			}
			change = true;
		}
	}

	
	private void copySet(RectObject[][] dst, RectObject[][] src) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				dst[i][j] = src[i][j];
			}
		}
	}

	
	private boolean isChange() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (rset[i][j] != null && list[i][j] != null && !rset[i][j].equals(list[i][j])) {
					return true;
				}
				if (rset[i][j] != null && list[i][j] == null) {
					return true;
				}
				if (rset[i][j] == null && list[i][j] != null) {
					return true;
				}
			}
		}
		return false;
	}

	private void clearList(RectObject[][] s) {
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < s[i].length; j++) {
				s[i][j] = null;
			}
		}
	}

	
	public void getARandomRect() {
		
		ArrayList<Point> list = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (rset[i][j] == null) {
					list.add(new Point(i, j));
				}
			}
		}

		if (list.size() == 0 && !aDirAble()) {
			gameOver();
			return;
		}
		Random rand = new Random();
		int index = rand.nextInt(list.size());
		Point loc = list.get(index);
		index = rand.nextInt(2);
		rset[loc.x][loc.y] = new RectObject();
		if (index == 1) {
			rset[loc.x][loc.y].value = 4;
		} else {
			rset[loc.x][loc.y].value = 2;
		}
	}

	
	public boolean aDirAble() {
		checkMode = true;
		change = false;
		moveLeft(true);
		moveRight(true);
		moveDown(true);
		moveUp(true);
		checkMode = false;
		if (change) {
			return true;
		} else {
			return false;
		}
	}

	public void gameOver() {
		bgm.stopMusic();
		jb.setVisible(false);
		gameOverFlag = true;
		JPanel jl = new GameOverPane(myp.getWidth(), myp.getHeight());
		jl.setBounds(0, 0, 700, 700);
		JButton jb1 = new JButton("Again");
		Font font = new Font("TimesRoman", Font.BOLD, 30);
		jb1.setOpaque(false);
		jb1.setFont(font);
//		JButton jb2 = new JButton("Close");
//		jb2.setSize(jb1.getSize());
//		jb2.setOpaque(false);
//		jb2.setFont(font);
		jb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myp.remove(jl);
				myp.validate();
				restart();
			}
		});
//		jb2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		});
		jl.add(jb1);
//		jl.add(jb2);
		myp.add(jl);
		jl.validate();
	}

	public void gameSuccess() {
		bgm.stopMusic();
		jb.setVisible(false);
		JPanel jl = new SuccessPane(myp.getWidth(), myp.getHeight());
		jl.setOpaque(false);
		jl.setBounds(0, 0, 700, 700);
		JButton jb1 = new JButton("Continue");
		Font font = new Font("TimesRoman", Font.BOLD, 30);
		jb1.setOpaque(false);
		jb1.setFont(font);
//		JButton jb2 = new JButton("Close");
//		jb2.setSize(jb1.getSize());
//		jb2.setOpaque(false);
//		jb2.setFont(font);
		jb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myp.remove(jl);
				myp.validate();
				restart();
				repaint();
			}
		});
//		jb2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		});
		jl.add(jb1);
//		jl.add(jb2);
		myp.add(jl);
		jl.validate();
	}

}
