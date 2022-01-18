package com.abri.tech.orderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Setter
@Getter
@ApiModel(description = "The restaurant order")
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrder {

    @NonNull
    @ApiModelProperty(notes = "Order Id")
    private String orderId;
    @NonNull
    @ApiModelProperty(notes = "Customer  Name")
    private String customerName;
    @NonNull
    @ApiModelProperty(notes = "The food item that customer has ordered")
    private String menuName;

}
