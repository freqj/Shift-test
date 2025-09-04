package com.example.parser;

import java.util.HashMap;
import java.util.Set;

public class ParamParser {
    static Set<String> validParams = Set.of("--sort","-s","--order", "--stat", "--output", "-o");

    public static HashMap<String, String> parseParams(String[] args) throws IllegalArgumentException {
        HashMap<String, String> params = new HashMap<>();
        for(int i = 0; i < args.length; i++) {
            var eqIndex = args[i].indexOf('=');
            String key;
            if(eqIndex == -1) {
                key = args[i];
            }else {
                key = args[i].substring(0,args[i].indexOf('=')).trim();
            }

            if (validParams.contains(key)){
                switch (key) {
                    case "--sort":
                    case "-s":{
                        var value = args[i].substring(args[i].indexOf('=') + 1);
                        if (value.equals("name") || value.equals("salary")){
                            params.put("sort", value);
                        }else {
                            throw new IllegalArgumentException("Invalid sort parameter: " + value + ". Should be 'name' or 'salary'");
                        }
                        break;
                    }
                    case "--order": {
                        var value = args[i].substring(args[i].indexOf('=') + 1);
                        if (value.equals("asc") || value.equals("desc")){
                            params.put("order", value);
                        } else {
                            throw new IllegalArgumentException("Invalid order parameter: " + value + ". Should be 'asc' or 'desc'");
                        }
                        break;
                    }
                    case "--stat":{
                        params.put("stat", "true");
                        break;
                    }
                    case "--output":
                    case "-o":
                    {
                        var value = args[i].indexOf('=') > 0 ? args[i].substring(args[i].indexOf('=') + 1) : "console";
                        if (value.equals("console")){
                            params.put("output", value);
                        } else if (value.equals("file")){
                            try{
                                if (args[i + 1].substring(0, args[i + 1].indexOf('=')).equals("--path")){
                                    var path = args[i + 1].substring(args[i+1].indexOf('=') + 1);
                                    params.put("path", path);
                                    params.put("output", "file");
                                    i++;
                                }else {
                                    throw new IllegalArgumentException("Invalid key: " +
                                            args[i + 1].substring(0, args[i + 1].indexOf('=')) + ", should be --path");
                                }


                            }catch(IllegalArgumentException e) {
                                throw new IllegalArgumentException(e.getMessage());
                            } catch (Exception e){
                                throw new IllegalArgumentException("No path value, path should be --path=...");
                            }
                        }else {
                            System.out.println("Invalid output parameter: " + value + ". Should be 'file' or 'console'");
                            throw new IllegalArgumentException("Invalid output parameter");
                        }
                        break;
                    }
                    default: {
                        System.out.println("Invalid parameter: " + args[i].substring(args[i].indexOf('=')));
                        throw new IllegalArgumentException("Invalid key");
                    }
                }
            }
            else  {
                throw new IllegalArgumentException("Invalid key" + args[i]);
            }
        }
        if((params.containsKey("output") || params.containsKey("path")) && !params.containsKey("stat")){
            throw new IllegalArgumentException("--output or --path parameters are not allowed without --stat");
        }
        if(!(params.containsKey("order") == params.containsKey("sort"))){
            throw new IllegalArgumentException("--order should be used with -s parameter");
        }
        return params;
    }
}
