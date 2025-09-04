package com.example.model;

public record Employee(
        String name,
        int salary,
        int departmentId,
        int id
) {
    public String[] toLine(){
        var line = new String[5];
        line[0] = this.getClass().getSimpleName();
        line[1] = String.valueOf(name);
        line[2] = String.valueOf(salary);
        line[3] = String.valueOf(departmentId);
        line[4] = String.valueOf(id);
        return line;
    }
}
