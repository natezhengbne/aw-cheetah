package com.asyncworking.services;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.exceptions.NoCompanyWithSuchUserException;
import com.asyncworking.models.Company;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public List<CompanyInfoDto> fetchCompaniesWithGivenUser(String email) throws NoCompanyWithSuchUserException {

        UserEntity LoggedInUser = userRepository.findByEmail(email).get();
        List<Company> companies = companyRepository.findCompaniesByUserId(LoggedInUser.getId());

        if (companies.isEmpty()) {
            throw new NoCompanyWithSuchUserException("No company found with such user");
        }

        return companies.stream().map(company ->
                CompanyInfoDto.builder()
                        .name(company.getName())
                        .build()
        ).collect(Collectors.toList());

    }
}
