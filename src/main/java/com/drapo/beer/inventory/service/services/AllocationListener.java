package com.drapo.beer.inventory.service.services;

import com.drapo.beer.inventory.service.config.JmsConfig;
import com.drapo.brewery.model.events.AllocateOrderRequestEvent;
import com.drapo.brewery.model.events.AllocateOrderResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void allocate(AllocateOrderRequestEvent allocationEvent) {
        AllocateOrderResultEvent.AllocateOrderResultEventBuilder builder = AllocateOrderResultEvent.builder();
        builder.beerOrderDto(allocationEvent.getBeerOrderDto());

        try {
            Boolean allocationResult = allocationService.allocateOrder(allocationEvent.getBeerOrderDto());

            if (allocationResult) {
                builder.pendingInventory(false);
            } else {
                builder.pendingInventory(true);
            }
        } catch (Exception e) {
            log.error("Allocation failed for order id: "+allocationEvent.getBeerOrderDto().getId());
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, builder.build());
    }
}
