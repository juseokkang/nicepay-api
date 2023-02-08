package kr.wadiz.platform.api.nicepay.service;

import kr.wadiz.platform.api.nicepay.config.WadizConfig;
import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.InterestFreeDto;
import kr.wadiz.platform.api.nicepay.model.dto.InterestPartDto;
import kr.wadiz.platform.api.nicepay.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class InterestService {
    private static final String EUC_KR = "EUC-KR";
    private static final String SID = "0201001";

    private static final String GUBUN = "S"; // S : 요청, R : 응답

    private static final String INTEREST_SUCCESS = "0000";

    private static final List<String> CARD_LIST = List.of("01", "02", "03", "04", "06", "07", "08", "11", "12", "15", "22"); // 01:비씨, 02:KB국민, 03:하나(외환), 04:삼성, 06:신한, 07:현대, 08:롯데, 11:씨티, 12:NH채움, 15:우리, 16:하나, 22:전북

    private final WadizConfig wadizConfig;

    private final WebClient billingWebClient;

    private final WebClient interestWebClient;

    public Mono<Response> getInterestFree() {
        Map<String, String> midMap = wadizConfig.getService().get("store_auth");

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String trDtm = Util.getDateTime();

        StringBuilder signData = new StringBuilder();
        signData.append(SID);
        signData.append(mid);
        signData.append(trDtm);
        signData.append(merchantKey);

        InterestFreeDto.Request request = InterestFreeDto.Request.builder()
                .header(InterestFreeDto.Header.builder()
                        .sid(SID)
                        .trDtm(trDtm)
                        .gubun(GUBUN)
                        .build())
                .body(InterestFreeDto.Request.Body.builder()
                        .mid(mid)
                        .encKey(Util.signData(signData.toString()))
                        .targetDt(Util.getDate())
                        .build())
                .build();

        Mono<InterestFreeDto.Response> responseMono = interestWebClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InterestFreeDto.Response.class);

        return responseMono.map(response -> {
            ResultCode resultCode;
            String message = null;

            if (response.getHeader().getResCode().equals(INTEREST_SUCCESS)) {
                resultCode = ResultCode.SUCCESS;
            } else {
                resultCode = ResultCode.BILLING_APPROVAL_FAIL;
                message = response.getHeader().getResMsg();
            }

            log.info("카드 무이자 : {}", response);

            return Response.builder()
                            .code(resultCode.getValue())
                            .message(message)
                            .data(response.getBody().getData())
                    .build();
        });
    }

    public Mono<Response> getInterestPart(String cardCd) {
        Mono<InterestPartDto.Response> responseMono = getInterestPartByCardCd(cardCd);

        return responseMono.map(response -> {
            log.info("카드 부분 무이자 : {}", response);

            return Response.builder()
                            .code(ResultCode.SUCCESS.getValue())
                            .data(response)
                    .build();
        });
    }

    public Mono<InterestPartDto.Response> getInterestPartByCardCd(String cardCd) {
        Map<String, String> midMap = wadizConfig.getService().get("store_auth");

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String ediDate = Util.getDateTime();
        String queryDate = Util.getDate();

        StringBuilder signData = new StringBuilder();
        signData.append(mid);
        signData.append(ediDate);
        signData.append(cardCd);
        signData.append(queryDate);
        signData.append(merchantKey);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("MID", mid);
        formData.add("EdiDate", ediDate);
        formData.add("SignData", Util.signData(signData.toString()));
        formData.add("CharSet", StandardCharsets.UTF_8.name());
        formData.add("CardCd", cardCd);
        formData.add("QueryDate", queryDate);

        return billingWebClient.post()
                .uri("/inquery/event_card_part.jsp")
                .header("Accept-Charset", EUC_KR)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=EUC-KR")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(InterestPartDto.Response.class);
    }

    public Mono<Response> getInterestPartAll() {
        List<Mono<InterestPartDto.Response>> monoList = new ArrayList<>();

        for (int i = 0; i < CARD_LIST.size(); i++) {
            String cardCd = CARD_LIST.get(i);

            Mono<InterestPartDto.Response> responseMono = getInterestPartByCardCd(cardCd);
            monoList.add(responseMono);
        }

        return Mono.zip(monoList, response -> {
            List<InterestPartDto.Response.EventCardPart> list = new ArrayList();

            for (int i = 0; i < response.length; i++) {
                InterestPartDto.Response o = (InterestPartDto.Response) response[i];
                if (o.getResultCode().equals("0000")) {
                    list.addAll(o.getEventCardPart());
                }
            }

            log.info("카드 부분 무이자 (전체) : {}", response);

            return Response.builder()
                            .code(ResultCode.SUCCESS.getValue())
                            .data(list)
                    .build();
        });
    }

    public Mono<Map<String, List>> getInterestPartAll2() {
        List<Mono<InterestPartDto.Response>> list = new ArrayList<>();

        for (int i = 0; i < CARD_LIST.size(); i++) {
            String cardCd = CARD_LIST.get(i);

            Mono<InterestPartDto.Response> responseMono = getInterestPartByCardCd(cardCd);
            list.add(responseMono);
        }

        return Mono.zip(list, response -> {
            Map<String, List> map = new ConcurrentHashMap<>();

            for (int i = 0; i < response.length; i++) {
                InterestPartDto.Response o = (InterestPartDto.Response) response[i];
                if (o.getResultCode().equals("0000")) {
                    map.put(o.getEventCardPart().get(0).getCardCd(), o.getEventCardPart());
                }
            }

            return map;
        });
    }
}
