package com.saveetha.kanchi_wave_hub.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.saveetha.kanchi_wave_hub.model.payment;
import com.saveetha.kanchi_wave_hub.service.paymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private paymentService paymentService;

    @PostMapping("/add")
    public payment addPayment(@RequestBody payment payment) {

        System.out.println("productId "+payment.getProductId());
        return paymentService.addPayment(payment);
    }

}
