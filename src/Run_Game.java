import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Run_Game extends JPanel {

    // Колір фону ігрового поля
    private static final Color BG_COLOR = new Color(0xDBC3AD);

    // Шрифт для відображення тексту
    private static final String FONT_NAME = "Montserrat Alternates";

    // Розмір плитки
    private static final int TILE_SIZE = 64;

    // Відстань між плитками
    private static final int TILES_MARGIN = 16;

    // Змінні для відстеження стану гри
    int myScore = 0;  // Рахунок
    boolean myWin = false;  // Чи виграв гравець
    boolean myLose = false;  // Чи програв гравець

    // Масив плиток для ігрового поля
    private Tile[] myTiles;

    // Конструктор, що ініціалізує ігрове поле
    public Run_Game() {
        setPreferredSize(new Dimension(340, 400));  // Встановлюємо розмір вікна
        setFocusable(true);  // Фокус на вікно для обробки подій
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resetGame();  // Скидання гри при натисканні ESC
                }
                if (!canMove()) {
                    myLose = true;  // Якщо неможливо більше рухатись, програємо
                }

                if (!myWin && !myLose) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();  // Переміщення плиток вліво
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();  // Переміщення плиток вправо
                            break;
                        case KeyEvent.VK_DOWN:
                            down();  // Переміщення плиток вниз
                            break;
                        case KeyEvent.VK_UP:
                            up();  // Переміщення плиток вгору
                            break;
                    }
                }

                if (!myWin && !canMove()) {
                    myLose = true;  // Якщо не можемо більше рухатись, програємо
                }

                repaint();  // Оновлюємо вікно після кожного ходу
            }
        });
        resetGame();  // Скидання гри при першому запуску
    }

    // Метод для скидання гри
    private void resetGame() {
        myScore = 0;  // Скидаємо рахунок
        myWin = false;  // Скидаємо перемогу
        myLose = false;  // Скидаємо поразку
        myTiles = new Tile[4 * 4];  // Ініціалізуємо масив плиток
        for (int i = 0; i < myTiles.length; i++) {
            myTiles[i] = new Tile();  // Створюємо порожні плитки
        }
        addTile();  // Додаємо першу плитку
        addTile();  // Додаємо другу плитку
    }

    // Метод для переміщення плиток вліво
    public void left() {
        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {
            Tile[] line = getLine(i);  // Отримуємо лінію (рядок) плиток
            Tile[] merged = mergeLine(moveLine(line));  // Спочатку рухаємо, потім об'єднуємо плитки
            setLine(i, merged);  // Встановлюємо оновлену лінію плиток
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;  // Якщо були зміни, додаємо нову плитку
            }
        }

        if (needAddTile) {
            addTile();  // Додаємо нову плитку, якщо були зміни
        }
    }

    // Метод для переміщення плиток вправо
    public void right() {
        myTiles = rotate(180);  // Повертаємо поле на 180 градусів
        left();  // Використовуємо метод переміщення вліво
        myTiles = rotate(180);  // Повертаємо поле назад
    }

    // Метод для переміщення плиток вгору
    public void up() {
        myTiles = rotate(270);  // Повертаємо поле на 270 градусів
        left();  // Використовуємо метод переміщення вліво
        myTiles = rotate(90);  // Повертаємо поле назад
    }

    // Метод для переміщення плиток вниз
    public void down() {
        myTiles = rotate(90);  // Повертаємо поле на 90 градусів
        left();  // Використовуємо метод переміщення вліво
        myTiles = rotate(270);  // Повертаємо поле назад
    }

    // Метод для отримання плитки на координатах (x, y)
    private Tile tileAt(int x, int y) {
        return myTiles[x + y * 4];
    }

    // Метод для додавання нової плитки
    private void addTile() {
        List<Tile> list = availableSpace();  // Отримуємо всі доступні порожні місця
        if (!availableSpace().isEmpty()) {
            int idx = (int) (Math.random() * list.size()) % list.size();
            Tile emptyTile = list.get(idx);  // Вибираємо випадкову порожню плитку
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;  // Додаємо нову плитку 2 або 4
        }
    }

    // Метод для отримання доступних порожніх місць
    private List<Tile> availableSpace() {
        final List<Tile> list = new ArrayList<>(16);  // Створюємо список для порожніх місць
        for (Tile t : myTiles) {
            if (t.isEmpty()) {
                list.add(t);  // Додаємо порожні плитки до списку
            }
        }
        return list;
    }

    // Метод для перевірки, чи заповнене поле
    private boolean isFull() {
        return availableSpace().size() == 0;  // Перевіряємо, чи немає вільних місць
    }

    // Метод для перевірки, чи можливий рух
    boolean canMove() {
        if (!isFull()) {
            return true;  // Якщо є порожні місця, рух можливий
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Tile t = tileAt(x, y);
                if ((x < 3 && t.value == tileAt(x + 1, y).value)
                        || (y < 3 && t.value == tileAt(x, y + 1).value)) {
                    return true;  // Якщо можна об'єднати плитки, рух можливий
                }
            }
        }
        return false;
    }

    // Метод для порівняння двох ліній плиток
    private boolean compare(Tile[] line1, Tile[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }

        for (int i = 0; i < line1.length; i++) {
            if (line1[i].value != line2[i].value) {
                return false;
            }
        }
        return true;
    }

    // Метод для повороту поля на певний кут
    private Tile[] rotate(int angle) {
        Tile[] newTiles = new Tile[4 * 4];  // Створюємо новий масив для повороту
        int offsetX = 3, offsetY = 3;
        if (angle == 90) {
            offsetY = 0;
        } else if (angle == 270) {
            offsetX = 0;
        }

        double rad = Math.toRadians(angle);  // Конвертуємо кут у радіани
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newTiles[newX + newY * 4] = tileAt(x, y);  // Повертаємо нові координати плитки
            }
        }
        return newTiles;
    }

    // Метод отримує лінію (рядок) плиток
    private Tile[] getLine(int index) {
        Tile[] result = new Tile[4];  // Створюємо масив для збереження лінії
        for (int i = 0; i < 4; i++) {
            result[i] = tileAt(i, index);  // Повертає плитки з одного рядка
        }
        return result;
    }

    // Метод для встановлення лінії плиток
    private void setLine(int index, Tile[] re) {
        System.arraycopy(re, 0, myTiles, index * 4, 4);  // Встановлює рядок плиток на полі
    }

    // Метод для руху лінії плиток
    private Tile[] moveLine(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()) {
                l.addLast(oldLine[i]);  // Додаємо плитки, які не порожні, до списку
            }
        }
        if (l.size() == 0) {
            return oldLine;  // Якщо всі плитки порожні, повертаємо старий рядок
        } else {
            Tile[] newLine = new Tile[4];
            ensureSize(l, 4);  // Забезпечуємо необхідний розмір лінії
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();  // Повертаємо новий рядок плиток
            }
            return newLine;
        }
    }

    // Метод для злиття плиток в лінії
    private Tile[] mergeLine(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<>();
        for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].value;
            if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
                num *= 2;
                myScore += num;  // Додаємо до рахунку
                int ourTarget = 2048;
                if (num == ourTarget) {
                    myWin = true;  // Встановлюємо прапорець перемоги, якщо досягли 2048
                }
                i++;  // Пропускаємо наступну плитку, якщо об'єднали дві однакові
            }
            list.add(new Tile(num));  // Додаємо нову плитку з об'єднаним значенням
        }
        if (list.size() == 0) {
            return oldLine;  // Якщо всі плитки порожні, повертаємо старий рядок
        } else {
            ensureSize(list, 4);  // Забезпечуємо, щоб лінія мала 4 плитки
            return list.toArray(new Tile[4]);  // Повертаємо новий рядок
        }
    }

    // Метод для забезпечення розміру лінії плиток
    private static void ensureSize(List<Tile> l, int s) {
        while (l.size() != s) {
            l.add(new Tile());  // Додаємо порожні плитки до списку, якщо їх недостатньо
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);  // Встановлюємо колір фону
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);  // Малюємо фон
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                drawTile(g, myTiles[x + y * 4], x, y);  // Малюємо кожну плитку
            }
        }
    }

    // Метод для малювання плитки на екрані
    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);  // Використовуємо Graphics2D для малювання
        int value = tile.value;  // Отримуємо значення плитки
        int xOffset = offsetColors(x);  // Вираховуємо зсув по x
        int yOffset = offsetColors(y);  // Вираховуємо зсув по y
        g.setColor(tile.getBackground());  // Встановлюємо колір фону плитки
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);  // Малюємо плитку
        g.setColor(Color.WHITE);  // Встановлюємо колір тексту плитки

        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;  // Розмір шрифту залежить від значення плитки
        final Font font = new Font(FONT_NAME, Font.BOLD, size);  // Встановлюємо шрифт для тексту
        g.setFont(font);

        String s = String.valueOf(value);  // Преобразовуємо значення в рядок
        final FontMetrics fm = getFontMetrics(font);  // Отримуємо метрики шрифту

        final int w = fm.stringWidth(s);  // Ширина тексту
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];  // Висота тексту

        if (value != 0) {
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);  // Малюємо текст на плитці
        }

        if (myWin || myLose) {
            g.setColor(new Color(255, 255, 255, 30));  // Напівпрозорий білий фон
            g.fillRect(0, 0, getWidth(), getHeight());  // Заповнюємо фон при завершенні гри
            g.setColor(new Color(78, 139, 202));  // Колір для тексту повідомлень
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));  // Шрифт для повідомлень
            if (myWin) {
                g.drawString("You won!", 68, 150);  // Відображаємо повідомлення про перемогу
            }
            if (myLose) {
                g.drawString("Game over!", 50, 130);  // Відображаємо повідомлення про програш
                g.drawString("You lose!", 64, 200);  // Додаткове повідомлення
            }
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));  // Шрифт для інструкції
            g.setColor(new Color(128, 128, 128, 128));  // Напівпрозорий сірий текст
            g.drawString("Press ESC to play again", 80, getHeight() - 40);  // Повідомлення про повтор гри
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));  // Шрифт для рахунку
        g.drawString("Score: " + myScore, 200, 365);  // Відображаємо рахунок гравця
    }

    // Метод для вираховування зсуву плитки
    private static int offsetColors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;  // Вираховуємо зсув для малювання плитки
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
        JFrame game = new JFrame("2048");  // Створюємо вікно гри
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Закриття програми при закритті вікна
        game.setSize(350, 420);  // Встановлюємо розмір вікна
        game.setResizable(false);  // Забороняємо змінювати розмір вікна
        game.setLocationRelativeTo(null);  // Центруємо вікно на екрані

        game.add(new Run_Game());  // Додаємо ігрову панель

        game.setVisible(true);  // Робимо вікно видимим
    }
}
