package org.yaojiu.supermarket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("goods")
public class Good {
    @TableId
    private Integer id;
    @NotBlank(message = "商品名称不能为空")
    private String name;
    @NotBlank(message = "供应商名称不能为空")
    private String provider;
    @Min(value = 0, message = "商品价格不能小于0")
    private BigDecimal price;
    @Min(value = 0, message = "商品销量不能小于0")
    private Integer scale;
    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock;
    private String imgPath;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Past(message = "商品创建时间不能大于当前时间")
    private Date createDate;

}
