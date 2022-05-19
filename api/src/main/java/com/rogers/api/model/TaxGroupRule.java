package com.rogers.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "RCI_TAX_GROUP_RULE")
public class TaxGroupRule {

    @Id
    @Column(name = "TAX_RULE_SEQ_NO")
    private Long taxGroupRuleId;

    @Column(name = "Tax_Location_Id")
    private String taxLocationId;

    @Column(name = "Tax_Group_Id")
    private String taxGroupId;

    @Column(name = "ACTION_CODE")
    private String actionCode;

    @Column(name = "RECORD_IDENTIFIER")
    private String recordIdentifier;


    @Column(name = "NAME")
    private String name;

    @Column(name = "TAX_TYPE_CODE")
    private String taxTypeCode;

    @Column(name = "TAX_AUTHORITY_ID")
    private String taxAuthorityId;

    @Column(name = "TAX_AT_TRANS_LEVEL_FLAG")
    private String taxAtTransLevelFlag;

    @Column(name = "COMPOUND_FLAG")
    private String compoundFlag;

    @Column(name = "COMPOUND_SEQ_NO")
    private String compoundId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ORG_CODE")
    private String organizationCode;

    @Column(name = "ORG_VALUE")
    private String organizationValue;

}
