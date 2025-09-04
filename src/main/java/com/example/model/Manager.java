package com.example.model;

public record Manager(
        long id,
        int salary,
        String name,
        String department
){
    public String[] toLine(){
        var line = new String[5];
        line[0] = this.getClass().getSimpleName();
        line[1] = String.valueOf(name);
        line[2] = String.valueOf(salary);
        line[3] = department;
        line[4] = String.valueOf(id);
        return line;
    }


}
