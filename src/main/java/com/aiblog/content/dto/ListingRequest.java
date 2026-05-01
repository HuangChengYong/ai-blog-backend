package com.aiblog.content.dto;

import jakarta.validation.constraints.NotBlank;

public record ListingRequest(@NotBlank(message = "上架状态不能为空") String listingStatus) {
}
