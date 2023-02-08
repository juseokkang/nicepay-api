package kr.wadiz.platform.api.nicepay.model;

import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.dto.BillingCancelDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCancel {
    private ResultCode resultCode;

    private String message;

    private BillingCancelDto.Request request;

    private BillingCancelDto.Response response;
}
