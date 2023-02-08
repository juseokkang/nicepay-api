package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

public class InterestPartDto {
    @Getter
    @Setter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
    public static class Response {
        private String ResultCode;
        private String ResultMsg;
        @JsonProperty("MID")
        private String MID;
        private String QueryDate;
        private List<EventCardPart> EventCardPart;

        @Getter
        @Setter
        @RequiredArgsConstructor
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
        public static class EventCardPart {
            private String CardCd;
            private String CardNm;
            private String InstmnMm;
            private String CustInstmnMm;
            private String EventFrDt;
            private String EventToDt;
            private String ApplyMsg;
            private String Memo;
        }
    }
}
