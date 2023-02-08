package kr.wadiz.platform.api.nicepay.model;

import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.dto.BillingApprovalDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingApproval {
    private ResultCode resultCode;

    private String message;

    private BillingApprovalDto.Request request;

    private BillingApprovalDto.Response response;
}
