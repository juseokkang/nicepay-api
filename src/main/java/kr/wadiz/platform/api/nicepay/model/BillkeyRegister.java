package kr.wadiz.platform.api.nicepay.model;

import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRegisterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillkeyRegister {
    private ResultCode resultCode;

    private String message;

    private BillkeyRegisterDto.Request request;

    private BillkeyRegisterDto.Response response;
}
