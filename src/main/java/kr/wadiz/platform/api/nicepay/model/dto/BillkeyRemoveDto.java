package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class BillkeyRemoveDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Integer userId;
        private String serviceName; // enum 으로 변경
        private Long billkeyId;
        private String bid;
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
    }
}
