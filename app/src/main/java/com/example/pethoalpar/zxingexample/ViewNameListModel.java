package com.example.pethoalpar.zxingexample;

public class ViewNameListModel {
    String student_name;
    String student_matric;
    int status;

    public void setStudent_name(String student_name){
        this.student_name = student_name;
    }

    public void setStudent_matric(String student_matric){
        this.student_matric = student_matric;
    }

    public void setStatus (int status) { this.status = status; }

    public String getStudent_name(){
        return student_name;
    }

    public String getStudent_matric(){
        return student_matric;
    }

    public int getStatus() { return status; }
}
