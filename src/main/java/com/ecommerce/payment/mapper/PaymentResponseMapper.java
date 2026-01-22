package com.ecommerce.payment.mapper;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.dto.PaymentInitResponseDto;
import com.ecommerce.payment.provider.PaymentProviderResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentResponseMapper {
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "paymentUrl", source = "paymentUrl")
    PaymentInitResponseDto toDto(Payment payment);
}
