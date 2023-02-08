package kr.wadiz.platform.api.nicepay.publisher;

import kr.wadiz.platform.api.nicepay.model.BillingApproval;
import kr.wadiz.platform.api.nicepay.model.BillingCancel;
import kr.wadiz.platform.api.nicepay.model.BillkeyRegister;
import kr.wadiz.platform.api.nicepay.model.BillkeyRemove;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LogPublisher {
    private final StreamBridge streamBridge;

    @Async
    public void sendBillkeyRegister(BillkeyRegister billkeyRegister) {
        streamBridge.send("billkeyRegister-out-0", MessageBuilder.withPayload(billkeyRegister).build());
    }

    @Async
    public void sendBillkeyRemove(BillkeyRemove billkeyRemove) {
        streamBridge.send("billkeyRemove-out-0", MessageBuilder.withPayload(billkeyRemove).build());
    }

    @Async
    public void sendBillingApproval(BillingApproval billingApproval) {
        streamBridge.send("billingApproval-out-0", MessageBuilder.withPayload(billingApproval).build());
    }

    @Async
    public void sendBillingCancel(BillingCancel billingCancel) {
        streamBridge.send("billingCancel-out-0", MessageBuilder.withPayload(billingCancel).build());
    }
}
