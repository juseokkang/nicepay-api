package kr.wadiz.platform.api.nicepay;

import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.BillingApprovalDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillingCancelDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRegisterDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRemoveDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;

@Slf4j
@DisplayName("비인증 결제 테스트")
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BillingTest {
    @Autowired
    private WebTestClient webTestClient;

    private String bid;

    private String tid;

    @Test
    @DisplayName("빌키 등록")
    @Order(1)
    public void registBillkeyTest() throws Exception {
        log.info("{}", 1);

        FluxExchangeResult<Response> responseFluxExchangeResult = this.webTestClient.post().uri("/billkey")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BillkeyRegisterDto.Request.builder()
                        .userId(1)
                        .serviceName("funding")
                        .cardNo("4327680000039088")
                        .cardPw("32")
                        .expYear("28")
                        .expMonth("09")
                        .idNo("820117")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Response.class);

        Flux<Response> responseFlux = responseFluxExchangeResult.getResponseBody();

        responseFlux.map(m -> {
            log.info("{}", m.getData());

            LinkedHashMap map = (LinkedHashMap) m.getData();

            Assertions.assertEquals("F100", map.get("ResultCode"));

            log.info("{}", map.get("ResultCode"));
            log.info("{}", map.get("BID"));

            bid = (String) map.get("BID");

            return m;
        }).subscribe();
    }

    @Test
    @DisplayName("결제 승인")
    @Order(2)
    public void approvalTest() throws Exception {
        log.info("{}", 2);

        FluxExchangeResult<Response> responseFluxExchangeResult = this.webTestClient.post().uri("/approval")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BillingApprovalDto.Request.builder()
                        .userId(1)
                        .serviceName("funding")
                        .productId(1)
                        .bid(bid)
                        .moid("order_1")
                        .amt("1000")
                        .goodsName("카이막")
                        .cardInterest("0")
                        .cardQuota("00")
                        .cardPoint("0")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Response.class);

        Flux<Response> responseFlux = responseFluxExchangeResult.getResponseBody();

        responseFlux.map(m -> {
            log.info("{}", m.getData());

            LinkedHashMap map = (LinkedHashMap) m.getData();

            Assertions.assertEquals("3001", map.get("ResultCode"));

            log.info("{}", map.get("ResultCode"));
            log.info("{}", map.get("TID"));

            tid = (String) map.get("TID");

            return m;
        }).subscribe();
    }

    @Test
    @DisplayName("결제 취소")
    @Order(3)
    public void cancelTest() throws Exception {
        log.info("{}", 3);

        FluxExchangeResult<Response> responseFluxExchangeResult = this.webTestClient.post().uri("/cancel")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BillingCancelDto.Request.builder()
                        .userId(1)
                        .serviceName("funding")
                        .productId(1)
                        .tid(tid)
                        .moid("order_1")
                        .cancelAmt("1000")
                        .cancelMsg("취소 테스트")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Response.class);

        Flux<Response> responseFlux = responseFluxExchangeResult.getResponseBody();

        responseFlux.map(m -> {
            log.info("{}", m.getData());

            LinkedHashMap map = (LinkedHashMap) m.getData();

            Assertions.assertEquals("2001", map.get("ResultCode"));

            log.info("{}", map.get("ResultCode"));
            log.info("{}", map.get("TID"));

            return m;
        }).subscribe();
    }

    @Test
    @DisplayName("빌키 삭제")
    @Order(4)
    public void removeBillkeyTest() throws Exception {
        log.info("{}", 4);

        FluxExchangeResult<Response> responseFluxExchangeResult = this.webTestClient.method(HttpMethod.DELETE)
                .uri("/billkey")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BillkeyRemoveDto.Request.builder()
                        .userId(1)
                        .serviceName("funding")
                        .bid(bid)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Response.class);

        Flux<Response> responseFlux = responseFluxExchangeResult.getResponseBody();

        responseFlux.map(m -> {
            log.info("{}", m.getData());

            LinkedHashMap map = (LinkedHashMap) m.getData();

            Assertions.assertEquals("F101", map.get("ResultCode"));

            log.info("{}", map.get("ResultCode"));
            log.info("{}", map.get("TID"));
            log.info("{}", map.get("BID"));

            return m;
        }).subscribe();
    }
}
