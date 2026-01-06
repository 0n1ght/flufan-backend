package com.flufan.common.mapper;

import com.flufan.modules.user.dto.ServiceDto;
import com.flufan.modules.user.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public Service toService(ServiceDto serviceDto) {
        Service service = new Service();

        service.setTitle(service.getTitle());
        service.setDescription(serviceDto.getDescription());
        service.setPrice(serviceDto.getPrice());
        service.setOptionalQuestions(serviceDto.getOptionalQuestions());

        return service;
    }
}
