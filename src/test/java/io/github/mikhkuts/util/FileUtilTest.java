package io.github.mikhkuts.util;

import io.github.mikhkuts.StyleColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @TempDir
    Path tempDir;

    private FileUtil fileUtil;
    private Path testFile;

    @BeforeEach
    void setUp() {
        fileUtil = new FileUtil();
        testFile = tempDir.resolve("test_style.txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(testFile)) {
            Files.deleteIfExists(testFile);
        }
    }

    // ==================== TESTS FOR write() METHOD ====================

    @Test
    void testWrite_singleStyle() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("primary", "255, 0, 0"));

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);
        String expected = "*{    primary: rgb(255, 0, 0);}";

        assertEquals(expected, content);
    }

    @Test
    void testWrite_multipleStyles() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("primary", "255, 0, 0"));
        styleColors.add(new StyleColor("secondary", "0, 255, 0"));
        styleColors.add(new StyleColor("background", "0, 0, 255"));

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);
        String expected = "*{    primary: rgb(255, 0, 0);    secondary: rgb(0, 255, 0);    background: rgb(0, 0, 255);}";

        assertEquals(expected, content);
    }

    @Test
    void testWrite_emptyList() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);
        String expected = "*{}";

        assertEquals(expected, content);
    }

    @Test
    void testWrite_appendMode() throws IOException {
        // Первая запись
        List<StyleColor> firstList = new ArrayList<>();
        firstList.add(new StyleColor("color1", "1,1,1"));
        fileUtil.write(testFile.toString(), firstList);

        // Вторая запись (должна перезаписать, а не дополнить)
        List<StyleColor> secondList = new ArrayList<>();
        secondList.add(new StyleColor("color2", "2,2,2"));
        fileUtil.write(testFile.toString(), secondList);

        String content = Files.readString(testFile);
        String expected = "*{    color2: rgb(2,2,2);}";

        assertEquals(expected, content);
    }

    @Test
    void testWrite_nullPathThrowsException() {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("test", "0,0,0"));

        assertThrows(NullPointerException.class, () -> {
            fileUtil.write(null, styleColors);
        });
    }

    @Test
    void testWrite_nullListThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            fileUtil.write(testFile.toString(), null);
        });
    }

    @Test
    void testWrite_invalidDirectoryPathThrowsIOException() {
        Path invalidPath = tempDir.resolve("nonexistent/subfolder/file.txt");
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("test", "0,0,0"));

        assertThrows(IOException.class, () -> {
            fileUtil.write(invalidPath.toString(), styleColors);
        });
    }

    // ==================== TESTS FOR read() METHOD ====================

    @Test
    void testRead_singleStyle() throws IOException {
        // Подготовка тестового файла
        String content = "*{    primary: rgb(255, 0, 0);}";
        Files.writeString(testFile, content);

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(1, result.length);
        assertTrue(result[0] instanceof StyleColor);

        StyleColor styleColor = (StyleColor) result[0];
        assertEquals("primary", styleColor.name);
        assertEquals("255, 0, 0", styleColor.color);
    }

    @Test
    void testRead_multipleStyles() throws IOException {
        // Подготовка тестового файла
        String content = "*{    primary: rgb(255, 0, 0);    secondary: rgb(0, 255, 0);    background: rgb(0, 0, 255);}";
        Files.writeString(testFile, content);

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(3, result.length);

        StyleColor primary = (StyleColor) result[0];
        assertEquals("primary", primary.name);
        assertEquals("255, 0, 0", primary.color);

        StyleColor secondary = (StyleColor) result[1];
        assertEquals("secondary", secondary.name);
        assertEquals("0, 255, 0", secondary.color);

        StyleColor background = (StyleColor) result[2];
        assertEquals("background", background.name);
        assertEquals("0, 0, 255", background.color);
    }

    @Test
    void testRead_emptyFile() throws IOException {
        // Создаем пустой файл
        Files.createFile(testFile);

        // Записываем только открывающую и закрывающую скобки
        Files.writeString(testFile, "*{}");

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(0, result.length);
    }

    @Test
    void testRead_fileWithOnlyBraces() throws IOException {
        Files.writeString(testFile, "*{}");

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(0, result.length);
    }

    @Test
    void testRead_styleWithSpacesInRgb() throws IOException {
        String content = "*{    accent: rgb(100 , 150 , 200);}";
        Files.writeString(testFile, content);

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(1, result.length);
        StyleColor styleColor = (StyleColor) result[0];
        assertEquals("accent", styleColor.name);
        assertEquals("100 , 150 , 200", styleColor.color);
    }

    @Test
    void testRead_styleWithSpecialCharactersInName() throws IOException {
        String content = "*{    dark-mode_bg: rgb(30, 30, 30);}";
        Files.writeString(testFile, content);

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(1, result.length);
        StyleColor styleColor = (StyleColor) result[0];
        assertEquals("dark-mode_bg", styleColor.name);
        assertEquals("30, 30, 30", styleColor.color);
    }

    @Test
    void testRead_multilineFile() throws IOException {
        // Имитация файла с переносами строк
        StringBuilder content = new StringBuilder("*{\n");
        content.append("    primary: rgb(255, 0, 0);\n");
        content.append("    secondary: rgb(0, 255, 0);\n");
        content.append("}");
        Files.writeString(testFile, content.toString());

        Object[] result = fileUtil.read(testFile.toString());

        assertEquals(2, result.length);

        StyleColor primary = (StyleColor) result[0];
        assertEquals("primary", primary.name);
        assertEquals("255, 0, 0", primary.color);

        StyleColor secondary = (StyleColor) result[1];
        assertEquals("secondary", secondary.name);
        assertEquals("0, 255, 0", secondary.color);
    }

    @Test
    void testRead_nonexistentFileThrowsException() {
        Path nonExistentFile = tempDir.resolve("nonexistent.txt");

        assertThrows(IOException.class, () -> {
            fileUtil.read(nonExistentFile.toString());
        });
    }

    @Test
    void testRead_nullPathThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            fileUtil.read(null);
        });
    }

    @Test
    void testRead_fileWithInvalidFormat() throws IOException {
        // Некорректный формат файла
        String content = "Invalid content without proper format";
        Files.writeString(testFile, content);

        // Метод read должен корректно обработать или выбросить исключение
        // В зависимости от реализации convertToColor
        assertThrows(Exception.class, () -> {
            fileUtil.read(testFile.toString());
        });
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    void testWriteAndRead_roundTrip() throws IOException {
        // Создаем список стилей
        List<StyleColor> originalColors = new ArrayList<>();
        originalColors.add(new StyleColor("primary", "255, 0, 0"));
        originalColors.add(new StyleColor("secondary", "0, 255, 0"));
        originalColors.add(new StyleColor("background", "0, 0, 255"));

        // Записываем в файл
        fileUtil.write(testFile.toString(), originalColors);

        // Читаем из файла
        Object[] readColors = fileUtil.read(testFile.toString());

        // Проверяем соответствие
        assertEquals(originalColors.size(), readColors.length);

        for (int i = 0; i < originalColors.size(); i++) {
            StyleColor original = originalColors.get(i);
            StyleColor read = (StyleColor) readColors[i];

            assertEquals(original.name, read.name);
            assertEquals(original.color, read.color);
        }
    }

    @Test
    void testWriteAndRead_emptyRoundTrip() throws IOException {
        List<StyleColor> originalColors = new ArrayList<>();

        fileUtil.write(testFile.toString(), originalColors);
        Object[] readColors = fileUtil.read(testFile.toString());

        assertEquals(0, readColors.length);
    }

    @Test
    void testWriteAndRead_withSpaces() throws IOException {
        List<StyleColor> originalColors = new ArrayList<>();
        originalColors.add(new StyleColor("test color", "100, 150, 200"));

        fileUtil.write(testFile.toString(), originalColors);
        Object[] readColors = fileUtil.read(testFile.toString());

        assertEquals(1, readColors.length);
        StyleColor read = (StyleColor) readColors[0];
        assertEquals("test color", read.name);
        assertEquals("100, 150, 200", read.color);
    }
}