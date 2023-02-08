package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class BillingCancelDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer userId; // 사용자 ID
        private String serviceName; // enum 으로 변경
        private Integer productId; // 상품 번호
        private String tid;
        private String moid;
        private String cancelAmt;
        private String cancelMsg;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
    public static class Response {
        private String ResultCode;
        private String ResultMsg;
        private String ErrorCD;
        private String ErrorMsg;
        private String CancelAmt;
        @JsonProperty("MID")
        private String MID;
        @JsonProperty("TID")
        private String TID;
        private String Moid;
        private String Signature;
        private String PayMethod;
        private String CancelDate;
        private String CancelTime;
        private String CancelNum;
        private String RemainAmt;
        private String CouponAmt;
        private String ClickpayCl;
        private String MultiCardAcquAmt;
        private String MultiPointAmt;
        private String MultiCouponAmt;
        private String MallReserved;
    }
}
