package co.com.proptech.config;

import co.com.proptech.model.application.gateways.ApplicationRepository;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.gateways.JwtService;
import co.com.proptech.model.user.gateways.PasswordEncoder;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.application.ApplyForPropertyUseCase;
import co.com.proptech.usecase.application.CalculateSecurityDepositUseCase;
import co.com.proptech.usecase.application.EvaluateFinancialRiskUseCase;
import co.com.proptech.usecase.application.GetPropertyApplicationsUseCase;
import co.com.proptech.usecase.application.GetTenantApplicationsUseCase;
import co.com.proptech.usecase.property.GetPropertiesUseCase;
import co.com.proptech.usecase.property.GetPropertyByIdUseCase;
import co.com.proptech.usecase.property.PublishPropertyUseCase;
import co.com.proptech.usecase.user.GetUserByIdUseCase;
import co.com.proptech.usecase.user.LoginUserUseCase;
import co.com.proptech.usecase.user.RegisterUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new LoginUserUseCase(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserRepository userRepository) {
        return new GetUserByIdUseCase(userRepository);
    }

    @Bean
    public PublishPropertyUseCase publishPropertyUseCase(
            PropertyRepository propertyRepository,
            UserRepository userRepository) {
        return new PublishPropertyUseCase(propertyRepository, userRepository);
    }

    @Bean
    public GetPropertiesUseCase getPropertiesUseCase(PropertyRepository propertyRepository) {
        return new GetPropertiesUseCase(propertyRepository);
    }

    @Bean
    public GetPropertyByIdUseCase getPropertyByIdUseCase(PropertyRepository propertyRepository) {
        return new GetPropertyByIdUseCase(propertyRepository);
    }

    @Bean
    public ApplyForPropertyUseCase applyForPropertyUseCase(
            ApplicationRepository applicationRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository) {
        return new ApplyForPropertyUseCase(applicationRepository, propertyRepository, userRepository);
    }

    @Bean
    public GetTenantApplicationsUseCase getTenantApplicationsUseCase(
            ApplicationRepository applicationRepository) {
        return new GetTenantApplicationsUseCase(applicationRepository);
    }

    @Bean
    public EvaluateFinancialRiskUseCase evaluateFinancialRiskUseCase() {
        return new EvaluateFinancialRiskUseCase();
    }

    @Bean
    public CalculateSecurityDepositUseCase calculateSecurityDepositUseCase() {
        return new CalculateSecurityDepositUseCase();
    }

    @Bean
    public GetPropertyApplicationsUseCase getPropertyApplicationsUseCase(
            ApplicationRepository applicationRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            EvaluateFinancialRiskUseCase evaluateFinancialRiskUseCase,
            CalculateSecurityDepositUseCase calculateSecurityDepositUseCase) {
        return new GetPropertyApplicationsUseCase(
                applicationRepository, 
                propertyRepository, 
                userRepository, 
                evaluateFinancialRiskUseCase,
                calculateSecurityDepositUseCase);
    }
}
