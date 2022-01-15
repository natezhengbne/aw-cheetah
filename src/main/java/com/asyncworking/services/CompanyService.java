package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.*;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.*;
import com.asyncworking.utility.mapper.CompanyMapper;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.TodoMapper;
import com.asyncworking.utility.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.asyncworking.models.RoleNames.COMPANY_MANAGER;
import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final EmployeeRepository employeeRepository;

    private final TodoItemRepository todoItemRepository;

    private final EmailSendRepository emailSendRepository;

    private final CompanyMapper companyMapper;

    private final UserMapper userMapper;

    private final EmployeeMapper employeeMapper;

    private final TodoMapper todoMapper;

    private final RoleService roleService;

    private final EmailService emailService;

    private final UserService userService;

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

    public Map<DayOfWeek, Integer> findOneWeekCompletedTodoItemsCounts(Long companyId, Long userId) {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime startDateOfWeek = today.minusDays
                (today.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : today.getDayOfWeek().getValue());

        OffsetDateTime start = startDateOfWeek.withHour(0).withMinute(0).withSecond(0);
        Map<DayOfWeek, Integer> oneWeekCompletedTodoItemsCounts = new LinkedHashMap<>();

        for (int i = 0; i < DayOfWeek.values().length; i++) {
            OffsetDateTime end = start.withHour(23).withMinute(59).withSecond(59);
            int completedTodoItemsCount = todoItemRepository
                    .countByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(companyId, userId.toString(), start, end);
            oneWeekCompletedTodoItemsCounts.put(start.getDayOfWeek(), completedTodoItemsCount);
            start = start.plusDays(1);
        }
        return oneWeekCompletedTodoItemsCounts;
    }

    public Map<DayOfWeek, List<ContributionActivitiesDto>> findOneWeekCompletedTodoItemsList(Long companyId, Long userId) {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime startDateOfWeek = today.minusDays
                (today.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : today.getDayOfWeek().getValue());

        OffsetDateTime start = startDateOfWeek.withHour(0).withMinute(0).withSecond(0);
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = new LinkedHashMap<>();
        for (int i = 0; i < DayOfWeek.values().length; i++) {
            OffsetDateTime end = start.withHour(23).withMinute(59).withSecond(59);
            List<TodoItem> completedTodoItems = todoItemRepository
                    .findByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(companyId, userId.toString(), start, end);
            List<ContributionActivitiesDto> contributionActivitiesDtos = completedTodoItems.stream().map(completedTodoItem -> TodoMapper.mapContributionActivitiesDto(completedTodoItem)).collect(Collectors.toList());
            oneWeekCompletedTodoItemsList.put(start.getDayOfWeek(), contributionActivitiesDtos);
            start = start.plusDays(1);
        }
        return oneWeekCompletedTodoItemsList;

    }

    public void sendCompanyInvitationToSQS(Long companyId, CompanyInvitedAccountDto invitedAccountDto) throws JsonProcessingException {

        UserEntity receiver = userRepository.findByEmail(invitedAccountDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with email" + invitedAccountDto.getEmail()));
        ICompanyInvitationEmailCompanyInfo companyInfo = emailSendRepository.findCompanyInfo(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Cannot find company with id: " + companyId));
        log.info("Company Invitation Receiver Name: {}, Receiver Email: {}, Company Name: {}, Company Owner's Name: {}",
                receiver.getName(), receiver.getEmail(), companyInfo.getCompanyName(), companyInfo.getCompanyOwnerName());
        EmailSendRecord emailSendRecord = emailService.saveCompanyInvitationEmailSendingRecord(
                receiver, EmailType.CompanyInvitation, invitedAccountDto.getEmail(), companyId);

        Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        String invitationLink = userService.generateCompanyInvitationLink(
                companyId, invitedAccountDto.getEmail(), invitedAccountDto.getName(), expireDate);
        if (invitedAccountDto.getName().contains(" ")) {
            invitedAccountDto.setName(invitedAccountDto.getName().substring(0, invitedAccountDto.getName().indexOf(" ")));
        }
        emailService.sendCompanyInvitationMessageToSQS(
                emailSendRecord.getId(),
                invitedAccountDto.getName(),
                invitedAccountDto.getEmail(),
                companyInfo.getCompanyName(),
                companyInfo.getCompanyOwnerName(),
                invitationLink,
                EmailType.CompanyInvitation
        );
    }
}
