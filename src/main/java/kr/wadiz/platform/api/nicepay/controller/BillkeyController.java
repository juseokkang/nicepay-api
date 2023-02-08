package kr.wadiz.platform.api.nicepay.controller;

import jakarta.validation.Valid;
import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRegisterDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRemoveDto;
import kr.wadiz.platform.api.nicepay.service.BillkeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BillkeyController {
    private final BillkeyService billkeyService;

    @PostMapping("/billkey")
    public Mono<Response> registBillkey(@Valid @RequestBody BillkeyRegisterDto.Request request) {
        return billkeyService.registerBillkey(request);
    }

    @DeleteMapping("/billkey")
    public Mono<Response> removeBillkey(@Valid @RequestBody BillkeyRemoveDto.Request request) {
        return billkeyService.removeBillkey(request);
    }
}
