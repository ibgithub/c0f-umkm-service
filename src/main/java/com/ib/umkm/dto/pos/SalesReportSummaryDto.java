package com.ib.umkm.dto.pos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesReportSummaryDto {
    private String merchantName;
    private Long outletId;
    private String outletName;
    private LocalDate salesDate;
    private BigDecimal totalAmount;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public LocalDate getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(LocalDate salesDate) {
        this.salesDate = salesDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
