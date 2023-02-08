package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class BillingApprovalDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer userId; // 사용자 ID
        private String serviceName; // enum 으로 변경
        private Integer productId; // 상품 번호
        private String bid;
        private String moid;
        private String amt;
        private String goodsName;
        private String cardInterest; // 무이자 사용여부
        private String cardQuota; // 할부개월
        private String cardPoint; // 카드 포인트 사용여부
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
    public static class Response {
        private String ResultCode;
        private String ResultMsg;
        @JsonProperty("MID")
        private String MID;
        @JsonProperty("TID")
        private String TID;
        private String Moid;
        private String Amt;
        private String GoodsName;
        private String AuthCode;
        private String AuthDate;
        private String AcquCardCode;
        private String AcquCardName;
        private String CardNo;
        private String CardCode;
        private String CardName;
        private String CardQuota;
        private String CardCl;
        private String CardInterest;
        private String CcPartCl;
        private String MallReserved;
        private String BuyerName;
    }
}
