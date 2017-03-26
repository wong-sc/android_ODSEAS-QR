package com.example.pethoalpar.zxingexample;

public class SubjectDetailsModel {
    private String subjectCode, subjectName, venue, date, time, numberOfStudent;
    private String invigilator, position;

//    public void SubjectDetailsModel(String )

    public void setSubjectCode(String subjectCode){this.subjectCode = subjectCode;}
    public void setSubjectName(String subjectName){this.subjectName = subjectName;}
    public void setVenue(String venue){this.venue = venue;}
    public void setDate(String date){this.date = date;}
    public void setTime(String time){this.time = time;}
    public void setNumberOfStudent(String numberOfStudent){this.numberOfStudent = numberOfStudent;}
    public void setInvigilator(String invigilator){this.invigilator = invigilator;}
    public void setPosition(String position){this.position = position;}
    public String getSubjectCode(){return subjectCode;}
    public String getSubjectName(){return subjectName;}
    public String getVenue(){return venue;}
    public String getDate(){return date;}
    public String getTime(){return time;}
    public String getNumberOfStudent(){return numberOfStudent;}
    public String getInvigilator(){return invigilator;}
    public String getPosition(){return position;}
}
