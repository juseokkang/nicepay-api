package kr.wadiz.platform.api.nicepay.service;

import kr.wadiz.platform.api.nicepay.config.WadizConfig;
import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.BillingApproval;
import kr.wadiz.platform.api.nicepay.model.BillingCancel;
import kr.wadiz.platform.api.nicepay.model.Response;
import kr.wadiz.platform.api.nicepay.model.dto.BillingApprovalDto;
import kr.wadiz.platform.api.nicepay.model.dto.BillingCancelDto;
import kr.wadiz.platform.api.nicepay.publisher.LogPublisher;
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
public class BillingService {
    private static final String BILLING_APPROVAL_SUCCESS = "3001";

    private static final String BILLING_CANCEL_SUCCESS = "2001";
    private static final String EUC_KR = "EUC-KR";

    private static final String JSON = "JSON";

    private final WadizConfig wadizConfig;

    private final WebClient billingWebClient;

    private final LogPublisher logPublisher;

    public Mono<Response> approval(BillingApprovalDto.Request request) {
        Map<String, String> midMap = wadizConfig.getService().get("funding");

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String ediDate = Util.getDateTime();

        String bid = request.getBid(); // bid 조회 기능 개발
        String tid = Util.getTid(mid, "01", "16");

        StringBuilder signData = new StringBuilder();
        signData.append(mid);
        signData.append(ediDate);
        signData.append(request.getMoid());
        signData.append(request.getAmt());
        signData.append(bid);
        signData.append(merchantKey);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("BID", bid);
        formData.add("MID", mid);
        formData.add("TID", tid);
        formData.add("EdiDate", ediDate);
        formData.add("Moid", request.getMoid());
        formData.add("Amt", request.getAmt());
        formData.add("GoodsName", request.getGoodsName());
        formData.add("SignData", Util.signData(signData.toString()));
        formData.add("CardInterest", request.getCardInterest());
        formData.add("CardQuota", request.getCardQuota());
        formData.add("CardPoint", request.getCardPoint());
        formData.add("CharSet", StandardCharsets.UTF_8.name());
        formData.add("EdiType", JSON);

        Mono<BillingApprovalDto.Response> responseMono = billingWebClient
                .post()
                .uri("/billing/billing_approve.jsp")
                .header("Accept-Charset", EUC_KR)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=EUC-KR")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(BillingApprovalDto.Response.class);

        return responseMono.map(response -> {
            ResultCode resultCode;
            String message = response.getResultMsg();

            if (response.getResultCode().equals(BILLING_APPROVAL_SUCCESS)) {
                resultCode = ResultCode.SUCCESS;
            } else {
                resultCode = ResultCode.BILLING_APPROVAL_FAIL;
            }

            logPublisher.sendBillingApproval(BillingApproval.builder()
                            .resultCode(resultCode)
                            .message(message)
                            .request(request)
                            .response(response)
                    .build());

            return Response.builder()
                            .code(resultCode.getValue())
                            .message(message)
                            .data(response)
                    .build();
        });
    }

    public Mono<Response> cancel(BillingCancelDto.Request request) {
        Map<String, String> midMap = wadizConfig.getService().get("funding");

        String mid = midMap.get("mid");
        String merchantKey = midMap.get("merchantKey");

        String ediDate = Util.getDateTime();

        StringBuilder signData = new StringBuilder();
        signData.append(mid);
        signData.append(request.getCancelAmt());
        signData.append(ediDate);
        signData.append(merchantKey);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("TID", request.getTid());
        formData.add("MID", mid);
        formData.add("Moid", request.getMoid());
        formData.add("CancelAmt", request.getCancelAmt());
        formData.add("CancelMsg", request.getCancelMsg());
        formData.add("PartialCancelCode", "0");
        formData.add("EdiDate", ediDate);
        formData.add("SignData", Util.signData(signData.toString()));
        formData.add("CharSet", StandardCharsets.UTF_8.name());
        formData.add("EdiType", JSON);

        Mono<BillingCancelDto.Response> responseMono = billingWebClient
                .post()
                .uri("/cancel_process.jsp")
                .header("Accept-Charset", EUC_KR)
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=EUC-KR")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(BillingCancelDto.Response.class);

        return responseMono.map(response -> {
            ResultCode resultCode;
            String message = response.getResultMsg();

            if (response.getResultCode().equals(BILLING_CANCEL_SUCCESS)) {
                resultCode = ResultCode.SUCCESS;
            } else {
                resultCode = ResultCode.BILLING_CANCEL_FAIL;
            }

            logPublisher.sendBillingCancel(BillingCancel.builder()
                            .resultCode(resultCode)
                            .message(message)
                            .request(request)
                            .response(response)
                    .build());

            return Response.builder()
                            .code(resultCode.getValue())
                            .message(message)
                            .data(response)
                    .build();
        });
    }
}
