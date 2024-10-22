import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Run_Game extends JPanel {

    // Колір фону ігрового поля
    private static final Color BACKGROUND_COLOR = new Color(0xDBC3AD);

    // Шрифт для відображення тексту
    private static final String FONT_NAME = "Montserrat Alternates";

    // Розмір плитки
    private static final int TILE_SIZE = 64;

    // Відстань між плитками
    private static final int TILE_MARGIN = 16;

    // Змінні для відстеження стану гри
    private int score = 0; // Рахунок
    private boolean isWin = false; // Чи виграв гравець
    private boolean isGameOver = false; // Чи програв гравець

    // Масив плиток для ігрового поля
    private Tile[] gameTiles;

    // Конструктор, що ініціалізує ігрове поле
    public Run_Game() {
        setPreferredSize(new Dimension(340, 400));  // Встановлюємо розмір вікна
        setFocusable(true);  // Фокус на вікно для обробки подій
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    restartGame();  // Скидання гри при натисканні ESC
                }
                if (!canMove()) {
                    isGameOver = true;  // Якщо неможливо більше рухатись, програємо
                }

                if (!isWin && !isGameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();  // Переміщення плиток вліво
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();  // Переміщення плиток вправо
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();  // Переміщення плиток вниз
                            break;
                        case KeyEvent.VK_UP:
                            moveUp();  // Переміщення плиток вгору
                            break;
                    }
                }

                if (!isWin && !canMove()) {
                    isGameOver = true;  // Якщо не можемо більше рухатись, програємо
                }

                repaint();  // Оновлюємо вікно після кожного ходу
            }
        });
        restartGame();  // Скидання гри при першому запуску
    }

    // Метод для скидання гри
    private void restartGame() {
        score = 0;  // Скидаємо рахунок
        isWin = false;  // Скидаємо перемогу
        isGameOver = false;  // Скидаємо поразку
        gameTiles = new Tile[4 * 4];  // Ініціалізуємо масив плиток
        for (int i = 0; i < gameTiles.length; i++) {
            gameTiles[i] = new Tile();  // Створюємо порожні плитки
        }
        addRandomTile();  // Додаємо першу плитку
        addRandomTile();  // Додаємо другу плитку
    }

    // Метод для переміщення плиток вліво
    public void moveLeft() {
        boolean needNewTile = false;
        for (int i = 0; i < 4; i++) {
            Tile[] row = getRow(i);  // Отримуємо лінію (рядок) плиток
            Tile[] mergedRow = mergeRow(shiftRow(row));  // Спочатку рухаємо, потім об'єднуємо плитки
            setRow(i, mergedRow);  // Встановлюємо оновлену лінію плиток
            if (!needNewTile && !compareRows(row, mergedRow)) {
                needNewTile = true;  // Якщо були зміни, додаємо нову плитку
            }
        }

        if (needNewTile) {
            addRandomTile();  // Додаємо нову плитку, якщо були зміни
        }
    }

    // Метод для переміщення плиток вправо
    public void moveRight() {
        gameTiles = rotateGrid(180);  // Повертаємо поле на 180 градусів
        moveLeft();  // Використовуємо метод переміщення вліво
        gameTiles = rotateGrid(180);  // Повертаємо поле назад
    }

    // Метод для переміщення плиток вгору
    public void moveUp() {
        gameTiles = rotateGrid(270);  // Повертаємо поле на 270 градусів
        moveLeft();  // Використовуємо метод переміщення вліво
        gameTiles = rotateGrid(90);  // Повертаємо поле назад
    }

    // Метод для переміщення плиток вниз
    public void moveDown() {
        gameTiles = rotateGrid(90);  // Повертаємо поле на 90 градусів
        moveLeft();  // Використовуємо метод переміщення вліво
        gameTiles = rotateGrid(270);  // Повертаємо поле назад
    }

    // Метод для отримання плитки на координатах (x, y)
    private Tile getTileAt(int x, int y) {
        return gameTiles[x + y * 4];
    }

    // Метод для додавання нової плитки
    private void addRandomTile() {
        List<Tile> availableSpaces = getAvailableSpaces();  // Отримуємо всі доступні порожні місця
        if (!availableSpaces.isEmpty()) {
            int index = (int) (Math.random() * availableSpaces.size()) % availableSpaces.size();
            Tile emptyTile = availableSpaces.get(index);  // Вибираємо випадкову порожню плитку
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;  // Додаємо нову плитку 2 або 4
        }
    }

    // Метод для отримання доступних порожніх місць
    private List<Tile> getAvailableSpaces() {
        final List<Tile> availableSpaces = new ArrayList<>(16);  // Створюємо список для порожніх місць
        for (Tile t : gameTiles) {
            if (t.isEmpty()) {
                availableSpaces.add(t);  // Додаємо порожні плитки до списку
            }
        }
        return availableSpaces;
    }

    // Метод для перевірки, чи заповнене поле
    private boolean isGridFull() {
        return getAvailableSpaces().isEmpty();  // Перевіряємо, чи немає вільних місць
    }

    // Метод для перевірки, чи можливий рух
    boolean canMove() {
        if (!isGridFull()) {
            return true;  // Якщо є порожні місця, рух можливий
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Tile currentTile = getTileAt(x, y);
                if ((x < 3 && currentTile.value == getTileAt(x + 1, y).value)
                        || (y < 3 && currentTile.value == getTileAt(x, y + 1).value)) {
                    return true;  // Якщо можна об'єднати плитки, рух можливий
                }
            }
        }
        return false;
    }

    // Метод для порівняння двох ліній плиток
    private boolean compareRows(Tile[] row1, Tile[] row2) {
        if (row1 == row2) {
            return true;
        } else if (row1.length != row2.length) {
            return false;
        }

        for (int i = 0; i < row1.length; i++) {
            if (row1[i].value != row2[i].value) {
                return false;
            }
        }
        return true;
    }

    // Метод для повороту поля на певний кут
    private Tile[] rotateGrid(int angle) {
        Tile[] rotatedTiles = new Tile[4 * 4];  // Створюємо новий масив для повороту
        int offsetX = 3, offsetY = 3;
        if (angle == 90) {
            offsetY = 0;
        } else if (angle == 270) {
            offsetX = 0;
        }

        double radianAngle = Math.toRadians(angle);  // Конвертуємо кут у радіани
        int cos = (int) Math.cos(radianAngle);
        int sin = (int) Math.sin(radianAngle);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                rotatedTiles[newX + newY * 4] = getTileAt(x, y);  // Повертаємо нові координати плитки
            }
        }
        return rotatedTiles;
    }

    // Метод отримує лінію (рядок) плиток
    private Tile[] getRow(int index) {
        Tile[] row = new Tile[4];  // Створюємо масив для збереження лінії
        for (int i = 0; i < 4; i++) {
            row[i] = getTileAt(i, index);  // Повертає плитки з одного рядка
        }
        return row;
    }

    // Метод для встановлення лінії плиток
    private void setRow(int index, Tile[] row) {
        System.arraycopy(row, 0, gameTiles, index * 4, 4);  // Встановлює рядок плиток на полі
    }

    // Метод для руху лінії плиток
    private Tile[] shiftRow(Tile[] row) {
        LinkedList<Tile> shiftedRow = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            if (!row[i].isEmpty()) {
                shiftedRow.addLast(row[i]);  // Додаємо плитки, які не порожні, до списку
            }
        }
        if (shiftedRow.size() == 0) {
            return row;  // Якщо всі плитки порожні, повертаємо старий рядок
        } else {
            Tile[] newRow = new Tile[4];
            ensureRowSize(shiftedRow, 4);  // Забезпечуємо необхідний розмір лінії
            for (int i = 0; i < 4; i++) {
                newRow[i] = shiftedRow.removeFirst();  // Повертаємо новий рядок плиток
            }
            return newRow;
        }
    }

    // Метод для злиття плиток в лінії
    private Tile[] mergeRow(Tile[] row) {
        LinkedList<Tile> mergedRow = new LinkedList<>();
        for (int i = 0; i < 4 && !row[i].isEmpty(); i++) {
            int currentValue = row[i].value;
            if (i < 3 && row[i].value == row[i + 1].value) {
                currentValue *= 2;
                score += currentValue;  // Додаємо до рахунку
                int target = 2048;
                if (currentValue == target) {
                    isWin = true;  // Встановлюємо прапорець перемоги, якщо досягли 2048
                }
                i++;  // Пропускаємо наступну плитку, якщо об'єднали дві однакові
            }
            mergedRow.add(new Tile(currentValue));  // Додаємо нову плитку з об'єднаним значенням
        }
        if (mergedRow.size() == 0) {
            return row;  // Якщо всі плитки порожні, повертаємо старий рядок
        } else {
            ensureRowSize(mergedRow, 4);  // Забезпечуємо, щоб лінія мала 4 плитки
            return mergedRow.toArray(new Tile[4]);  // Повертаємо новий рядок
        }
    }

    // Метод для забезпечення розміру лінії плиток
    private static void ensureRowSize(List<Tile> list, int size) {
        while (list.size() != size) {
            list.add(new Tile());  // Додаємо порожні плитки до списку, якщо їх недостатньо
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BACKGROUND_COLOR);  // Встановлюємо колір фону
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);  // Малюємо фон
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                drawTile(g, gameTiles[x + y * 4], x, y);  // Малюємо кожну плитку
            }
        }
    }

    // Метод для малювання плитки на екрані
    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);  // Використовуємо Graphics2D для малювання
        int value = tile.value;  // Отримуємо значення плитки
        int xOffset = offsetForTile(x);  // Вираховуємо зсув по x
        int yOffset = offsetForTile(y);  // Вираховуємо зсув по y
        g.setColor(tile.getBackground());  // Встановлюємо колір фону плитки
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);  // Малюємо плитку
        g.setColor(Color.WHITE);  // Встановлюємо колір тексту плитки

        final int fontSize = value < 100 ? 36 : value < 1000 ? 32 : 24;  // Розмір шрифту залежить від значення плитки
        final Font tileFont = new Font(FONT_NAME, Font.BOLD, fontSize);  // Встановлюємо шрифт для тексту
        g.setFont(tileFont);

        String tileText = String.valueOf(value);  // Преобразовуємо значення в рядок
        final FontMetrics fm = getFontMetrics(tileFont);  // Отримуємо метрики шрифту

        final int textWidth = fm.stringWidth(tileText);  // Ширина тексту
        final int textHeight = -(int) fm.getLineMetrics(tileText, g).getBaselineOffsets()[2];  // Висота тексту

        if (value != 0) {
            g.drawString(tileText, xOffset + (TILE_SIZE - textWidth) / 2, yOffset + TILE_SIZE - (TILE_SIZE - textHeight) / 2 - 2);  // Малюємо текст на плитці
        }

        if (isWin || isGameOver) {
            g.setColor(new Color(255, 255, 255, 30));  // Напівпрозорий білий фон
            g.fillRect(0, 0, getWidth(), getHeight());  // Заповнюємо фон при завершенні гри
            g.setColor(new Color(78, 139, 202));  // Колір для тексту повідомлень
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));  // Шрифт для повідомлень
            if (isWin) {
                g.drawString("You won!", 68, 150);  // Відображаємо повідомлення про перемогу
            }
            if (isGameOver) {
                g.drawString("Game over!", 50, 130);  // Відображаємо повідомлення про програш
                g.drawString("You lose!", 64, 200);  // Додаткове повідомлення
            }
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));  // Шрифт для інструкції
            g.setColor(new Color(128, 128, 128, 128));  // Напівпрозорий сірий текст
            g.drawString("Press ESC to play again", 80, getHeight() - 40);  // Повідомлення про повтор гри
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));  // Шрифт для рахунку
        g.drawString("Score: " + score, 200, 365);  // Відображаємо рахунок гравця
    }

    // Метод для вираховування зсуву плитки
    private static int offsetForTile(int index) {
        return index * (TILE_MARGIN + TILE_SIZE) + TILE_MARGIN;  // Вираховуємо зсув для малювання плитки
    }

    // Клас для представлення кожної плитки
    static class Tile {
        int value;  // Значення плитки

        public Tile() {
            this(0);  // За замовчуванням плитка порожня
        }

        public Tile(int num) {
            value = num;  // Ініціалізуємо значення плитки
        }

        public boolean isEmpty() {
            return value == 0;  // Повертає true, якщо плитка порожня
        }

        public Color getBackground() {
            switch (value) {
                case 2:
                    return new Color(0xf84d89);  // Колір для плитки 2
                case 4:
                    return new Color(0xe3447a);  // Колір для плитки 4
                case 8:
                    return new Color(0xce3c6b);  // Колір для плитки 8
                case 16:
                    return new Color(0xb9335b);  // Колір для плитки 16
                case 32:
                    return new Color(0xa32b4c);  // Колір для плитки 32
                case 64:
                    return new Color(0x8e223d);  // Колір для плитки 64
                case 128:
                    return new Color(0x791a2e);  // Колір для плитки 128
                case 256:
                    return new Color(0x64111e);  // Колір для плитки 256
                case 512:
                    return new Color(0x4f090f);  // Колір для плитки 512
                case 1024:
                    return new Color(0x3a0000);  // Колір для плитки 1024
                case 2048:
                    return new Color(0x3d070c);  // Колір для плитки 2048
            }
            return new Color(0xcdc1b4);  // Колір для порожніх плиток
        }
    }

    // Основний метод для запуску гри
    public static void main(String[] args) {
        JFrame gameWindow = new JFrame("2048");  // Створюємо вікно гри
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Закриття програми при закритті вікна
        gameWindow.setSize(350, 420);  // Встановлюємо розмір вікна
        gameWindow.setResizable(false);  // Забороняємо змінювати розмір вікна
        gameWindow.setLocationRelativeTo(null);  // Центруємо вікно на екрані

        gameWindow.add(new Run_Game());  // Додаємо ігрову панель

        gameWindow.setVisible(true);  // Робимо вікно видимим
    }
}
 
