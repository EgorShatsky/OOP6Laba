import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FractalExplorer {
    private int displaySize;
    private JImageDisplay display;
    private FractalGenerator fractal;
    private Rectangle2D.Double range;
    private int rowRemaining;

    private JButton saveButton;
    private JButton resetButton;
    private JComboBox comboBox;

    public FractalExplorer(int size) {
        displaySize = size;                                        // Установка размера экрана

        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();                          // Создание комплексной плоскости

        fractal.getInitialRange(range);                            // Считывание диапозона
        display = new JImageDisplay(displaySize, displaySize);     // Создание изображения
    }

    public void createAndShowGUI() {
        display.setLayout(new BorderLayout());

        resetButton = new JButton("Reset");                     // Добавляем кнопоку Reset
        Resetter resetHandler = new Resetter();
        resetButton.addActionListener(resetHandler);

        saveButton = new JButton("Save");                       // Добавляем кнопку Save
        Saver saveHandler = new Saver();
        saveButton.addActionListener(saveHandler);

        Clicker click = new Clicker();
        display.addMouseListener(click);

        FractalGenerator mandelbrotFractal = new Mandelbrot();               // Cоздание обьектов классов
        FractalGenerator tricornFractal = new Tricorn();
        FractalGenerator burningShipFractal = new BurningShip();

        comboBox = new JComboBox();                               // Создание  комбобокса

        comboBox.addItem(mandelbrotFractal);                                // Добавение элементов комбобокса
        comboBox.addItem(tricornFractal);
        comboBox.addItem(burningShipFractal);

        Chooser fractalChooser = new Chooser();
        comboBox.addActionListener(fractalChooser);

        JLabel label = new JLabel("Fractal:");                          // Текст

        JPanel panel = new JPanel();                                         //Добавление Label на панель
        panel.add(label);
        panel.add(comboBox);

        JPanel myBottomPanel = new JPanel();                                 // Нижняя панель
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);

        JFrame myFrame = new JFrame("Fractal Explorer");                // Название окна

        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);                     // Задаем расположение
        myFrame.add(display, BorderLayout.CENTER);
        myFrame.add(panel, BorderLayout.NORTH);

        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);
    }

    private void drawFractal() {                            // Отрисовка фрактала на фоне
        enableUI(false);                                // Отключение всех элементов пользовательского интерфейса во время рисования фрактала

        rowRemaining = displaySize;                                // Установить значения «rows remaining» равным общему количеству строк, которые нужно нарисовать.

        for (int y = 0; y < displaySize; y++){
            FractalWorker drawRow = new FractalWorker(y);    // Создание рабочего обьекта дял каждой строки
            drawRow.execute();                              // Это действие запустит фоновый поток и запустит задачу в фоновом режиме.
        }
    }
    private void enableUI(boolean value) {
        comboBox.setEnabled(value);                         // Включение или отключение кнопки с выпадающим списком в пользовательском интерфейсе на основе указанного параметра
        resetButton.setEnabled(value);
        saveButton.setEnabled(value);
    }

    private class Resetter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Reset")) {
                fractal.getInitialRange(range);             // Сброс диапазона и перерисовывка фрактала
                drawFractal();
            }
        }
    }

    private class Chooser implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();                                  // Метод getsource возвращает источник события, это необходимо для того, чтобы понять, что произошло в комбобоксе
            if (source instanceof JComboBox) {
                JComboBox comboBox = (JComboBox) source;                   // Преобразование для того что бы дальше работать как с обьектом а не как с ссылкой

                fractal = (FractalGenerator) comboBox.getSelectedItem();    // Передаем в фрактал генератор тот элемент который мы выбрали
                assert fractal != null;                                     // Убеждаемся что он что то содержит

                fractal.getInitialRange(range);                              // Узнаем размер
                drawFractal();                                              //  Рисуем фрактал
            }
        }
    }

    private class Saver implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Save")) {                      //Save - команда действия эту команду мы считываем с помощью getActionCommand
                JFileChooser fileChooser = new JFileChooser();

                FileFilter extensionFilter = new FileNameExtensionFilter(   //для того что бы отображались файлы только с указанными типами, в методический указаниях есть указание использовать тип png
                        "PNG",
                        "png"
                );

                fileChooser.setFileFilter(extensionFilter);

                fileChooser.setAcceptAllFileFilterUsed(false);

                int userSelection = fileChooser.showSaveDialog(display);             //открывает окно Save File

                if (userSelection == JFileChooser.APPROVE_OPTION) {                  //если пользователь выбрал файл записываем его в строку
                    java.io.File file = fileChooser.getSelectedFile();
                    String filePath = file.getPath();

                    if (!filePath.contains(".png"))
                        file = new File(filePath + ".png"); //не указал расширение файла, добавляется автоматически
                    try {                                                                        //try – определяет блок кода, в котором может произойти исключение;
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    } catch (Exception exception) {                                                //catch – определяет блок кода, в котором происходит обработка исключения;
                        JOptionPane.showMessageDialog(display,
                                exception.getMessage(), "Cannot Save Image",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else return;
            }
        }
    }

    private class Clicker extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e)                                      //Увеличение фрактала по щелчку мыши
        {
            int x = e.getX();
            double xCoord = FractalGenerator.getCoord(range.x,
                    range.x + range.width, displaySize, x);

            int y = e.getY();
            double yCoord = FractalGenerator.getCoord(range.y,
                    range.y + range.height, displaySize, y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }
    private class FractalWorker extends SwingWorker<Object, Object> {
        int row;
        int[] rgbValues;

        private FractalWorker(int row) { this.row = row; }

        @Override
        protected Object doInBackground() {                         //Метод doInBackground() вызывается в фоновом потоке и отвечает за выполнение длительной задачи.
            /*
            Вместо того, чтобы рисовать изображение в окне, цикл должен
            будет сохранить каждое значение RGB в соответствующем элементе
            целочисленного массива. Вы не сможете изменять отображение из этого
            потока, потому что вы нарушите ограничения ограничения потоков Swing.
             */
            rgbValues = new int[displaySize];

            for (int x = 0; x < rgbValues.length; x++) {             // Цикл прохода по всем пикселям строки для которых мы и устанавливаем цвет
                double xCoord = FractalGenerator.getCoord(range.x,
                        range.x + range.width, displaySize, x);

                double yCoord = FractalGenerator.getCoord(range.y,
                        range.y + range.height, displaySize, row);

                int iteration = fractal.numIterations(xCoord, yCoord);

                if (iteration == -1) {
                    display.drawPixel(x, row, 0);
                } else {
                    float hue = 0.5f + (float) iteration / 50;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    rgbValues[x] = rgbColor;
                }
            }
            return null;
        }

        protected void done() {
            for (int x = 0; x < rgbValues.length; x++) {
                display.drawPixel(x, row, rgbValues[x]);               // Вычисление строки
            }

            display.repaint(0, 0, row, displaySize, 1);  // Указываем область для перерисовки.

            rowRemaining--;
            if (rowRemaining == 0) enableUI(true);
        }
    }
    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}