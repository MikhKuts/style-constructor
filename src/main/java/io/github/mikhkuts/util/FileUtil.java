package io.github.mikhkuts.util;

import io.github.mikhkuts.StyleColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
    private final String OPEN = "*{";
    private final String CLOSE = "}";

    public <T extends List<StyleColor>> void write(String path, T styleColor) throws IOException{
        Path pathStr = Path.of(path);

        Files.write(pathStr, OPEN.getBytes());

        for (StyleColor style: styleColor) {
            Files.write(pathStr, convertToString(style).getBytes(), StandardOpenOption.APPEND);
        }

        Files.write(pathStr, CLOSE.getBytes(), StandardOpenOption.APPEND);
    }

    public Object[] read(String path) throws IOException {
        Path pathStr = Path.of(path);
        String content = Files.readString(pathStr);

        // Извлекаем содержимое между OPEN и CLOSE
        int start = content.indexOf(OPEN) + OPEN.length();
        int end = content.lastIndexOf(CLOSE);
        if (start >= end) return new Object[0];

        String innerContent = content.substring(start, end).trim();
        if (innerContent.isEmpty()) return new Object[0];

        // Разделяем по стилям (предполагая разделитель ";")
        String[] styles = innerContent.split(";");

        return Arrays.stream(styles)
                .filter(s -> !s.trim().isEmpty())
                .map(s -> s.trim())
                .map(this::convertToColor)
                .toArray();
    }

    private String convertToString(StyleColor color){
        StringBuilder sb = new StringBuilder("    ");
        sb.append(color.name);
        sb.append(": ");
        sb.append("rgb(").append(color.color).append(");");
        return sb.toString();
    }

    private StyleColor convertToColor(String style){
        String[] styleStr = style.split("\s*:\s*", 2);
        String color = styleStr[1].substring(4);
        color = color.substring(0, color.length()-1);
        return new StyleColor(styleStr[0], color);
    }
}
