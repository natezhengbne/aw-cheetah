package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.CompanyInvitedAccountDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.dtos.EmailContentDto;
import com.asyncworking.dtos.todoitem.CardTodoItemDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.IAvailableEmployeeInfo;
import com.asyncworking.models.IAvailableEmployeeInfoImpl;
import com.asyncworking.models.ICompanyInfo;
import com.asyncworking.models.ICompanyInfoImpl;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.UserLoginInfoRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.CompanyMapper;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.TodoMapper;
import com.asyncworking.utility.mapper.TodoMapperImpl;
import com.asyncworking.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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
    private UserLoginInfoRepository userLoginInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private EmailSendRepository emailSendRepository;

    @Mock
    private LinkGenerator linkGenerator;

    private EmployeeMapper employeeMapper;
    private CompanyMapper companyMapper;
    private UserMapper userMapper;
    private TodoMapper todoMapper;
    private final EmailMapper emailMapper = new EmailMapperImpl();
    private CompanyService companyService;


    @BeforeEach()
    public void setup() {
        employeeMapper = new EmployeeMapper();
        companyMapper = new CompanyMapper();
        todoMapper = new TodoMapperImpl();
        userMapper = new UserMapper(passwordEncoder);
        companyService = new CompanyService(
                userRepository,
                userLoginInfoRepository,
                companyRepository,
                employeeRepository,
                todoItemRepository,
                companyMapper,
                userMapper,
                employeeMapper,
                todoMapper,
                roleService,
                emailService,
                emailMapper,
                linkGenerator
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
    public void shouldReturnErrorWhenUserDoesNotBelongToCompany() {
        Long companyId = 999L;
        String email = "123@gmail.com";
        List<Long> allCompanyIdsOfUser = userRepository.findUserCompanyIdList(email);
        assertEquals(allCompanyIdsOfUser.contains(companyId), false);
    }

    @Test
    public void test_sendInvitationLink_ok() {
        long companyId = 1L;
        long companyAdminId = 1L;
        String companyName = "AW";
        String link = "http://localhost:3000/test";
        CompanyInvitedAccountDto accountDto = CompanyInvitedAccountDto.builder()
                .email("test@gmail.com")
                .name("test")
                .title("title")
                .build();
        Company company = Company.builder()
                .adminId(companyAdminId)
                .name(companyName)
                .build();
        UserEntity owner = UserEntity.builder()
                .name("Joe Doe")
                .build();
        EmailContentDto emailContentDto = emailMapper.toEmailContentDto(
                EmailType.CompanyInvitation.toString(),
                link,
                accountDto,
                company.getName(),
                owner.getName()
        );
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.findById(companyAdminId)).thenReturn(Optional.of(owner));
        doNothing().when(emailService).sendLinkByEmail(emailContentDto, null);
        when(linkGenerator.generateCompanyInvitationLink(
                companyId,
                "test@gmail.com",
                "test",
                "title",
                DateTimeUtility.MILLISECONDS_IN_DAY
        )).thenReturn(link);

        companyService.sendInvitationLink(companyId, accountDto);

        verify(companyRepository, times(1)).findById(companyId);
        verify(userRepository, times(1)).findById(companyAdminId);
        verify(emailService, times(1)).sendLinkByEmail(emailContentDto, null);
        verify(linkGenerator, times(1)).generateCompanyInvitationLink(
                companyId,
                "test@gmail.com",
                "test",
                "title",
                DateTimeUtility.MILLISECONDS_IN_DAY
        );
    }

    @Test
    public void test_sendInvitationLink_whenCompanyNotFound_thenThrowCompanyNotFoundException() {
        long companyId = 1L;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> companyService.sendInvitationLink(companyId, any(CompanyInvitedAccountDto.class)));
    }

    @Test
    public void test_sendInvitationLink_whenAdminNotFound_thenThrowUserNotFoundException() {
        long companyId = 1L;
        long companyAdminId = 1L;
        Company company = Company.builder()
                .adminId(companyAdminId)
                .name("AW")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.findById(companyAdminId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> companyService.sendInvitationLink(companyId, any(CompanyInvitedAccountDto.class)));
    }

    @Test
    public void test_generateInvitationLink_thenReturnLinkString() {
        long companyId = 1L;
        String link = "http://localhost:3000/test";
        CompanyInvitedAccountDto accountDto = CompanyInvitedAccountDto.builder()
                .email("test@gmail.com")
                .name("test")
                .title("title")
                .build();
        when(linkGenerator.generateCompanyInvitationLink(
                companyId,
                "test@gmail.com",
                "test",
                "title",
                DateTimeUtility.MILLISECONDS_IN_DAY
        )).thenReturn(link);

        String actualLink = companyService.generateCompanyInvitationLink(companyId, accountDto);

        assertEquals(link, actualLink);
        verify(linkGenerator, times(1)).generateCompanyInvitationLink(
                companyId,
                "test@gmail.com",
                "test",
                "title",
                DateTimeUtility.MILLISECONDS_IN_DAY
        );
    }
}
