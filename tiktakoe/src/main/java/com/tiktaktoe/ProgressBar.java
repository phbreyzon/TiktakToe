package com.tiktaktoe;

public class ProgressBar {
    private int current;
    private int max;
    private StringBuilder progressBar;
    private final int barWidth = 50;
    
    // Time tracking
    private long startTime;
    private long lastUpdateTime;
    private double avgTimePerTick;

    public ProgressBar() {
        this.current = 0;
        this.max = 100;
        this.progressBar = new StringBuilder();
        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = startTime;
        this.avgTimePerTick = 0;
    }

    public void setMax(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max value must be positive");
        }
        this.max = max;
        this.current = Math.min(this.current, max);
        resetTimer();
    }

    public void tickOne() {
        if (current < max) {
            current++;
            updateTimeEstimates();
            updateProgressBar();
        }
    }

    public void reset() {
        current = 0;
        resetTimer();
        updateProgressBar();
    }

    private void resetTimer() {
        startTime = System.currentTimeMillis();
        lastUpdateTime = startTime;
        avgTimePerTick = 0;
    }

    private void updateTimeEstimates() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastUpdateTime;
        
        // Update moving average for time per tick
        if (avgTimePerTick == 0) {
            avgTimePerTick = timeDiff;
        } else {
            avgTimePerTick = avgTimePerTick * 0.9 + timeDiff * 0.1; // Weighted average
        }
        
        lastUpdateTime = currentTime;
    }

    private String formatTime(long milliseconds) {
        if (milliseconds < 0) return "Unknown";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }

    private void updateProgressBar() {
        float percentage = (float) current / max;
        int progress = Math.round(percentage * barWidth);
        
        // Calculate ETA
        long elapsed = System.currentTimeMillis() - startTime;
        long eta = current > 0 ? Math.round((max - current) * avgTimePerTick) : 0;
        
        progressBar.setLength(0);
        progressBar.append("\r[");
        for (int i = 0; i < barWidth; i++) {
            progressBar.append(i < progress ? "=" : " ");
        }
        progressBar.append("] ")
                  .append(String.format("%3d", Math.round(percentage * 100)))
                  .append("% | Elapsed: ")
                  .append(formatTime(elapsed))
                  .append(" | ETA: ")
                  .append(formatTime(eta));
        
        System.out.print(progressBar);
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    public boolean isComplete() {
        return current >= max;
    }
}