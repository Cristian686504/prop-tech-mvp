package co.com.proptech.usecase.application;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GetTenantApplicationsUseCase {
    
    private final ApplicationRepository applicationRepository;
    
    /**
     * Get all applications submitted by a tenant
     */
    public List<Application> execute(UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        return applicationRepository.findByTenantId(tenantId);
    }
}
