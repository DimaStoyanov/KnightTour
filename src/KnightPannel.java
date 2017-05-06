import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class KnightPanel extends JPanel implements ActionListener {

    private final int n, m;
    private int x, y, newX, newY;
    private Timer timer;
    private List<Point> points;
    private List<Point> lazyMovePoints;
    private boolean stopped = false;

    KnightPanel(int n, int m, int delay) {
        this.n = n;
        this.m = m;
        if (n % 2 == 1 || m % 2 == 1) {
            throw new UnsupportedOperationException("Size of field should be even");
        }
        timer = new Timer(delay, this);
        points = new ArrayList<>();
        points.add(new Point(x, y));
        lazyMovePoints = new LinkedList<>();
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

    // Перемещает коня к новой точке
    private void moveKnight(int newX, int newY) {

        if (timer.isRunning()) {
            lazyMovePoints.add(new Point(newX, newY));
        } else {
            this.newX = newX;
            this.newY = newY;
            timer.start();
        }
    }

    void moveKnight(Point point) {
        moveKnight(point.x * 50, point.y * 50);
    }

    // Возвращает коня и точки, в которых он побывал к заданной позиции
    void backup(List<Point> points, Point knightPoint) {
        points = points.stream().map(p -> new Point(p.x * 50, p.y * 50)).collect(Collectors.toList());
        knightPoint = new Point(knightPoint.x * 50, knightPoint.y * 50);
        this.points = points;
        this.x = knightPoint.x;
        this.y = knightPoint.y;
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем шахматное поле
        g.fillRect(0, 0, 50 * n, 50 * m);
        g.setColor(Color.WHITE);
        for (int i = 0; i < n; i += 2) {
            for (int j = 0; j < m; j += 2) {
                g.fillRect(50 * i, 50 * j, 50, 50);
            }
        }
        for (int i = 1; i < n; i += 2) {
            for (int j = 1; j < m; j += 2) {
                g.fillRect(50 * i, 50 * j, 50, 50);
            }
        }

        // Рисуем точки, где конь уже побывал
        g.setColor(Color.BLUE);
        for (Point p : points) {
            g.fillOval(p.x + 20, p.y + 20, 10, 10);
        }

        // Риусем коня
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("knight_model2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(image, x, y, 50, 50, null);

        // Изменяем его координаты для анимации
        if (x == newX) {
            if (y == newY) {
                // Добавляем текущую точку в список тех, в которых уже побывали
                points.add(new Point(newX, newY));
                if (lazyMovePoints.isEmpty()) {
                    timer.stop();
                } else {
                    // Если закончили текущий ход, ждем небольшую паузу, и начинаем анимацию к след ходу
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    newX = lazyMovePoints.get(0).x;
                    newY = lazyMovePoints.remove(0).y;
                }
            } else {
                y += newY > y ? 5 : -5;
            }
        } else {
            x += newX > x ? 5 : -5;
        }
    }
}
