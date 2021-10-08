package com.drapo.beer.inventory.service.services;

import com.drapo.beer.inventory.service.domain.BeerInventory;
import com.drapo.beer.inventory.service.repositories.BeerInventoryRepository;
import com.drapo.brewery.model.BeerOrderDto;
import com.drapo.brewery.model.BeerOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {
    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
        log.debug("Allocating Order Id: "+beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLine ->{
            if((((beerOrderLine.getOrderQuantity()!= null ? beerOrderLine.getOrderQuantity():0)
            - (beerOrderLine.getQuantityAllocated()!=null?beerOrderLine.getQuantityAllocated():0)) >0)){
                allocateBeerOrderLine(beerOrderLine);
            }

            totalOrdered.set(totalOrdered.get()+beerOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get()+(beerOrderLine.getQuantityAllocated()!=null?beerOrderLine.getQuantityAllocated():0));
        });

        log.debug(String.format("Total ordered: %d. Total allocated: %d", totalOrdered.get(), totalAllocated.get()));

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLine) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLine.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = (beerInventory.getQuantityOnHand() == null) ? 0 : beerInventory.getQuantityOnHand();
            int orderQty = (beerOrderLine.getOrderQuantity() == null) ? 0 : beerOrderLine.getOrderQuantity();
            int allocatedQty = (beerOrderLine.getQuantityAllocated() == null) ? 0 : beerOrderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                beerOrderLine.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);

                beerInventoryRepository.save(beerInventory);
            } else if (inventory > 0) { //partial allocation
                beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);

            }

            if (beerInventory.getQuantityOnHand() == 0) {
                beerInventoryRepository.delete(beerInventory);
            }
        });
    }
}
