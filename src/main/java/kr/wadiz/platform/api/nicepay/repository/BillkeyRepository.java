package kr.wadiz.platform.api.nicepay.repository;

import kr.wadiz.platform.api.nicepay.model.entity.Billkey;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BillkeyRepository extends ReactiveCrudRepository<Billkey, Long> {
    Mono<Billkey> findByUserIdAndCardNo(Integer userId, String cardNo);
}
