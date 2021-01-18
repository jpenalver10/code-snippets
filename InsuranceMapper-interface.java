package com.alpegagroup.insurance.mappers;

import com.alpegagroup.common.util.DateUtils;
import com.alpegagroup.insurance.domain.InsuranceSource;
import com.alpegagroup.insurance.model.Insurance;
import com.alpegagroup.insurance.webmodel.request.InsuranceRequest;
import com.alpegagroup.insurance.webmodel.response.InsuranceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface InsuranceMapper {

    @Mapping(target = "substatus", ignore = true)
    @Mapping(target = "freight.origin.country", source = "freight.originCountry")
    @Mapping(target = "freight.origin.city", source = "freight.originCity")
    @Mapping(target = "freight.destination.country", source = "freight.destinationCountry")
    @Mapping(target = "freight.destination.city", source = "freight.destinationCity")
    InsuranceResponse toResponse(Insurance insurance);

    @Mapping(source = "freight.origin.country", target = "freight.originCountry")
    @Mapping(source = "freight.origin.city", target = "freight.originCity")
    @Mapping(source = "freight.destination.country", target = "freight.destinationCountry")
    @Mapping(source = "freight.destination.city", target = "freight.destinationCity")
    @Mapping(target = "source", expression = "java(getSource(request))")
    @Mapping(target = "promissoryNoteDate", expression ="java(DateUtils.getStartOfDay(request.getPromissoryNoteDate()))")
    @Mapping(target = "invoiceDate", expression ="java(DateUtils.getStartOfDay(request.getInvoiceDate()))")
    @Mapping(target = "paymentDate", expression ="java(DateUtils.getStartOfDay(request.getPaymentDate()))")
    Insurance fromRequest(InsuranceRequest request);

    default InsuranceSource getSource(InsuranceRequest request) {

        if (request.hasSource()) {
            return request.getSource();
        }

        return InsuranceSource.getDefault();
    }

}
