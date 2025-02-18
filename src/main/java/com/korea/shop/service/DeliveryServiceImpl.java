package com.korea.shop.service;

import com.korea.shop.domain.Delivery;
import com.korea.shop.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    @Override
    public void saveDelivery(Delivery delivery) {
        deliveryRepository.save(delivery);
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Override
    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
    }

    @Transactional
    @Override
    public void updateDelivery(Long id, Delivery delivery) {
        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        existingDelivery.setStatus(delivery.getStatus());
        existingDelivery.setAddress(delivery.getAddress());

        deliveryRepository.save(existingDelivery);
    }

    @Transactional
    @Override
    public void deleteDelivery(Long id) {
        if (deliveryRepository.existsById(id)) {
            deliveryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Delivery not found");
        }
    }
}
