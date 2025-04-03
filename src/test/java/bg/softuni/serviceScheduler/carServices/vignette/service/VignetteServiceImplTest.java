package bg.softuni.serviceScheduler.carServices.vignette.service;

import bg.softuni.serviceScheduler.carServices.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.carServices.vignette.model.Vignette;
import bg.softuni.serviceScheduler.carServices.vignette.model.VignetteCost;
import bg.softuni.serviceScheduler.carServices.vignette.model.VignetteValidity;
import bg.softuni.serviceScheduler.carServices.vignette.service.impl.VignetteServiceImpl;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VignetteServiceImplTest {

    private static final UUID CAR_ID = UUID.randomUUID();
    private static final Year CAR_YEAR = Year.of(2000);
    private static final String CAR_VIN = "CARVINNUMBER12345";
    private static final String CAR_REGISTRATION = "BB0000BB";
    private static final VehicleCategory CAR_CATEGORY = VehicleCategory.B;
    private static final UUID VIGNETTE_ID = UUID.randomUUID();
    private static final VignetteValidity VIGNETTE_VALIDITY = VignetteValidity.WEEKEND;
    private static final LocalDate VIGNETTE_START_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate VIGNETTE_END_DATE = VIGNETTE_START_DATE.plusDays(VIGNETTE_VALIDITY.getDays());
    private static final LocalDate VIGNETTE_ADD_DATE = LocalDate.now();
    private static final BigDecimal VIGNETTE_COST = VignetteCost.valueOf(VIGNETTE_VALIDITY.name()).getCost();
    private static final Boolean VIGNETTE_IS_VALID = VIGNETTE_END_DATE.isAfter(LocalDate.now());
    @Mock
    private VignetteRepository vignetteRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    private VignetteService vignetteService;
    private VignetteAddBindingModel vignetteAdd;
    private Vignette vignette;
    private Car car;
    @Captor
    private ArgumentCaptor<Vignette> vignetteCaptor;
    private ArgumentCaptor<List<Vignette>> vignetteListCaptor;

    @BeforeEach
    void setUp() {
        car = new Car(CAR_ID, null, null, CAR_VIN, CAR_YEAR, CAR_CATEGORY, null, CAR_REGISTRATION, null, new ArrayList<>());
        vignetteService = new VignetteServiceImpl(vignetteRepository, carRepository, userRepository);
        vignetteAdd = new VignetteAddBindingModel(VIGNETTE_START_DATE, VIGNETTE_VALIDITY);
        vignette = new Vignette(VIGNETTE_ID, VIGNETTE_ADD_DATE, VIGNETTE_COST, VIGNETTE_START_DATE, VIGNETTE_END_DATE, VIGNETTE_VALIDITY, VIGNETTE_IS_VALID, car);
        vignetteCaptor = ArgumentCaptor.forClass(Vignette.class);
        vignetteListCaptor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void testInvalidateAllExpiredVignettesIsValidReturnsFalseOnExpiredVignette() {
        Vignette expiredVignette = new Vignette(null, null, null, null, LocalDate.now().minusDays(1), null, true, null);
        Vignette expiredVignette2 = new Vignette(null, null, null, null, LocalDate.now().minusDays(5), null, true, null);
        Vignette expiredVignette3 = new Vignette(null, null, null, null, LocalDate.now().minusDays(100), null, true, null);
        Mockito
                .when(vignetteRepository.findAllByIsValidIsTrueAndEndDateIsBefore(Mockito.any(LocalDate.class)))
                .thenReturn(List.of(expiredVignette, expiredVignette2, expiredVignette3));

        vignetteService.invalidateAllExpiredVignettes();

        verify(vignetteRepository).saveAll(vignetteListCaptor.capture());

        List<Vignette> saved = vignetteListCaptor.getValue();

        saved.forEach(vignette -> {
            assertFalse(vignette.getIsValid());
        });
    }

    @Test
    public void testValidateAllExpiredVignettesIsValidReturnsFalseOnExpiredVignette() {
        Vignette expiredVignette = new Vignette(null, null, null, LocalDate.now(), LocalDate.now().plusDays(1), null, false, null);
        Vignette expiredVignette2 = new Vignette(null, null, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), null, false, null);
        Vignette expiredVignette3 = new Vignette(null, null, null, LocalDate.now().minusDays(2), LocalDate.now().plusDays(100), null, false, null);
        Mockito
                .when(vignetteRepository.findAllByIsValidIsFalseAndStartDateIsLessThanEqual(Mockito.any(LocalDate.class)))
                .thenReturn(List.of(expiredVignette, expiredVignette2, expiredVignette3));

        vignetteService.validateAllStartingVignettes();

        verify(vignetteRepository).saveAll(vignetteListCaptor.capture());

        List<Vignette> saved = vignetteListCaptor.getValue();

        saved.forEach(vignette -> {
            assertTrue(vignette.getIsValid());
        });
    }

    @Test
    public void testChangeAllExpiredVignettesIsValidReturnsFalseOnExpiredVignette() {
        Vignette expiredVignette = new Vignette(null, null, null, null, LocalDate.now().minusDays(1), null, true, null);
        Vignette expiredVignette2 = new Vignette(null, null, null, null, LocalDate.now().minusDays(5), null, true, null);
        Vignette expiredVignette3 = new Vignette(null, null, null, null, LocalDate.now().minusDays(100), null, true, null);
        Mockito
                .when(vignetteRepository.findAllByIsValidIsTrueAndEndDateIsBefore(Mockito.any(LocalDate.class)))
                .thenReturn(List.of(expiredVignette, expiredVignette2, expiredVignette3));

        vignetteService.invalidateAllExpiredVignettes();

        verify(vignetteRepository).saveAll(vignetteListCaptor.capture());

        List<Vignette> saved = vignetteListCaptor.getValue();

        saved.forEach(vignette -> {
            assertFalse(vignette.getIsValid());
        });
    }

    @Test
    public void testGetSumVignetteCostByUserIdReturnsCorrectCostWhenUserHasVignettes() {
        Mockito.when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        Mockito
                .when(vignetteRepository.getSumVignetteCostByUserId(Mockito.any())).
                thenReturn(VIGNETTE_COST);

        assertEquals(VIGNETTE_COST, vignetteService.getSumVignetteCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testGetSumVignetteCostByUserIdReturnsBigDecimalZeroWhenNoUserHasNoVignettes() {
        Mockito.when(userRepository.existsById(Mockito.any()))
                .thenReturn(true);

        Mockito
                .when(vignetteRepository.getSumVignetteCostByUserId(Mockito.any())).
                thenReturn(null);

        assertEquals(BigDecimal.ZERO, vignetteService.getSumVignetteCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testGetSumVignetteCostByUserIdThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.existsById(Mockito.any()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> vignetteService.getSumVignetteCostByUserId(UUID.randomUUID()));
    }

    @Test
    public void testHasActiveVignetteReturnsFalseWhenExistsByCarIdAndIsValidIsFalse() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);
        Mockito
                .when(vignetteRepository.existsByCarIdAndIsValidTrue(CAR_ID))
                .thenReturn(false);

        assertFalse(vignetteService.hasActiveVignette(CAR_ID));
    }

    @Test
    public void testHasActiveVignetteReturnsTrueWhenExistsByCarIdAndIsValidIsTrue() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);
        Mockito
                .when(vignetteRepository.existsByCarIdAndIsValidTrue(CAR_ID))
                .thenReturn(true);

        assertTrue(vignetteService.hasActiveVignette(CAR_ID));
    }

    @Test
    public void testHasActiveVignetteThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(false);

        assertThrows(CarNotFoundException.class, () -> vignetteService.hasActiveVignette(CAR_ID));
    }

    @Test
    public void testDoAddSavesVignette() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        Mockito
                .when(vignetteRepository.save(Mockito.any(Vignette.class)))
                .thenReturn(vignette);

        vignetteService.doAdd(vignetteAdd, CAR_ID);

        verify(vignetteRepository).save(vignetteCaptor.capture());

        Vignette saved = vignetteCaptor.getValue();

        assertEquals(LocalDate.now(), saved.getAddedAt(), "wrong added at");
        assertEquals(VIGNETTE_COST, saved.getCost(), "wrong cost");
        assertEquals(VIGNETTE_VALIDITY, saved.getValidity(), "wrong validity");
        assertEquals(VIGNETTE_START_DATE, saved.getStartDate(), "wrong start date");
        assertEquals(VIGNETTE_END_DATE, saved.getEndDate(), "wrong end date");
        assertEquals(VIGNETTE_IS_VALID, saved.getIsValid(), "wrong isValid");
        assertEquals(car, saved.getCar(), "wrong car");
    }

    @Test
    public void testDoAddThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> vignetteService.doAdd(vignetteAdd, CAR_ID));
    }

    @Test
    public void testGetSumVignetteCostByCarIdReturnsCorrectSum() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);

        Mockito
                .when(vignetteRepository.getSumVignetteCostByCarId(CAR_ID))
                .thenReturn(VIGNETTE_COST);

        assertEquals(VIGNETTE_COST, vignetteService.getSumVignetteCostByCarId(CAR_ID));
    }

    @Test
    public void testGetSumInsuranceCostByCarIdThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(false);

        assertThrows(CarNotFoundException.class, () -> vignetteService.getSumVignetteCostByCarId(CAR_ID));
    }

}
