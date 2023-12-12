package com.example.demo.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CopyPropertiesImp implements CopyProperties {

    @Override
    public void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }
}
