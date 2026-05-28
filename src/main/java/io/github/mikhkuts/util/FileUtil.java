package io.github.mikhkuts.util;

import io.github.mikhkuts.StyleColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    private String convertToString(StyleColor color){
        StringBuilder sb = new StringBuilder("  ");
        sb.append(color.name);
        sb.append(": ");
        sb.append("rgb(").append(color.color).append(");");
        return sb.toString();
    }
}
