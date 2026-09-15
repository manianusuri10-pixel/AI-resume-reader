package com.aicopilot.dto;

public class BulletRewriteDto {
    private String original;
    private String improved;
    private String rationale;

    public BulletRewriteDto() {}

    public BulletRewriteDto(String original, String improved, String rationale) {
        this.original = original;
        this.improved = improved;
        this.rationale = rationale;
    }

    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }

    public String getImproved() { return improved; }
    public void setImproved(String improved) { this.improved = improved; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
}
