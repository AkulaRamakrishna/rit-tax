package com.rogers.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "RCI_TAX_RATE_RULE")
public class TaxRateRule {

    @Id
    @Column(name = "RCI_TAX_RATE_RULE_SEQ_NO")
    private Long taxRateRuleId;

    @Column(name = "Tax_Location_Id")
    private String taxLocationId;

    @Column(name = "Tax_Group_Id")
    private String taxGroupId;

    @Column(name = "TAX_RULE_SEQ_NO")
    private Long taxRuleId;

    @Column(name = "TAX_RATE_MIN_AMT")
    private BigDecimal taxRateMinAmt;

    @Column(name = "TAX_RATE_MAX_AMT")
    private BigDecimal taxRateMaxAmt;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PERCENT")
    private BigDecimal percent;

    @Column(name = "ACTION_CODE")
    private String actionCode;

    @Column(name = "RECORD_IDENTIFIER")
    private String recordIdentifier;

    @Column(name = "BREAK_POINT_TYPE_CODE")
    private String breakPointTypeCode;

    @Column(name = "EFFECTIVE_DATETIME")
    private OffsetDateTime effectiveDateTime;

    @Column(name = "EXPR_DATETIME")
    private OffsetDateTime expryDateTime;

    @Column(name = "DAILY_START_TIME")
    private OffsetDateTime dailyStartTime;

    @Column(name = "DAILY_END_TIME")
    private OffsetDateTime dailyEndTime;

    @Column(name = "TAX_BRACKET_ID")
    private String taxBracketId;

    @Column(name = "ORG_CODE")
    private String organizationCode;

    @Column(name = "ORG_VALUE")
    private String organizationValue;

}
