package kr.wadiz.platform.api.nicepay.service;

import kr.wadiz.platform.api.nicepay.config.WadizConfig;
import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.BillkeyRegister;
import kr.wadiz.platform.api.nicepay.model.BillkeyRemove;
import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRegisterDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRemoveDto;
import kr.wadiz.platform.api.nicepay.model.entity.Billkey;
import kr.wadiz.platform.api.nicepay.publisher.LogPublisher;
import kr.wadiz.platform.api.nicepay.repository.BillkeyRepository;
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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BillkeyService {
    private static final String EUC_KR = "EUC-KR";

    private static final String MOID = "wadiz";

    private static final String JSON = "JSON";

    private static final String BILLKEY_REGIST_SUCCESS = "F100";

    private static final String BILLKEY_REMOVE_SUCCESS = "F101";

    private final WadizConfig wadizConfig;

    private final LogPublisher logPublisher;

    private final WebClient billingWebClient;

    private final BillkeyRepository billkeyRepository;

    public Mono<Response> registerBillkey(BillkeyRegisterDto.Request request) {
        Integer userId = request.getUserId();
        String cardNo = request.getCardNo();

        return billkeyRepository.findByUserIdAndCardNo(userId, cardNo)
                .map(billkey -> {
                        return Response.builder()
                                .code(ResultCode.BILLKEY_REGIST_FAIL.getValue())
                                .message("이미 등록된 카드입니다")
                                .build();
                }).switchIfEmpty(Mono.defer(() -> {
                    return registerBillkeyNicepay(request)
                            .flatMap(response -> {
                                    ResultCode resultCode;
                                    String message = response.getResultMsg();

                                    Mono<Billkey> billkeyMono = null;

                                    if (response.getResultCode().equals(BILLKEY_REGIST_SUCCESS)) {
                                        resultCode = ResultCode.SUCCESS;

                                        try {
                                            billkeyMono = billkeyRepository.save(Billkey.builder()
                                                    .bid(response.getBID())
                                                    .userId(request.getUserId())
                                                    .cardNo(request.getCardNo())
                                                    .expYear(request.getExpYear())
                                                    .expMonth(request.getExpMonth())
                                                    .authDate(response.getAuthDate())
                                                    .cardCode(response.getCardCode())
                                                    .cardName(response.getCardName())
                                                    .build());
                                        } catch (Exception e) {
                                            String errorMessage = "[DB] 발키 저장 중 에러가 발생했습니다";
                                            log.error(errorMessage);
                                            return Mono.just(Response.builder()
                                                    .code(ResultCode.BILLKEY_REGIST_FAIL.getValue())
                                                    .message(errorMessage)
                                                    .build());
                                        }
                                    } else {
                                        resultCode = ResultCode.BILLKEY_REGIST_FAIL;
                                    }

                                    request.setCardPw(null);

                                    logPublisher.sendBillkeyRegister(BillkeyRegister.builder()
                                                    .resultCode(resultCode)
                                                    .message(message)
                                                    .request(request)
                                                    .response(response)
                                            .build());

                                    return billkeyMono.map(billkey -> {
                                        return Response.builder()
                                                .code(resultCode.getValue())
                                                .message(message)
                                                .data(billkey.getId())
                                                .build();
                                    });
                            });
                }));
    }

    private Mono<BillkeyRegisterDto.Response> registerBillkeyNicepay(BillkeyRegisterDto.Request request) {
        Map<String, String> midMap = wadizConfig.getService().get(request.getServiceName());

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String ediDate = Util.getDateTime();

        StringBuilder signData = new StringBuilder();
        signData.append(mid);
        signData.append(ediDate);
        signData.append(MOID);
        signData.append(merchantKey);

        StringBuilder encData = new StringBuilder();
        encData.append("CardNo=");
        encData.append(request.getCardNo());
        encData.append("&ExpYear=");
        encData.append(request.getExpYear());
        encData.append("&ExpMonth=");
        encData.append(request.getExpMonth());
        encData.append("&IDNo=");
        encData.append(request.getIdNo());
        encData.append("&CardPw=");
        encData.append(request.getCardPw());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("MID", mid);
        formData.add("EdiDate", ediDate);
        formData.add("Moid", MOID);
        formData.add("EncData", Util.encData(encData.toString(), merchantKey));
        formData.add("SignData", Util.signData(signData.toString()));
        formData.add("CharSet", StandardCharsets.UTF_8.name());
        formData.add("EdiType", JSON);

        return billingWebClient
                .post()
                .uri("/billing/billing_regist.jsp")
                .header("Accept-Charset", EUC_KR)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(BillkeyRegisterDto.Response.class);
    }

    public Mono<Response> removeBillkey(BillkeyRemoveDto.Request request) {
        return billkeyRepository.findById(request.getBillkeyId())
                .flatMap(billkey -> {
                        request.setBid(billkey.getBid());
                        Mono<BillkeyRemoveDto.Response> responseMono = removeBillkeyNicepay(request);

                        return responseMono.map(response -> {
                            ResultCode resultCode;
                            String message = response.getResultMsg();

                            if (response.getResultCode().equals(BILLKEY_REMOVE_SUCCESS)) {
                                resultCode = ResultCode.SUCCESS;

                                try {
                                    billkeyRepository.delete(billkey).subscribe();
                                } catch (Exception e) {
                                    String errorMessage = "[DB] 발키 삭제 중 에러가 발생했습니다";
                                    log.error(errorMessage);
                                    return Response.builder()
                                            .code(ResultCode.BILLKEY_REGIST_FAIL.getValue())
                                            .message(errorMessage)
                                            .build();
                                }
                            } else {
                                resultCode = ResultCode.BILLKEY_REMOVE_FAIL;
                            }

                            logPublisher.sendBillkeyRemove(BillkeyRemove.builder()
                                    .resultCode(resultCode)
                                    .message(message)
                                    .request(request)
                                    .response(response)
                                    .build());

                            return Response.builder()
                                    .code(resultCode.getValue())
                                    .message(message)
                                    .build();
                        });
                }).switchIfEmpty(Mono.defer(() -> {
                        return Mono.just(Response.builder()
                                .code(ResultCode.BILLKEY_REMOVE_FAIL.getValue())
                                .message("조회된 빌키가 없습니다.")
                                .build());
                }));
    }

    private Mono<BillkeyRemoveDto.Response> removeBillkeyNicepay(BillkeyRemoveDto.Request request) {
        Map<String, String> midMap = wadizConfig.getService().get(request.getServiceName());

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String ediDate = Util.getDateTime();

        StringBuilder signData = new StringBuilder();
        signData.append(mid);
        signData.append(ediDate);
        signData.append(MOID);
        signData.append(request.getBid());
        signData.append(merchantKey);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("BID", request.getBid());
        formData.add("MID", mid);
        formData.add("EdiDate", ediDate);
        formData.add("Moid", MOID);
        formData.add("SignData", Util.signData(signData.toString()));
        formData.add("CharSet", StandardCharsets.UTF_8.name());
        formData.add("EdiType", JSON);

        return billingWebClient
                .post()
                .uri("/billing/billkey_remove.jsp")
                .header("Accept-Charset", EUC_KR)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(BillkeyRemoveDto.Response.class);
    }
}
