package com.example;


import com.example.parser.FileParser;
import com.example.validator.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

import static com.example.parser.ParamParser.parseParams;

public class Main {

    private static final int TYPE = 0;
    private static final int ID = 1;
    private static final int NAME = 2;
    private static final int SALARY = 3;
    private static final int DEPARTMENT = 4;

    private static final String MANAGER = "Manager";
    private static final String EMPLOYEE = "Employee";



    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        try {
             params = parseParams(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        List<String> alllines = FileParser.proccessFiles();
        proccessLinesWithParams(alllines, params);
    }

    public static void proccessLinesWithParams(List<String> allLines, HashMap<String, String> params) {
        HashMap<Integer, String[]> managerMap = new HashMap<>();
        HashMap<Integer, List<String[]>> employeeMap = new HashMap<>();
        List<String[]> err = new ArrayList<>();

        allLines.forEach(line -> {
            String[] lineArr = line.split(",");
            if (lineArr[TYPE].equals(MANAGER)) {
                if (Validator.validateManager(lineArr)){
                    managerMap.put(Integer.valueOf(lineArr[ID]), lineArr);
                } else {
                    err.add(lineArr);
                }
            }else  if (lineArr[TYPE].equals(EMPLOYEE)) {
                if (Validator.validateEmployee(lineArr)){
                    employeeMap.computeIfAbsent(Integer.valueOf(lineArr[DEPARTMENT]), l ->new ArrayList<>()).add(lineArr);
                }else{
                    err.add(lineArr);
                }
            }
        });

        employeeMap.keySet().forEach(key -> {if (!managerMap.containsKey(key)){err.addAll(employeeMap.get(key));}});
        proccessParams(params, managerMap, employeeMap, err);
    }

    public static void proccessParams(HashMap<String, String> params, HashMap<Integer, String[]> managerMap,
                                      HashMap<Integer, List<String[]>> employeeMap, List<String[]> err) {

        HashMap<String, List<String[]>> result = new HashMap<>();
        for(Map.Entry<Integer, String[]> entry : managerMap.entrySet()){

            var depName = entry.getValue()[DEPARTMENT];
            result.computeIfAbsent(depName, l -> new ArrayList<>()).add(entry.getValue());
            if(params.containsKey("sort")){
                Comparator<String[]> comp = params.get("sort").equals("name") ?
                        Comparator.comparing((arr) -> arr[NAME]):Comparator.comparing((arr) -> Integer.valueOf(arr[SALARY]));

                if(params.get("order").equals("asc")){
                    employeeMap.getOrDefault(entry.getKey(), Collections.emptyList()).sort(comp);
                }else{
                    employeeMap.getOrDefault(entry.getKey(), Collections.emptyList()).sort(comp.reversed());
                }
            }
            Optional.ofNullable(employeeMap.get(entry.getKey())).ifPresent(result.get(depName)::addAll);

        }

        Path p = Path.of(params.getOrDefault("path", "output"));
        boolean isFile = p.getFileName().toString().contains(".");
        try {
            if (isFile) {
                p = p.getParent() != null? p.getParent() : Path.of(".");
                Files.createDirectories(p);
            } else {
                Files.createDirectory(p);
            }
        }catch (IOException e) {
            e.getMessage();
        }


        var dir = p + "/";
        File errorFile = new File(dir + "error.log");
        writeToFile(err, errorFile);
        for(Map.Entry<String, List<String[]>> entry : result.entrySet()){
            File sbFile = new File(dir  + entry.getKey() + ".sb");
            writeToFile(entry.getValue(), sbFile);
        }



        List<String[]> stat = params.get("stat") != null? buildStat(result):Collections.emptyList();
        if(!stat.isEmpty()){
            if(!params.containsKey("output") || params.get("output").equals("console")){
                for(String[] arr : stat){
                    System.out.println(String.join(",", arr));
                }
            }
            else{
                String path;
                if(params.get("path") != null && isFile){
                    path = params.get("path");
                }else {
                    path = dir + "statistics.txt";
                }
                File statFile = new File(path);
                writeToFile(stat, statFile);
            }
        }

    }

    private static void writeToFile(List<String[]> lines, File file) {
        try(FileOutputStream fos = new FileOutputStream(file, false)){
            for(String[] arr : lines){
                fos.write(String.join(",",arr).getBytes());
                fos.write("\n".getBytes());
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String[]> buildStat(HashMap<String, List<String[]>> result) {
        List<String[]> stat = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        for(Map.Entry<String, List<String[]>> entry : result.entrySet()){
            var min = BigDecimal.ZERO;
            var max = BigDecimal.ZERO;
            var mid  = BigDecimal.ZERO;
            if (entry.getValue().size() > 1){
                for(String[] arr : entry.getValue()){
                    if(arr[0].equals("Employee")){
                        max = max.compareTo(BigDecimal.valueOf(Long.parseLong(arr[SALARY]))) < 0 ? BigDecimal.valueOf(Long.parseLong(arr[SALARY])) : max;
                        if (min.equals(BigDecimal.ZERO)){
                            min =  BigDecimal.valueOf(Long.parseLong(arr[SALARY]));
                        }else{
                            min = min.compareTo(BigDecimal.valueOf(Long.parseLong(arr[SALARY]))) > 0 ? BigDecimal.valueOf(Long.parseLong(arr[SALARY])) : min;
                        }
                        mid = mid.add(BigDecimal.valueOf(Long.parseLong(arr[SALARY])));
                    }
                }
                mid = mid.divide(BigDecimal.valueOf(entry.getValue().size() - 1), 2,  RoundingMode.HALF_UP);
            }
            stat.add(new String[]{entry.getKey(), df.format(min), df.format(max), df.format(mid)});
        }
        stat.sort(Comparator.comparing(arr -> arr[0]));
        stat.add(0, new String[]{"department", "min", "max", "mid"});
        return stat;
    }

}