package com.alpegagroup.insurance.mappers;

import com.alpegagroup.common.util.DateUtils;
import com.alpegagroup.insurance.domain.InsuranceSource;
import com.alpegagroup.insurance.model.Insurance;
import com.alpegagroup.insurance.webmodel.request.InsuranceRequest;
import com.alpegagroup.insurance.webmodel.response.InsuranceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

@Mapper(componentModel = "spring")
public abstract class InsuranceMapper {

    @Mapping(target = "substatus", ignore = true)
    @Mapping(target = "freight.origin.country", source = "freight.originCountry")
    @Mapping(target = "freight.origin.city", source = "freight.originCity")
    @Mapping(target = "freight.destination.country", source = "freight.destinationCountry")
    @Mapping(target = "freight.destination.city", source = "freight.destinationCity")
    public abstract InsuranceResponse toResponse(Insurance insurance);

    @Mapping(source = "freight.origin.country", target = "freight.originCountry")
    @Mapping(source = "freight.origin.city", target = "freight.originCity")
    @Mapping(source = "freight.destination.country", target = "freight.destinationCountry")
    @Mapping(source = "freight.destination.city", target = "freight.destinationCity")
    @Mapping(target = "source", expression = "java(getSource(request))")
    @Mapping(target = "promissoryNoteDate", expression ="java(getPromissoryDate(request))")
    @Mapping(target = "invoiceDate", expression ="java(getInvoiceDate(request))")
    @Mapping(target = "paymentDate", expression ="java(getPaymentDate(request))")
    public abstract Insurance fromRequest(InsuranceRequest request);

    public InsuranceSource getSource(InsuranceRequest request) {

        if (request.hasSource()) {
            return request.getSource();
        }

        return InsuranceSource.getDefault();
    }

    public Date getPromissoryDate(InsuranceRequest request) {
        return getDateAtStartOfDay(request.getPromissoryNoteDate());
    }

    public Date getInvoiceDate(InsuranceRequest request) {
        return getDateAtStartOfDay(request.getInvoiceDate());
    }

    public Date getPaymentDate(InsuranceRequest request) {
        return getDateAtStartOfDay(request.getPaymentDate());
    }

    private Date getDateAtStartOfDay(Date date) {

        if (date == null) {
            return null;
        }

        return DateUtils.getStartOfDay(date);
    }

}
