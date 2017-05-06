import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static KnightPanel kp;
    private static Thread workerThread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::runGUI);
    }

    // Парсер входных данных
    private static void readInput(InputStream in) {
        Scanner sc = new Scanner(in);
        String text;
        StringBuilder nums = new StringBuilder();
        int num = 0, x = 0, y = 0;
        int brackets = 0;
        List<Pair<Integer, Point>> data, prevData = new ArrayList<>();
        while (sc.hasNext()) {
            text = sc.next();
            System.out.println(text);
            data = new ArrayList<>();
            for (char c : text.toCharArray()) {
                switch (c) {
                    case '[':
                    case ']':
                        break;
                    case '(':
                        brackets++;
                        break;
                    case ')':
                        brackets--;
                        if (brackets == 1) {
                            y = Integer.parseInt(nums.toString());
                            nums = new StringBuilder();
                        }
                        break;
                    case ',':
                        if (brackets == 0) {
                            data.add(new Pair<>(num, new Point(x, y)));
                        } else if (brackets == 1) {
                            num = Integer.parseInt(nums.toString());
                            nums = new StringBuilder();
                        } else {
                            x = Integer.parseInt(nums.toString());
                            nums = new StringBuilder();
                        }
                        break;
                    default:
                        nums.append(c);

                }
            }
            changePosition(data, prevData);
            prevData = data;
        }

    }

    // Обработчик входных данных
    private static void changePosition(List<Pair<Integer, Point>> data, List<Pair<Integer, Point>> prevData) {
        if (data.isEmpty()) return;
        // Если список посещенных вершин увеличился, нужно сдвинуть коня на новую позицию
        if (data.size() == prevData.size() + 1) {
            while (kp.isStopped()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            kp.moveKnight(data.get(0).getValue());
            // Иначе нужно вернуться к предыдущим позициям
        } else if (data.size() < prevData.size()) {
            while (kp.isMoving() || kp.isStopped()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
            }
            kp.backup(data.stream().map(Pair::getValue).collect(Collectors.toList()), data.get(0).getValue());
        }
    }

    public static void runGUI() {
        JFrame frame = new JFrame("Knight's tour");

        kp = new KnightPanel(4, 4, 10);
        frame.add(kp);


        final JButton button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            private boolean started = false;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!started) {
                    started = true;
                    button.setText("Pause");
                    if (workerThread != null && workerThread.isAlive()) {
                        kp.start();
                    } else {
                        try {
                            Process process = new ProcessBuilder("./Kn.exe").start();
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

        kp.add(button);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}



