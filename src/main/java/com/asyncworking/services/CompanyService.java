package com.asyncworking.services;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.exceptions.NoCompanyWithSuchUserException;
import com.asyncworking.models.Company;
import com.asyncworking.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

 /*   private final CompanyRepository companyRepository;

    public List<CompanyInfoDto> fetchCompaniesWithGivenUserEmail(String email) throws NoCompanyWithSuchUserException {

        List<Company> companies = companyRepository.findCompaniesByUserEmail(email);

        if (companies.isEmpty()) {
            throw new NoCompanyWithSuchUserException("No company found with such user");
        }

        return companies.stream().map(company ->
                CompanyInfoDto.builder()
                        .name(company.getName())
                        .build()
        ).collect(Collectors.toList());

    }*/
}
