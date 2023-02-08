package kr.wadiz.platform.api.nicepay.controller;

import jakarta.validation.Valid;
import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.BillingApprovalDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillingCancelDto;
import kr.wadiz.platform.api.nicepay.service.BillingService;
import kr.wadiz.platform.api.nicepay.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BillingController {
    private final BillingService billingService;

    @PostMapping("/approval")
    public Mono<Response> approval(@Valid @RequestBody BillingApprovalDto.Request request) {
        return billingService.approval(request);
    }

    @PostMapping("/cancel")
    public Mono<Response> cancel(@Valid @RequestBody BillingCancelDto.Request request) {
        return billingService.cancel(request);
    }

    @PostMapping("/tid")
    public void tid() {
        for (int i = 0; i < 10000; i++) {
            log.info("{}", Util.getTid("nictest04m", "01", "16"));
        }
    }
}
