package com.korea.shop.service;

import com.korea.shop.domain.Delivery;
import java.util.List;

public interface DeliveryService {
    void saveDelivery(Delivery delivery);
    List<Delivery> getAllDeliveries();
    Delivery getDelivery(Long id);
    void updateDelivery(Long id, Delivery delivery);
    void deleteDelivery(Long id);
}
