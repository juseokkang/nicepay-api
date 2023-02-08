package kr.wadiz.platform.api.nicepay.model;

import kr.wadiz.platform.api.nicepay.enums.ResultCode;
import kr.wadiz.platform.api.nicepay.model.dto.BillkeyRemoveDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillkeyRemove {
    private ResultCode resultCode;

    private String message;

    private BillkeyRemoveDto.Request request;

    private BillkeyRemoveDto.Response response;
}
