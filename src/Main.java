import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static KnightPanel kp;
    private static int n = 10;
    private static Point knight = new Point(0,0);
    private static Thread workerThread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::runGUI);
    }

    // Парсер входных данных
    private static void readInput(InputStream in) {
        Scanner sc = new Scanner(in);
        String text;
        String[] nums;
        Point lastPoint;
        boolean lastBackup = false;
        ArrayList<Point> points = new ArrayList<>();
        points.add(knight);

        while (sc.hasNext()) {
            text = sc.next();
            if (text.equals("-")) {
                lastBackup = true;
                points.remove(points.size() - 1);
            } else {
                if (lastBackup) {
                    //noinspection unchecked
                    kp.addAction(new Action((List<Point>) points.clone(), points.get(points.size() - 1)));
                    lastBackup = false;
                }
                text = sc.next();
                text = text.substring(1, text.length() - 1);
                nums = text.split(",");
                lastPoint = new Point(Integer.parseInt(nums[0]) * 50, Integer.parseInt(nums[1]) * 50);
                points.add(lastPoint);
                kp.addAction(new Action(lastPoint));
            }
        }

    }


    public static void runGUI() {
        JFrame frame = new JFrame("Knight's tour");

        kp = new KnightPanel(n, 10);
        frame.add(kp);
        kp.setLayout(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JTextField size = new JTextField("10");

        JButton confirmButton = new JButton("Confirm");
        ActionListener actionListener = actionEvent -> {
            try {
                n = Integer.parseInt(size.getText());
                if (n > 15) {
                    System.err.println("Max size is 15");
                    n = 8;
                }
                if (n < 2) {
                    System.err.println("Min size is 2");
                    n = 2;
                }
                kp.setFieldSize(n);
                frame.setSize(Math.max(250, n * 50), (n + 1) * 50);
                kp.repaint();
            } catch (NumberFormatException e) {
                System.err.println("Incorrect size!");

            }
        };
        confirmButton.addActionListener(actionListener);
        size.addActionListener(actionListener);

        buttonsPanel.add(new JLabel("Size: "));
        buttonsPanel.add(size);
        buttonsPanel.add(confirmButton);


        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Point p = mouseEvent.getPoint();
                if (p.x < 50 * n && p.y < 50 * n) {
                    knight = new Point(p.x - p.x % 50, p.y - p.y % 50);
                    kp.setKnight(knight);
                }
            }
        };
        kp.addMouseListener(mouseListener);

        final JButton button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            private boolean started = false;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!started) {
                    started = true;
                    button.setText("Pause");
                    confirmButton.setEnabled(false);
                    kp.removeMouseListener(mouseListener);
                    size.removeActionListener(actionListener);
                    if (workerThread != null && workerThread.isAlive()) {
                        kp.start();
                    } else {
                        try {
                            Process process = new ProcessBuilder("./Knights.exe", String.valueOf(knight.x / 50),
                                    String.valueOf(knight.y / 50), String.valueOf(n)).start();
                            workerThread = new Thread(() -> readInput(process.getErrorStream()));
                            workerThread.start();
                            kp.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    started = false;
                    button.setText("Resume");
                    kp.stop();
                }
            }
        });

        buttonsPanel.add(button);
        kp.add(buttonsPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(n * 50, (n + 1) * 50);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}



