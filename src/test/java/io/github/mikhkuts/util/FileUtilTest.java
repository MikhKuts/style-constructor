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
        testFile = tempDir.resolve("test_style.css");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(testFile)) {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void testWrite_singleStyle() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("primary", "255, 0, 0"));

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);
        String expected = "*{  primary: rgb(255, 0, 0);}";

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
        String expected = "*{  primary: rgb(255, 0, 0);  secondary: rgb(0, 255, 0);  background: rgb(0, 0, 255);}";

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
    void testWrite_styleColorWithSpecialCharactersInName() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("dark-mode_bg", "30, 30, 30"));

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);
        String expected = "*{  dark-mode_bg: rgb(30, 30, 30);}";

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

    @Test
    void testWrite_appendDoesNotHappenOnSecondCall() throws IOException {
        List<StyleColor> firstList = new ArrayList<>();
        firstList.add(new StyleColor("color1", "1,1,1"));
        fileUtil.write(testFile.toString(), firstList);

        List<StyleColor> secondList = new ArrayList<>();
        secondList.add(new StyleColor("color2", "2,2,2"));
        fileUtil.write(testFile.toString(), secondList);

        String content = Files.readString(testFile);
        String expected = "*{  color2: rgb(2, 2, 2);}";

        assertEquals(expected, content);
    }

    @Test
    void testWrite_convertedToStringFormatting() throws IOException {
        List<StyleColor> styleColors = new ArrayList<>();
        styleColors.add(new StyleColor("testColor", "123, 45, 67"));

        fileUtil.write(testFile.toString(), styleColors);

        String content = Files.readString(testFile);

        assertTrue(content.startsWith("*{"));
        assertTrue(content.endsWith("}"));
        assertTrue(content.contains("testColor: rgb(123, 45, 67);"));
        assertEquals("*{  testColor: rgb(123, 45, 67);}", content);
    }
}