package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.model.ProductDto;
import com.zerobase.cms.order.domain.model.ProductItemDto;
import com.zerobase.cms.order.service.ProductSearchService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.cms.order.domain.model.ProductItemDto.getItemInfo;

@RestController
@RequestMapping("/search/product")
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> searchByName(
            @RequestParam(name = "name") String name
    ){
        return ResponseEntity.ok(
                productSearchService.searchByName(name)
                        .stream()
                        .map(ProductDto::WithoutItemsfrom)
                        .collect(Collectors.toList())
        );
    }

/*    @GetMapping("/detail")
    public ResponseEntity<ProductDto> getDetail(
            @RequestParam(name = "productId") Long productId
    ) {
        return ResponseEntity.ok(
                ProductDto.from(productSearchService.getByProductId(productId))
        );
    }*/


    @GetMapping("/detail")
    public ResponseEntity<List<ProductItemDto>> getDetail(
            @RequestParam(name = "productId") Long productId
    ){

        return  ResponseEntity.ok(productSearchService.searchById(productId)
                        .stream()
                        .map(ProductItemDto::getItemInfo)
                        .collect(Collectors.toList())
        );
    }
}
