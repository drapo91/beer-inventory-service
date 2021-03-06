package com.drapo.brewery.model.events;

import com.drapo.brewery.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BeerEvent {

    private BeerDto beerDto;
}
