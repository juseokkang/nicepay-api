package kr.wadiz.platform.api.nicepay.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int code;           // 결과 코드
    private String message;     // 결과 메세지
    private Object data;        // 상세 내용
}
