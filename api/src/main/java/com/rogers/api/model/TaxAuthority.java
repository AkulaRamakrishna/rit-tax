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
@Table(name = "RCI_TAX_AUTHORITY")
public class TaxAuthority {

    @Id
    @Column(name = "TAX_AUTHORITY_ID")
    private String taxAuthorityId;

    @Column(name = "ACTION_CODE")
    private String actionCode;

    @Column(name = "RECORD_IDENTIFIER")
    private String recordIdentifier;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ROUNDING_CODE")
    private String roundingCode;

    @Column(name = "ROUNDING_DIGITS_QUANTITY")
    private Short roundingDigitsQuantity;

    @Column(name = "ORG_CODE")
    private String organizationCode;

    @Column(name = "ORG_VALUE")
    private String organizationValue;

}
