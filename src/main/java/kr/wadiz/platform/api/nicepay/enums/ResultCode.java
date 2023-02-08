package kr.wadiz.platform.api.nicepay.enums;

public enum ResultCode {
    SUCCESS(2000),                  // 성공
    INVALID_PARAM(4000),            // 파라미터 오류
    BILLKEY_REGIST_FAIL(4001),       // 빌키 생성 실패
    BILLKEY_REMOVE_FAIL(4002),      // 빌키 해지 실패
    BILLING_APPROVAL_FAIL(4003),            // 결제 승인 실패
    BILLING_CANCEL_FAIL(4004),              // 결제 취소 실패
    EXTERNAL_SERVER_ERROR(5001);    // 외부 통신 오류
    private final int value;

    ResultCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
