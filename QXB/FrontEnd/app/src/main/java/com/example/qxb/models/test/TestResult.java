package com.example.qxb.models.test;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TestResult implements Serializable {
    @SerializedName("recordId")
    private Long recordId;

    @SerializedName("paperId")
    private Long paperId;

    @SerializedName("paperTitle")
    private String paperTitle;

    @SerializedName("totalScore")
    private Integer totalScore;

    @SerializedName("resultLevel")
    private String resultLevel;

    @SerializedName("resultTitle")
    private String resultTitle;

    @SerializedName("resultDescription")
    private String resultDescription;

    @SerializedName("suggestion")
    private String suggestion;

    @SerializedName("testTime")
    private String testTime;

    // ========== MBTI 专属字段 ==========

    @SerializedName("isMbtiResult")
    private Boolean isMbtiResult;

    @SerializedName("mbtiType")
    private String mbtiType;

    @SerializedName("typeName")
    private String typeName;

    @SerializedName("typeNameEn")
    private String typeNameEn;

    @SerializedName("typeGroup")
    private String typeGroup;

    @SerializedName("briefDesc")
    private String briefDesc;

    @SerializedName("detailDesc")
    private String detailDesc;

    // ========== 四维百分比数据 ==========

    @SerializedName("ePercent")
    private Integer ePercent;

    @SerializedName("iPercent")
    private Integer iPercent;

    @SerializedName("sPercent")
    private Integer sPercent;

    @SerializedName("nPercent")
    private Integer nPercent;

    @SerializedName("tPercent")
    private Integer tPercent;

    @SerializedName("fPercent")
    private Integer fPercent;

    @SerializedName("jPercent")
    private Integer jPercent;

    @SerializedName("pPercent")
    private Integer pPercent;

    // ========== MBTI 扩展信息 ==========

    @SerializedName("strengths")
    private List<String> strengths;

    @SerializedName("weaknesses")
    private List<String> weaknesses;

    @SerializedName("careerSuggestions")
    private List<String> careerSuggestions;

    @SerializedName("famousPeople")
    private List<String> famousPeople;

    @SerializedName("imageName")
    private String imageName;

    @SerializedName("colorPrimary")
    private String colorPrimary;

    @SerializedName("colorSecondary")
    private String colorSecondary;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(String resultLevel) {
        this.resultLevel = resultLevel;
    }

    public String getResultTitle() {
        return resultTitle;
    }

    public void setResultTitle(String resultTitle) {
        this.resultTitle = resultTitle;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    // ========== MBTI 字段 Getter/Setter ==========

    public Boolean getIsMbtiResult() {
        return isMbtiResult != null ? isMbtiResult : false;
    }

    public void setIsMbtiResult(Boolean isMbtiResult) {
        this.isMbtiResult = isMbtiResult;
    }

    public String getMbtiType() {
        return mbtiType;
    }

    public void setMbtiType(String mbtiType) {
        this.mbtiType = mbtiType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeNameEn() {
        return typeNameEn;
    }

    public void setTypeNameEn(String typeNameEn) {
        this.typeNameEn = typeNameEn;
    }

    public String getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(String typeGroup) {
        this.typeGroup = typeGroup;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public Integer getEPercent() {
        return ePercent != null ? ePercent : 50;
    }

    public void setEPercent(Integer ePercent) {
        this.ePercent = ePercent;
    }

    public Integer getIPercent() {
        return iPercent != null ? iPercent : 50;
    }

    public void setIPercent(Integer iPercent) {
        this.iPercent = iPercent;
    }

    public Integer getSPercent() {
        return sPercent != null ? sPercent : 50;
    }

    public void setSPercent(Integer sPercent) {
        this.sPercent = sPercent;
    }

    public Integer getNPercent() {
        return nPercent != null ? nPercent : 50;
    }

    public void setNPercent(Integer nPercent) {
        this.nPercent = nPercent;
    }

    public Integer getTPercent() {
        return tPercent != null ? tPercent : 50;
    }

    public void setTPercent(Integer tPercent) {
        this.tPercent = tPercent;
    }

    public Integer getFPercent() {
        return fPercent != null ? fPercent : 50;
    }

    public void setFPercent(Integer fPercent) {
        this.fPercent = fPercent;
    }

    public Integer getJPercent() {
        return jPercent != null ? jPercent : 50;
    }

    public void setJPercent(Integer jPercent) {
        this.jPercent = jPercent;
    }

    public Integer getPPercent() {
        return pPercent != null ? pPercent : 50;
    }

    public void setPPercent(Integer pPercent) {
        this.pPercent = pPercent;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getCareerSuggestions() {
        return careerSuggestions;
    }

    public void setCareerSuggestions(List<String> careerSuggestions) {
        this.careerSuggestions = careerSuggestions;
    }

    public List<String> getFamousPeople() {
        return famousPeople;
    }

    public void setFamousPeople(List<String> famousPeople) {
        this.famousPeople = famousPeople;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public String getColorSecondary() {
        return colorSecondary;
    }

    public void setColorSecondary(String colorSecondary) {
        this.colorSecondary = colorSecondary;
    }
}
