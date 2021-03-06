package smr.cs.ualberta.libcomp.data;

/**
 * The DataLibrary class is an object to store information on an library such as the metric data
 */
import java.time.LocalDate;

public class Library {
    private int id;
    private int library_id;
    private int domain_id;
    private String name;
    private String repository;
    private String Package;
    private double popularity;
    private double release_frequency;
    private double issue_closing_time;
    private double issue_response_time;
    private double performance;
    private double security;
    private double backwards_compatibility;
    private LocalDate last_discussed_so;
    private LocalDate Last_modification_date;

    private double overall_score;
    private String license;
    private String mavenlink;

    public Library() {
    }

    public int getId() {
        return id;
    }
    public int getLibrary_id() {
        return library_id;
    }
    public int getDomain_id() {
        return domain_id;
    }
    public String getName() { return name; }
    public String getRepository() {
        return repository;
    }
    public String getPackage() { return Package; }
    public String getMavenlink() { return mavenlink; }
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

    public LocalDate getLast_discussed_so() { return last_discussed_so; }
    public LocalDate getLast_modification_date() { return Last_modification_date; }
    public double getOverall_score() { return overall_score; }
    public String getLicense() { return license; }

    public void setLibrary_id(int library_id) {
        this.library_id = library_id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRepository(String repository) {
        this.repository = repository;
    }
    public void setPackage(String aPackage) { Package = aPackage; }
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
    public void setLast_discussed_so(LocalDate last_discussed_so) { this.last_discussed_so = last_discussed_so; }
    public void setLast_modification_date(LocalDate last_modification_date) { Last_modification_date = last_modification_date; }
    public void setOverall_score(double overall_score) { this.overall_score = overall_score; }
    public void setLicense(String license) { this.license = license; }
    public void setMavenlink(String mavenlink) { this.mavenlink = mavenlink; }
    public void setBackwards_compatibility(double backwards_compatibility) {this.backwards_compatibility = backwards_compatibility;

    }
}

