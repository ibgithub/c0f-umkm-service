package com.ib.umkm.service.pos;

import com.ib.umkm.dto.pos.*;
import com.ib.umkm.repository.pos.SalesItemRepository;
import com.ib.umkm.repository.pos.SalesPaymentRepository;
import com.ib.umkm.repository.pos.SalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SalesService {
    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final SalesPaymentRepository salesPaymentRepository;

    public SalesService(SalesRepository salesRepository,  SalesItemRepository salesItemRepository,  SalesPaymentRepository salesPaymentRepository) {
        this.salesRepository = salesRepository;
        this.salesItemRepository = salesItemRepository;
        this.salesPaymentRepository = salesPaymentRepository;
    }

    @Transactional
    public Long createSales(
            SalesCreateRequest req,
            Long cashierId,
            String username
    ) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (SalesItemRequest i : req.getItems()) {

            BigDecimal itemTotal =
                    i.getPrice().multiply(BigDecimal.valueOf(i.getQty()));

            subtotal = subtotal.add(itemTotal);

        }

        BigDecimal total = subtotal;

        Sales sales = new Sales();

        sales.setMerchantId(req.getMerchantId());
        sales.setOutletId(req.getOutletId());
        sales.setCashierId(cashierId);
        sales.setReceiptNo(generateReceipt(req.getMerchantId()));
        sales.setSubtotal(subtotal);
        sales.setTotalAmount(total);
        sales.setPaymentMethod(req.getPaymentMethod());
        sales.setStatus("COMPLETED");
        sales.setPaymentStatus("PAID");
        sales.setCreatedBy(username);

        Long salesId = salesRepository.save(sales);

        for (SalesItemRequest i : req.getItems()) {

            SalesItem item = new SalesItem();

            item.setSalesId(salesId);
            item.setProductId(i.getProductId());
            item.setProductName(i.getProductName());
            item.setQty(i.getQty());
            item.setPrice(i.getPrice());

            BigDecimal subtotalItem =
                    i.getPrice().multiply(BigDecimal.valueOf(i.getQty()));

            item.setSubtotal(subtotalItem);
            item.setCreatedBy(username);

            salesItemRepository.save(item);

        }

        SalesPayment payment = new SalesPayment();

        payment.setSalesId(salesId);
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setAmount(total);

        salesPaymentRepository.save(payment);
        return salesId;
    }

    private String generateReceipt(Long merchantId){

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        long count = salesRepository.countToday(merchantId);

        return "INV-" + date + "-" + String.format("%05d", count + 1);

    }
    public Sales getById(Long id) {
        Sales sales = salesRepository.findById(id);
        List<SalesItem> items = salesRepository.findItemsBySalesId(sales.getId());
        sales.setItems(items);
        return sales;
    }
}
