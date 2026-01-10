package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JSONFileHandler {
    public static String read(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) { w.write("[]"); }
            }
            byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) { e.printStackTrace(); return ""; }
    }

    public static void write(String path, String data) {
        try {
            File f = new File(path);
            if (!f.exists()) { f.getParentFile().mkdirs(); f.createNewFile(); }
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) { w.write(data); }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
