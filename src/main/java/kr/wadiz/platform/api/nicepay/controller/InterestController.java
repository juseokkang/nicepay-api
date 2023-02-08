package kr.wadiz.platform.api.nicepay.controller;

import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
public class InterestController {
    private final InterestService interestService;

    @GetMapping("/interest/free")
    public Mono<Response> getInterestFree() {
        return interestService.getInterestFree();
    }

    @GetMapping("/interest/part")
    public Mono<Response> getInterestPart(@RequestParam String cardCd) {
        return interestService.getInterestPart(cardCd);
    }

    @GetMapping("/interest/part/all")
    public Mono<Response> getInterestPartAll() {
        return interestService.getInterestPartAll();
    }
}
