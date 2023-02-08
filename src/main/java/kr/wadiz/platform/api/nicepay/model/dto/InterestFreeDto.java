package kr.wadiz.platform.api.nicepay.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class InterestFreeDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Request {
        private Header header;
        private Body body;

        @Getter
        @Setter
        @SuperBuilder
        @RequiredArgsConstructor
        public static class Body {
            private String mid;
            private String encKey;
            private String targetDt;
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Response {
        private Header header;
        private Body body;

        @Getter
        @Setter
        @RequiredArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private String mid;
            private String targetDt;
            private Integer dataCnt;
            private List<Detail> data;
        }

        @Getter
        @Setter
        @RequiredArgsConstructor
        static class Detail {
            private String fnCd;
            private String fnNm;
            private String instmntMon;
            private String instmntType;
            private Integer minAmt;
        }

//        @Getter
//        @Setter
//        @RequiredArgsConstructor
//        static class ReqInfo {
//            private Header header;
//            private Body body;
//        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @RequiredArgsConstructor
    public static class Header {
        private String sid;
        private String trDtm;
        private String gubun;
        private String resCode;
        private String resMsg;
    }
}
