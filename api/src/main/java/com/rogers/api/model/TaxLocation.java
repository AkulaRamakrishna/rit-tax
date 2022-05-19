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
@Table(name = "RCI_TAX_LOCATION")
public class TaxLocation {

    @Id
    @Column(name = "TAX_LOCATION_ID")
    private String taxLocationId;

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
