package com.example.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileParser {

    static Path ROOT_DIR = Path.of(System.getProperty("user.dir"));
    public static List<String> proccessFiles() {
        List<String> allStrings = new ArrayList<>();
        try(var stream = Files.list(ROOT_DIR)){
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().trim().toLowerCase().endsWith(".sb"))
                    .forEach(path -> {
                        try{allStrings.addAll(Files.readAllLines(path));}
                        catch (IOException e) {
                            System.out.println("Unable to read file: " + path);
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e){
            e.printStackTrace();
        }
        return  allStrings;
    }
}
