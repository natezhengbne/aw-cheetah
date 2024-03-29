package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.CompanyInvitedAccountDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import com.asyncworking.models.ICompanyInfo;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.UserLoginInfoRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.CompanyMapper;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.TodoMapper;
import com.asyncworking.utility.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.asyncworking.models.RoleNames.COMPANY_MANAGER;
import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    private final UserLoginInfoRepository userLoginInfoRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    private final TodoItemRepository todoItemRepository;

    private final CompanyMapper companyMapper;

    private final UserMapper userMapper;

    private final EmployeeMapper employeeMapper;

    private final TodoMapper todoMapper;

    private final RoleService roleService;

    private final EmailService emailService;

    private final EmailMapper emailMapper;

    private final LinkGenerator linkGenerator;

    @Transactional
    public Long createCompanyAndEmployee(CompanyModificationDto companyModificationDto) {

        UserEntity selectedUserEntity = fetchUserEntityByEmail(companyModificationDto.getAdminEmail());
        log.info("selectedUser's email" + selectedUserEntity.getEmail());
        Company newCompany = createCompany(companyModificationDto.getName(), selectedUserEntity.getId());

        companyRepository.save(newCompany);

        roleService.assignRole(selectedUserEntity, COMPANY_MANAGER, newCompany.getId());

        Employee newEmployee = createEmployee
                (new EmployeeId(selectedUserEntity.getId(), newCompany.getId()),
                        selectedUserEntity,
                        newCompany);
        if (companyModificationDto.getUserTitle() != null) {
            newEmployee.setTitle(companyModificationDto.getUserTitle());
            employeeRepository.save(newEmployee);
        }
        return newCompany.getId();
    }

    public CompanyColleagueDto getCompanyInfoDto(String email) {
        List<ICompanyInfo> companiesInfo = companyRepository.findCompanyInfoByEmail(email);
        if (companiesInfo == null || companiesInfo.isEmpty()) {
            throw new CompanyNotFoundException("Can not find companiesInfo by email:" + email);
        }
        log.info("find first company with company ID: {} by user's email: {}", companiesInfo.get(0).getId(), email);
        List<String> colleague = companyRepository.findNameById(companiesInfo.get(0).getId());
        return mapCompanyToCompanyDto(companiesInfo.get(0), colleague);
    }

    public List<ICompanyInfo> getUserCompanyListByEmail(String email){
        return companyRepository.findCompanyInfoByEmail(email);
    }

    private CompanyColleagueDto mapCompanyToCompanyDto(ICompanyInfo companyInfo, List<String> colleague) {
        return CompanyColleagueDto.builder()
                .companyId(companyInfo.getId())
                .name(companyInfo.getName())
                .description(companyInfo.getDescription())
                .colleague(colleague)
                .build();
    }

    private UserEntity fetchUserEntityByEmail(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Can not find user by email:" + email));
    }

    private Company createCompany(String company, Long userId) {
        return Company.builder()
                .name(company)
                .adminId(userId)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .employees(new HashSet<>())
                .build();
    }

    private Employee createEmployee(EmployeeId employeeId, UserEntity userEntity, Company company) {
        return Employee.builder()
                .id(employeeId)
                .company(company)
                .userEntity(userEntity)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public CompanyModificationDto fetchCompanyProfileById(Long companyId) {
        Company company = fetchCompanyById(companyId);
        return userMapper.mapEntityToCompanyProfileDto(company);
    }

    public Company fetchCompanyById(Long companyId) {
        return companyRepository
                .findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Can not find company with Id:" + companyId));
    }

    @Transactional
    public void updateCompany(CompanyModificationDto companyModificationDto) {
        Company company = userMapper.mapInfoDtoToEntity(companyModificationDto);

        int res = companyRepository.updateCompanyProfileById(company.getId(),
                company.getName(),
                company.getDescription(),
                OffsetDateTime.now(UTC));

        if (res == 0) {
            throw new CompanyNotFoundException("Can not find company with Id:" + company.getId());
        }
    }

    public CompanyInfoDto findCompanyById(Long id) {
        log.info("find company by company ID: {}", id);
        Company foundCompany = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Can not find company by id:" + id));
        return companyMapper.mapEntityToDto(foundCompany);
    }

    public List<EmployeeGetDto> findAllEmployeeByCompanyId(Long companyId) {
        log.info("search all employees by company ID: {}", companyId);
        return userRepository.findAllEmployeeByCompanyId(companyId).stream()
                .map(employeeMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public List<AvailableEmployeesGetDto> findAvailableEmployees(Long companyId, Long projectId) {
        log.info("search all available employee by company ID: {}, and projectId ID: {}", companyId, projectId);
        //how to verify the invitations/registerProjectId and companyId if they are both invalid the result is always empty arrays
        return userRepository.findAvailableEmployeesByCompanyAndProjectId(companyId, projectId).stream()
                .map(employeeMapper::mapAvailableEmployeesEntityToDto)
                .collect(Collectors.toList());
    }

    public List<List<CardTodoItemDto>> findTodoItemCardList(Long companyId, Long userId) {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        List<TodoItem> todoItems = todoItemRepository.findByCompanyIdAndDueDate(companyId, today.plusDays(7));

        List<CardTodoItemDto> todoItemDtos = todoItems.stream()
                .filter(item -> Arrays.asList(item.getSubscribersIds().trim().split(",")).contains(userId.toString()))
                .map(todoMapper::toCardTodoItemDto).collect(Collectors.toList());

        List<CardTodoItemDto> upcomingItems = todoItemDtos.stream()
                .filter(item -> (item.getDueDate().isAfter(today.plusDays(2)) && item.getDueDate().isBefore(today.plusDays(7))))
                .collect(Collectors.toList());
        List<CardTodoItemDto> expiringItems = todoItemDtos.stream()
                .filter(item -> (item.getDueDate().isAfter(today.minusDays(1)) && item.getDueDate().isBefore(today.plusDays(2))))
                .collect(Collectors.toList());
        List<CardTodoItemDto> overdueItems = todoItemDtos.stream()
                .filter(item -> (item.getDueDate().isBefore(today.minusDays(1))))
                .collect(Collectors.toList());

        List<List<CardTodoItemDto>> cardList = Arrays.asList(upcomingItems, expiringItems, overdueItems);
        return cardList.stream().map(list -> list.stream().sorted(Comparator
                .comparing(CardTodoItemDto::getDueDate)
                .thenComparing(CardTodoItemDto::getPriority, CardTodoItemDto::comparePriority)
                .thenComparing(CardTodoItemDto::getProjectTitle)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public void sendInvitationLink(Long companyId, CompanyInvitedAccountDto accountDto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Cannot find company by id: " + companyId));

        UserEntity owner = userRepository.findById(company.getAdminId())
                .orElseThrow(() -> new UserNotFoundException("Cannot find admin for company"));

        UserEntity user = userRepository.findByEmail(accountDto.getEmail()).orElse(null);

        String invitationLink = generateCompanyInvitationLink(companyId, accountDto);

        emailService.sendLinkByEmail(emailMapper.toEmailContentDto(
                EmailType.CompanyInvitation.toString(),
                invitationLink,
                accountDto,
                company.getName(),
                owner.getName()
        ), user != null ? user.getId() : null);
    }

    public String generateInvitationLink(Long companyId, String email, String name, String title) {
        String invitationLink = linkGenerator.generateInvitationLink(
                companyId,
                email,
                name,
                title
        );
        return invitationLink;
    }

    public String generateCompanyInvitationLink(Long companyId, CompanyInvitedAccountDto accountDto) {
        String invitationLink = linkGenerator.generateCompanyInvitationLink(
                companyId,
                accountDto.getEmail(),
                accountDto.getName(),
                accountDto.getTitle(),
                DateTimeUtility.MILLISECONDS_IN_DAY
        );
        return invitationLink;
    }

   @Transactional(rollbackFor = CompanyNotFoundException.class)
    public void updateUserLoginCompanyId(String email, Long companyId, Long userId) {
        List<Long> userCompanyIdList = userRepository.findUserCompanyIdList(email);
        OffsetDateTime loginTime = OffsetDateTime.now(UTC);
        if (userCompanyIdList.contains(companyId)) {
            userLoginInfoRepository.setUserLoginCompanyId(companyId, userId, loginTime);
        } else {
            throw new CompanyNotFoundException("This user does not belong to this company");
        }
    }
}
