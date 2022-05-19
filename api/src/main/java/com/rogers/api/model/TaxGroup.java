package com.rogers.api.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data


@Entity
@Table(name = "RCI_TAX_GROUP")
public class TaxGroup {

    @Id
    @Column(name = "TAX_GROUP_ID")
    private String taxGroupId;

    @Column(name = "ACTION_CODE")
    private String actionCode;

    @Column(name = "RECORD_IDENTIFIER")
    private String recordIdentifier;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ORG_CODE")
    private String organizationCode;

    @Column(name = "ORG_VALUE")
    private String organizationValue;

}
