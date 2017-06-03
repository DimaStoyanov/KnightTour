import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class KnightPanel extends JPanel implements ActionListener {

    private int n;
    private int x, y, newX, newY;
    private Timer timer;
    private List<Point> points;
    private List<Action> actions;
    private boolean stopped = false;

    KnightPanel(int n, int delay) {
        this.n = n;
        timer = new Timer(delay, this);
        points = new ArrayList<>();
        points.add(new Point(x, y));
        actions = new LinkedList<>();
    }

    boolean isMoving() {
        return timer.isRunning();
    }

    boolean isStopped() {
        return stopped;
    }

    void stop() {
        stopped = true;
        timer.stop();
    }

    void start() {
        stopped = false;
        timer.start();
    }

    void setFieldSize(int n) {
        this.n = n;
        points.clear();
        repaint();
    }

    void setKnight(Point p) {
        points.clear();
        addAction(new Action(p));
    }

    void addAction(Action action) {
        actions.add(action);
        if (!timer.isRunning()) timer.start();
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем шахматное поле
        g.fillRect(0, 0, 50 * n, 50 * n);
        g.setColor(Color.WHITE);
        for (int i = 0; i < n; i += 2) {
            for (int j = 0; j < n; j += 2) {
                g.fillRect(50 * i, 50 * j, 50, 50);
            }
        }
        for (int i = 1; i < n; i += 2) {
            for (int j = 1; j < n; j += 2) {
                g.fillRect(50 * i, 50 * j, 50, 50);
            }
        }

        // Рисуем точки, где конь уже побывал
        g.setColor(Color.BLUE);
        g.setFont(new Font("TimesRoman", Font.BOLD, 24));
        for (int i = 0; i < points.size(); i++) {

            g.drawString(((i + 1) < 10 ? " " : "") + String.valueOf(i + 1), points.get(i).x + 10, points.get(i).y + 30);
//            g.fillOval(p.x + 10, p.y+ 10, 25, 25);
        }

        // Риусем коня
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("knight_model.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(image, x, y, 50, 50, null);
        if (stopped) return;
        // Изменяем его координаты для анимации
        if (x == newX) {
            if (y == newY) {
                // Добавляем текущую точку в список тех, в которых уже побывали
                if (!points.contains(new Point(newX, newY)) && (actions.isEmpty()
                        || !(actions.get(0).type == Action.Type.MOVE && points.isEmpty())))
                    points.add(new Point(newX, newY));
                if (actions.isEmpty()) {
                    timer.stop();
                } else {
                    // Если закончили текущий ход, ждем небольшую паузу, и начинаем анимацию к след ходу
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    switch (actions.get(0).type) {
                        case BACKUP:
                            newX = x = actions.get(0).knightPoint.x;
                            newY = y = actions.get(0).knightPoint.y;
                            System.out.println("Backup " + actions.get(0).knightPoint);
                            points = actions.get(0).backupPoints;
                            actions.remove(0);
                            repaint();
                            break;
                        case MOVE:
                            newX = actions.get(0).knightPoint.x;
                            newY = actions.get(0).knightPoint.y;
                            actions.remove(0);

                    }
                }
            } else {
                y += newY > y ? 10 : -10;
            }
        } else {
            x += newX > x ? 10 : -10;
        }
    }
}
