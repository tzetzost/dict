package org.tstefanov.dict;

public class JobStatus {
    private JobStatusEnum status;
    private double progress;

    public JobStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JobStatusEnum status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
