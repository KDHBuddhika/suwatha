package com.spring.Suwatha.analytics_module.dto.reportDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;


@Data
@AllArgsConstructor
public class PaginationDto {
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int itemsPerPage;
    
    public static PaginationDto fromPage(Page<?> page) {
        return new PaginationDto(
                page.getNumber() + 1, // Page numbers are 0-indexed, but UI is 1-indexed
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}
