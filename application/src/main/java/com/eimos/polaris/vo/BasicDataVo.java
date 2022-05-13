package com.eimos.polaris.vo;

import com.eimos.polaris.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BasicDataVo {
    private String code;
    private String name;
    private LocalDate effectiveDate;
    private LocalDate expiredDate;

    public BasicDataVo(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public LocalDate getEffectiveDate() {
        return Objects.requireNonNullElse(this.effectiveDate, Constants.MIN_DATE);
    }

    public LocalDate getExpiredDate() {
        return Objects.requireNonNullElse(this.effectiveDate, Constants.MAX_DATE);
    }
}
