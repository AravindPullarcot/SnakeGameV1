import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int Screen_Width = 600;
    static final int Screen_Height = 600;
    static final int Unit_Size = 25;
    static final int Game_Units = (Screen_Width * Screen_Height) / Unit_Size;
    static final int Delay = 90; // higher number of delays the slower the game is

    final int x[] = new int[Game_Units]; // snake is not gonna be bigger than the box
    final int y[] = new int[Game_Units];
    int bodyParts = 6; // Begin with the snake having 6 body parts or squares
    int cherrysEaten;
    int cherryX;
    int cherryY;
    char direction = 'R'; // snake going 'R'ight during start of the game
    boolean running = false;
    boolean restart = false; // For game over method
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(Screen_Width, Screen_Height));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        bodyParts = 6;
        cherrysEaten = 0;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        for (int i = 1; i < bodyParts; i++) {
            x[i] = x[0] - (i * Unit_Size);
            y[i] = y[0];
        }
        newCherry();
        running = true;
        restart = false;
        timer = new Timer(Delay, this);
        timer.start();
    }

    public void newCherry() {
        cherryX = random.nextInt((int)(Screen_Width / Unit_Size)) * Unit_Size;
        cherryY = random.nextInt((int)(Screen_Height / Unit_Size)) * Unit_Size;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // For Grid Lines (optional, for debugging)
            // for (int i = 0; i < ((Screen_Height) / Unit_Size); i++) {
            //     g.drawLine(i * Unit_Size, 0, i * Unit_Size, Screen_Height);
            //     g.drawLine(0, i * Unit_Size, Screen_Width, i * Unit_Size);
            // }
            g.setColor(Color.green);
            g.fillOval(cherryX, cherryY, Unit_Size, Unit_Size);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.red);
                    g.fillRect(x[i], y[i], Unit_Size, Unit_Size);
                } else {
                    g.setColor(Color.red); // RGB value
                    g.fillRect(x[i], y[i], Unit_Size, Unit_Size);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("SCORE: " + cherrysEaten, (Screen_Width - metrics.stringWidth("SCORE: " + cherrysEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - Unit_Size;
                break;
            case 'D':
                y[0] = y[0] + Unit_Size;
                break;
            case 'L':
                x[0] = x[0] - Unit_Size;
                break;
            case 'R':
                x[0] = x[0] + Unit_Size;
                break;
        }
    }

    public void checkCherry() {
        if ((x[0] == cherryX) && (y[0] == cherryY)) {
            bodyParts++;
            cherrysEaten++;
            newCherry(); // generate a new cherry after snake devours the cherry
        }
    }

    public void checkCollision() {
        // Check if head of snake touches the body of snake
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) { // Game over method head of the snake touched the with body
                running = false;
            }
        }
        // check if head touches right border
        if (x[0] >= Screen_Width) {
            running = false;
        }
        // check if head touches left border
        if (x[0] < 0) { // less than 0 because equal to zero would be starting of the snake
            running = false;
        }
        // check if head touches top border
        if (y[0] >= Screen_Height) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] < 0) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (Screen_Width - metrics1.stringWidth("GAME OVER")) / 2, Screen_Height / 2); // y axis should be divided by 2

        // Score text
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + cherrysEaten, (Screen_Width - metrics2.stringWidth("SCORE: " + cherrysEaten)) / 2, g.getFont().getSize());

        // Restart message
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Enter to Restart", (Screen_Width - metrics3.stringWidth("Press Enter to Restart")) / 2, Screen_Height / 2 + 50);

        restart = true;
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER: // Handle restart
                    if (!running && restart) {
                        startGame();
                    }
                    break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCherry();
            checkCollision();
        }
        repaint();
    }
}
