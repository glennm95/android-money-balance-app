package edu.uic.cs478.gmasca2.project5app2;

/**
 * Created by Glenn on 30-Apr-18.
 */

public class Output {
    int day, month, year, openbalance, closebalance;
    String dayofweek;
    public Output(int day, int month, int year, String dayofweek, int openbalance, int closebalance){
        this.day = day;
        this.month = month;
        this.year = year;
        this.dayofweek = dayofweek;
        this.openbalance = openbalance;
        this.closebalance = closebalance;
    }
}
