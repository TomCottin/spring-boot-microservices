package com.cottin.inventoryservice.service;

import com.cottin.inventoryservice.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {

    List<InventoryResponseDto> isInStock(List<String> skuCode);

}
