package kr.wadiz.platform.api.nicepay.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table
public class Billkey {
    @Id
    private Long id;

    private String bid;

    private Integer userId;

    private String cardNo;

    private String expYear;

    private String expMonth;

    private String authDate;

    private String cardCode;

    private String cardName;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
