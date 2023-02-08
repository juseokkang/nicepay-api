package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class BillkeyRegisterDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer userId;
        private String serviceName; // enum 으로 변경
        private String cardNo;
        private String cardPw;
        private String expYear;
        private String expMonth;
        private String idNo;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
    public static class Response {
        private String ResultCode;
        private String ResultMsg;
        @JsonProperty("TID")
        private String TID;
        @JsonProperty("BID")
        private String BID;
        private String AuthDate;
        private String CardCode;
        private String CardName;
        private String CardCl;
        private String AcquCardCode;
        private String AcquCardName;
    }
}
