package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.*;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.*;
import com.asyncworking.utility.mapper.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private TodoItemRepository todoItemRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private EmailSendRepository emailSendRepository;

    private EmployeeMapper employeeMapper;
    private CompanyMapper companyMapper;
    private UserMapper userMapper;
    private TodoMapper todoMapper;
    private CompanyService companyService;


    @BeforeEach()
    public void setup() {
        employeeMapper = new EmployeeMapper();
        companyMapper = new CompanyMapper();
        todoMapper = new TodoMapperImpl();
        userMapper = new UserMapper(passwordEncoder);
        companyService = new CompanyService(
                userRepository,
                companyRepository,
                employeeRepository,
                todoItemRepository,
                emailSendRepository,
                companyMapper,
                userMapper,
                employeeMapper,
                todoMapper,
                roleService,
                emailService,
                userService
        );
    }

    @Test
    public void createCompanyAndEmployeeGivenProperUserInfoDto() {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("lengary@asyncworking.com")
                .name("ven").build();

        when(userRepository.findUserEntityByEmail(companyModificationDto.getAdminEmail()))
                .thenReturn(Optional.of(mockReturnedUserEntity));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        companyService.createCompanyAndEmployee(companyModificationDto);

        verify(companyRepository).save(companyCaptor.capture());
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();
        Company savedCompany = companyCaptor.getValue();

        assertEquals("VI", savedEmployee.getTitle());
        assertEquals(mockReturnedUserEntity.getId(), savedCompany.getAdminId());
    }

    @Test
    public void throwNotFoundExceptionWhenUserNotExit() {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("lengary@asyncworking.com")
                .name("AW")
                .userTitle("VI")
                .build();

        when(userRepository.findUserEntityByEmail(companyModificationDto.getAdminEmail()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> companyService.createCompanyAndEmployee(companyModificationDto));

        String expectedMessage = "Can not find user by email";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getCompanyInfoWhenGivenUserEmail() {
        String email = "p@asyncworking.com";
        ICompanyInfoImpl mockCompanyInfo = ICompanyInfoImpl.builder()
                .companyId(1L)
                .name("p")
                .description("the description for + HQ")
                .build();

        List<ICompanyInfo> returnedCompanyInfo = List.of(mockCompanyInfo);

        when(companyRepository.findCompanyInfoByEmail(email)).thenReturn(returnedCompanyInfo);

        CompanyColleagueDto companyInfo = companyService.getCompanyInfoDto(email);
        assertEquals("p", companyInfo.getName());
        assertEquals(mockCompanyInfo.getDescription(), companyInfo.getDescription());
    }

    @Test
    void fetchCompanyProfileById() {
        Company mockReturnedCompany = Company.builder()
                .id(1L)
                .name("AW")
                .description("desc")
                .build();
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .name("AW")
                .description("desc")
                .build();

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(mockReturnedCompany));

        String expectedDescription = "desc";

        String actualDescription = companyService
                .fetchCompanyProfileById(1L)
                .getDescription();

        assertEquals(expectedDescription, actualDescription);
    }


    @Test
    void fetchCompanyTodoItemsByCompanyId() {
        OffsetDateTime nowTime = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        CardTodoItemDto mockTodoItemDto1 = CardTodoItemDto.builder()
                .todoItemId(1L)
                .description("desc")
                .projectTitle("title")
                .priority("High")
                .dueDate(nowTime.minusDays(3))
                .build();

        CardTodoItemDto mockTodoItemDto3 = CardTodoItemDto.builder()
                .todoItemId(1L)
                .description("desc2")
                .projectTitle("title2")
                .priority("Low")
                .dueDate(nowTime.plusDays(5))
                .build();

        List<CardTodoItemDto> overDueItem = List.of(mockTodoItemDto1);
        List<CardTodoItemDto> expiringItem = new ArrayList<>(0);
        List<CardTodoItemDto> upComingItem = List.of(mockTodoItemDto3);
        List<List<CardTodoItemDto>> allTodoCardItemsList = List.of(upComingItem, expiringItem, overDueItem);

        Project mockProject1 = Project.builder().name("title").build();
        Project mockProject2 = Project.builder().name("title1").build();
        Project mockProject3 = Project.builder().name("title2").build();
        TodoList mockTodoList1 = TodoList.builder().project(mockProject1).build();
        TodoList mockTodoList2 = TodoList.builder().project(mockProject2).build();
        TodoList mockTodoList3 = TodoList.builder().project(mockProject3).build();

        TodoItem mockTodoItem1 = new TodoItem().builder()
                .id(1L)
                .description("desc")
                .todoList(mockTodoList1)
                .priority("High")
                .subscribersIds("1,2,3,4")
                .dueDate(nowTime.minusDays(3)).build();

        TodoItem mockTodoItem2 = new TodoItem().builder()
                .id(1L)
                .description("desc1")
                .todoList(mockTodoList2)
                .priority("Medium")
                .subscribersIds("1,3,6,8")
                .dueDate(nowTime.plusDays(2)).build();

        TodoItem mockTodoItem3 = new TodoItem().builder()
                .id(1L)
                .description("desc2")
                .todoList(mockTodoList3)
                .priority("Low")
                .subscribersIds("1,3,4,9,10")
                .dueDate(nowTime.plusDays(5)).build();
        List<TodoItem> mockTodoItemList = List.of(mockTodoItem1, mockTodoItem2, mockTodoItem3);

        when(todoItemRepository.findByCompanyIdAndDueDate(1L, nowTime.plusDays(7)))
                .thenReturn(mockTodoItemList);
        List<List<CardTodoItemDto>> todoItemCardList = companyService.findTodoItemCardList(1L, 1L);

        assertEquals(allTodoCardItemsList, todoItemCardList);
    }

    @Test
    void throwNotFoundExceptionWhenIdNotExist() {

        when(companyRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(CompanyNotFoundException.class,
                () -> companyService.fetchCompanyProfileById(2L));

        String expectedMessage = "Can not find company with Id:2";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnAvailableEmployeesByCompanyIdAndProjectId() {
        AvailableEmployeesGetDto mockEmployeeGetDto = AvailableEmployeesGetDto.builder()
                .id(1L)
                .name("name1")
                .email("1@gmail.com")
                .title("dev")
                .build();
        IAvailableEmployeeInfo mockIEmployeeInfo = IAvailableEmployeeInfoImpl.builder()
                .id(1L)
                .name("name1")
                .title("dev")
                .email("1@gmail.com")
                .build();
        when(userRepository.findAvailableEmployeesByCompanyAndProjectId(1L, 1L))
                .thenReturn(List.of(mockIEmployeeInfo));
        List<AvailableEmployeesGetDto> result = companyService.findAvailableEmployees(1L, 1L);
        assertEquals(result.get(0).getName(), mockIEmployeeInfo.getName());
    }

    @Test
    public void shouldSaveEmailRecordAndSendSQSMessage() throws JsonProcessingException {
        String receiverEmail = "test@gmail.com";
        String receiverName = "Alice S";
        String receiverTitle = "CEO";
        Long companyId = 1L;
        String companyName = "CompanyA";
        String companyOwnerName = "OwnerB";
        String invitationLink = "link";
        CompanyInvitedAccountDto accountDto = CompanyInvitedAccountDto.builder()
                .title(receiverTitle)
                .name(receiverName)
                .email(receiverEmail)
                .build();
        UserEntity receiver = UserEntity.builder()
                .name(receiverName)
                .email(receiverEmail)
                .build();
        ICompanyInvitationEmailCompanyInfo companyInfo = ICompanyInvitationEmailCompanyInfoImpl.builder()
                .companyId(companyId)
                .companyName(companyName)
                .companyOwnerName(companyOwnerName)
                .build();
        EmailSendRecord emailSendRecord = EmailSendRecord.builder()
                .companyId(companyId)
                .userEntity(receiver)
                .receiver(receiverEmail)
                .emailType(EmailType.CompanyInvitation)
                .sendStatus(false)
                .build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(receiver));
        when(emailSendRepository.findCompanyInfo(any())).thenReturn(Optional.ofNullable(companyInfo));
        when(emailService.saveCompanyInvitationEmailSendingRecord(any(), any(), any(), any())).thenReturn(emailSendRecord);
        when(userService.generateCompanyInvitationLink(any(), any(), any(), any())).thenReturn(invitationLink);
        companyService.sendCompanyInvitationToSQS(companyId, accountDto);

        ArgumentCaptor<Long> emailRecordIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> receiverNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> receiverEmailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> companyNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> companyOwnerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EmailType> emailTypeCaptor = ArgumentCaptor.forClass(EmailType.class);
        verify(emailService).sendCompanyInvitationMessageToSQS(
                emailRecordIdCaptor.capture(),
                receiverNameCaptor.capture(),
                receiverEmailCaptor.capture(),
                companyNameCaptor.capture(),
                companyOwnerNameCaptor.capture(),
                linkCaptor.capture(),
                emailTypeCaptor.capture());

        assertEquals("Alice", receiverNameCaptor.getValue());
        assertEquals(receiverEmail, receiverEmailCaptor.getValue());
        assertEquals(companyName, companyNameCaptor.getValue());
        assertEquals(companyOwnerName, companyOwnerNameCaptor.getValue());
        assertEquals(invitationLink, linkCaptor.getValue());
        assertEquals(EmailType.CompanyInvitation, emailTypeCaptor.getValue());
    }

}
