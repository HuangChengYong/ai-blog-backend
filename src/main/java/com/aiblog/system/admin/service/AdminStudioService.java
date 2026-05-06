package com.aiblog.system.admin.service;

import java.util.List;

public interface AdminStudioService {

    AdminStudioOptionsResponse studioOptions();

    record AdminStudioOptionsResponse(List<String> styles, List<String> lengths, List<String> tags) {}
}