package org.intellij.sdk.editor;

public class LibData {
    private int library_id;
    private String name;
    private int year;
    private int month;
    private String Package;
    private double popularity;
    private double release_frequency;
    private double issue_closing_time;
    private double issue_response_time;
    private double performance;
    private double security;
    private double backwards_compatibility;

    public LibData() {

    }

    public LibData(int library_id, String name, int year, int month, String aPackage, double popularity, double release_frequency, double issue_closing_time, double issue_response_time, double performance, double security, double backwards_compatibility) {
        this.library_id = library_id;
        this.name = name;
        this.year = year;
        this.month = month;
        Package = aPackage;
        this.popularity = popularity;
        this.release_frequency = release_frequency;
        this.issue_closing_time = issue_closing_time;
        this.issue_response_time = issue_response_time;
        this.performance = performance;
        this.security = security;
        this.backwards_compatibility = backwards_compatibility;
    }

    public int getLibrary_id() {
        return library_id;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getPackage() {
        return Package;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getRelease_frequency() {
        return release_frequency;
    }

    public double getIssue_closing_time() {
        return issue_closing_time;
    }

    public double getIssue_response_time() {
        return issue_response_time;
    }

    public double getPerformance() {
        return performance;
    }

    public double getSecurity() {
        return security;
    }

    public double getBackwards_compatibility() {
        return backwards_compatibility;
    }

    public void setLibrary_id(int library_id) {
        this.library_id = library_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setRelease_frequency(double release_frequency) {
        this.release_frequency = release_frequency;
    }

    public void setIssue_closing_time(double issue_closing_time) {
        this.issue_closing_time = issue_closing_time;
    }

    public void setIssue_response_time(double issue_response_time) {
        this.issue_response_time = issue_response_time;
    }

    public void setPerformance(double performance) {
        this.performance = performance;
    }

    public void setSecurity(double security) {
        this.security = security;
    }

    public void setBackwards_compatibility(double backwards_compatibility) {
        this.backwards_compatibility = backwards_compatibility;
    }
}

