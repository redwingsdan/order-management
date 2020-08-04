package com.aventuracctv.ordermangement.data;

public class BuildItem {

    private int order;
    private String customerName;
    private String buildType;
    private String pdfPath;
    private String assignedTo;
    private boolean isAssigned;
    private int progress;
    private int position;

    public BuildItem() {

    }

    public BuildItem(String customerName, String buildType, String pdfPath, String assignedTo) {
        this.customerName = customerName;
        this.buildType = buildType;
        this.pdfPath = pdfPath;
        this.assignedTo = assignedTo;
    }

    public BuildItem(String buildType) {
        this.buildType = buildType;
    }

    public BuildItem(String customerName, String buildType, boolean isAssigned) {
        this.customerName = customerName;
        this.buildType = buildType;
        this.isAssigned = isAssigned;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getpdfPath() {
        return pdfPath;
    }

    public void setpdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String assignedTo() {
        return assignedTo;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}