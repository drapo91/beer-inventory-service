package com.drapo.brewery.model.events;

import com.drapo.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderRequestEvent {
    private BeerOrderDto beerOrderDto;
}
