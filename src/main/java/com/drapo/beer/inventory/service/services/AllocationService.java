package com.drapo.beer.inventory.service.services;

import com.drapo.brewery.model.BeerOrderDto;

public interface AllocationService {

    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
