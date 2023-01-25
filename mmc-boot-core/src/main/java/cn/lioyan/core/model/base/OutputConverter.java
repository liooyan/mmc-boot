package cn.lioyan.core.model.base;


import cn.lioyan.core.util.BeanUtils;
import org.springframework.lang.NonNull;

public interface OutputConverter<DtoT extends OutputConverter<DtoT, D>, D> {

     @NonNull
     default DtoT convertFrom(@NonNull D domain) {
          BeanUtils.updateProperties(domain, this);
          return (DtoT) this;
     }
}
