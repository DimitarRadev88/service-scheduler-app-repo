package bg.softuni.serviceScheduler.carServices.insurance.service;

import bg.softuni.serviceScheduler.carServices.insurance.dao.InsuranceRepository;
import bg.softuni.serviceScheduler.carServices.insurance.model.Insurance;
import bg.softuni.serviceScheduler.carServices.insurance.model.InsuranceValidity;
import bg.softuni.serviceScheduler.carServices.insurance.service.impl.InsuranceServiceImpl;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InsuranceServiceTests {

    private static final UUID INSURANCE_ID = UUID.randomUUID();
    private static final String INSURANCE_COMPANY = "Insurance Company";
    private static final InsuranceValidity INSURANCE_VALIDITY = InsuranceValidity.MONTHLY;
    private static final LocalDate INSURANCE_START_DATE = LocalDate.now().minusDays(5);
    private static final LocalDate INSURANCE_END_DATE = INSURANCE_START_DATE.plusDays(INSURANCE_VALIDITY.getDays());
    private static final LocalDate INSURANCE_ADD_DATE = LocalDate.now();
    private static final Boolean INSURANCE_IS_VALID = INSURANCE_END_DATE.isAfter(LocalDate.now());
    private static final BigDecimal INSURANCE_COST = BigDecimal.valueOf(100);
    private static final UUID CAR_ID = UUID.randomUUID();

    private final InsuranceRepository insuranceRepository = Mockito.mock(InsuranceRepository.class);
    private final CarRepository carRepository = Mockito.mock(CarRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private InsuranceService insuranceService;
    private InsuranceAddBindingModel insuranceAdd;
    private Insurance insurance;
    private Car car;
    @Captor
    private ArgumentCaptor<Insurance> insuranceCaptor;
    @Captor
    private ArgumentCaptor<List<Insurance>> insuranceCaptorList;

    @BeforeEach
    public void setUp() {
        insuranceService = new InsuranceServiceImpl(insuranceRepository, carRepository, userRepository);
        insuranceAdd = new InsuranceAddBindingModel(INSURANCE_COMPANY, INSURANCE_START_DATE, INSURANCE_VALIDITY, INSURANCE_COST);
        insurance = new Insurance(INSURANCE_ID, INSURANCE_ADD_DATE, INSURANCE_COST, INSURANCE_COMPANY, INSURANCE_START_DATE, INSURANCE_END_DATE, INSURANCE_VALIDITY, INSURANCE_IS_VALID, null);
        car = new Car(CAR_ID, null, null, null, null, null, null, null, new ArrayList<>(), null);
        insuranceCaptor = ArgumentCaptor.forClass(Insurance.class);
        insuranceCaptorList = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void testChangeAllExpiredInsurancesIsValidStatusChangesExpiredInsurancesIsValidToFalse() {
        Insurance expiredInsuranceOne = new Insurance(null, null, null, null, null, LocalDate.now().minusDays(1), null, true, null);
        Insurance expiredInsuranceTwo = new Insurance(null, null, null, null, null, LocalDate.now().minusDays(2), null, true, null);
        Insurance expiredInsuranceThree = new Insurance(null, null, null, null, null, LocalDate.now().minusDays(3), null, true, null);

        List<Insurance> insurances = List.of(expiredInsuranceOne, expiredInsuranceTwo, expiredInsuranceThree);
        Mockito
                .when(insuranceRepository.findAllByIsValidIsTrueAndEndDateIsBefore(Mockito.any(LocalDate.class)))
                .thenReturn(insurances);

        insuranceService.changeAllExpiredInsurancesIsValidStatus();

        verify(insuranceRepository).saveAll(insuranceCaptorList.capture());

        List<Insurance> saved = insuranceCaptorList.getValue();

        saved.forEach(insurance -> assertFalse(insurance.getIsValid()));
    }

    @Test
    public void testGetSumInsuranceCostByUserIdReturnsSumWhenUserHasInsuranceWhenUserHasNoInsurances() {
        Mockito
                .when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        Mockito
                .when(insuranceRepository.getSumInsuranceCostByUserId(Mockito.any()))
                .thenReturn(INSURANCE_COST);

        assertEquals(INSURANCE_COST, insuranceService.getSumInsuranceCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testGetSumInsuranceCostByUserIdReturnsBigDecimalZeroWhenUserHasNoInsurances() {
        Mockito
                .when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        Mockito
                .when(insuranceRepository.getSumInsuranceCostByUserId(Mockito.any()))
                .thenReturn(null);

        assertEquals(BigDecimal.ZERO, insuranceService.getSumInsuranceCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testGetSumInsuranceCostByUserIdThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.existsById(Mockito.any()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> insuranceService.getSumInsuranceCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testHasActiveInsuranceReturnsTrueWhenValidInsuranceNotFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);

        Mockito.when(insuranceRepository.existsByIsValidTrueAndCarId(CAR_ID))
                .thenReturn(false);

        assertFalse(insuranceService.hasActiveInsurance(CAR_ID));
    }

    @Test
    public void testHasActiveInsuranceReturnsTrueWhenValidInsuranceFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);

        Mockito.when(insuranceRepository.existsByIsValidTrueAndCarId(CAR_ID))
                .thenReturn(true);

        assertTrue(insuranceService.hasActiveInsurance(CAR_ID));
    }

    @Test
    public void testHasActiveInsuranceThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(false);

        assertThrows(CarNotFoundException.class, () -> insuranceService.hasActiveInsurance(CAR_ID));
    }

    @Test
    public void testDoAddAddsInsuranceInCarInsurances() {
        insurance.setCar(car);
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        Mockito.when(insuranceRepository.save(Mockito.any(Insurance.class)))
                .thenReturn(insurance);

        Mockito.when(carRepository.save(car))
                        .thenReturn(car);

        insuranceService.doAdd(insuranceAdd, car.getId());

        verify(insuranceRepository).save(insuranceCaptor.capture());

        Insurance savedInsurance = insuranceCaptor.getValue();

        assertEquals(insurance.getAddedAt(), savedInsurance.getAddedAt(), "wrong add date");
        assertEquals(insurance.getCompanyName(), savedInsurance.getCompanyName(), "wrong company name");
        assertEquals(insurance.getStartDate(), savedInsurance.getStartDate(), "wrong start date");
        assertEquals(insurance.getEndDate(), savedInsurance.getEndDate(), "wrong end date");
        assertEquals(insurance.getValidity(), savedInsurance.getValidity(), "wrong validity");
        assertEquals(insurance.getIsValid(), savedInsurance.getIsValid(), "wrong is valid");
        assertEquals(insurance.getCar(), savedInsurance.getCar(), "wrong car");
    }

    @Test
    public void testDoAddThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> insuranceService.doAdd(insuranceAdd, CAR_ID));
    }

}
