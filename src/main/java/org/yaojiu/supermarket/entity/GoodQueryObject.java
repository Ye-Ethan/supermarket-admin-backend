package org.yaojiu.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodQueryObject {
    private Integer id;
    private String name;
    private String provider;
    @Min(value = 0, message = "商品价格最小值不能小于0")
    private BigDecimal priceMin;
    @Min(value = 0, message = "商品价格最大值不能小于0")
    private BigDecimal priceMax;
    @Min(value = 0, message = "商品销量最小值不能小于0")
    private Integer scaleMin;
    @Min(value = 0, message = "商品销量最大值不能小于0")
    private Integer scaleMax;
    @Min(value = 0, message = "商品库存最小值不能小于0")
    private Integer stockMin;
    @Min(value = 0, message = "商品库存最大值不能小于0")
    private Integer stockMax;
    private String imgPath;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past(message = "商品创建时间起始时间不能大于当前时间")
    private Date createDateMin;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past(message = "商品创建时间截止时间不能大于当前时间")
    private Date createDateMax;
}
