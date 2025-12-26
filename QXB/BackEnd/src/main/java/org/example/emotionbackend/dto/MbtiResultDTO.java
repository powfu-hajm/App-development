package org.example.emotionbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MBTI 测试结果 DTO
 * 包含四维百分比和详细类型信息
 */
@Data
public class MbtiResultDTO {

    private Long recordId;
    private Long paperId;
    private String paperTitle;
    private Integer totalScore;
    private LocalDateTime testTime;

    // ========== MBTI 专属字段 ==========

    /**
     * MBTI类型代码，如ENTP
     */
    private String mbtiType;

    /**
     * 类型中文名称，如辩论家
     */
    private String typeName;

    /**
     * 类型英文名称，如The Debater
     */
    private String typeNameEn;

    /**
     * 类型分组：分析家/外交家/守护者/探险家
     */
    private String typeGroup;

    /**
     * 简短描述
     */
    private String briefDesc;

    /**
     * 详细描述
     */
    private String detailDesc;

    // ========== 四维百分比数据 ==========

    /**
     * 外向(E) 百分比 0-100
     */
    private Integer ePercent;

    /**
     * 内向(I) 百分比 0-100
     */
    private Integer iPercent;

    /**
     * 感觉(S) 百分比 0-100
     */
    private Integer sPercent;

    /**
     * 直觉(N) 百分比 0-100
     */
    private Integer nPercent;

    /**
     * 思考(T) 百分比 0-100
     */
    private Integer tPercent;

    /**
     * 情感(F) 百分比 0-100
     */
    private Integer fPercent;

    /**
     * 判断(J) 百分比 0-100
     */
    private Integer jPercent;

    /**
     * 知觉(P) 百分比 0-100
     */
    private Integer pPercent;

    // ========== 扩展信息 ==========

    /**
     * 优势特点列表
     */
    private List<String> strengths;

    /**
     * 潜在弱点列表
     */
    private List<String> weaknesses;

    /**
     * 职业建议列表
     */
    private List<String> careerSuggestions;

    /**
     * 著名人物列表
     */
    private List<String> famousPeople;

    /**
     * 图片资源名称
     */
    private String imageName;

    /**
     * 主题色
     */
    private String colorPrimary;

    /**
     * 辅助色
     */
    private String colorSecondary;
}
