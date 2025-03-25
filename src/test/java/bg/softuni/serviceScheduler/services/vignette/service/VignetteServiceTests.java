package bg.softuni.serviceScheduler.services.vignette.service;

import bg.softuni.serviceScheduler.services.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.services.vignette.model.Vignette;
import bg.softuni.serviceScheduler.services.vignette.model.VignetteCost;
import bg.softuni.serviceScheduler.services.vignette.model.VignetteValidity;
import bg.softuni.serviceScheduler.services.vignette.service.impl.VignetteServiceImpl;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VignetteServiceTests {

    private static final UUID CAR_ID = UUID.randomUUID();
    private static final Year CAR_YEAR = Year.of(2000);
    private static final String CAR_VIN = "CARVINNUMBER12345";
    private static final String CAR_REGISTRATION = "BB0000BB";
    private static final VehicleCategory CAR_CATEGORY = VehicleCategory.B;
    private static final UUID VIGNETTE_ID = UUID.randomUUID();
    private static final VignetteValidity VIGNETTE_VALIDITY = VignetteValidity.WEEKEND;
    private static final LocalDate VIGNETTE_START_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate VIGNETTE_END_DATE = LocalDate.now().plusDays(VIGNETTE_VALIDITY.getDays());
    private static final LocalDate VIGNETTE_ADD_DATE = LocalDate.now();
    private static final BigDecimal VIGNETTE_COST = BigDecimal.ONE;
    private static final Boolean VIGNETTE_IS_VALID = VIGNETTE_END_DATE.isAfter(LocalDate.now());

    private final VignetteRepository vignetteRepository = Mockito.mock(VignetteRepository.class);
    private final CarRepository carRepository = Mockito.mock(CarRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private VignetteService vignetteService;
    private VignetteAddBindingModel vignetteAdd;
    private Vignette vignette;
    private Car car;

    @BeforeEach
    public void setUp() {
        car = new Car(CAR_ID, null, null, CAR_VIN, CAR_YEAR, CAR_CATEGORY, null, CAR_REGISTRATION, null, new ArrayList<>());
        vignetteService = new VignetteServiceImpl(vignetteRepository, carRepository, userRepository);
        vignetteAdd = new VignetteAddBindingModel(VIGNETTE_START_DATE, VIGNETTE_VALIDITY);
        vignette = new Vignette(VIGNETTE_ID, VIGNETTE_ADD_DATE, VIGNETTE_COST, VIGNETTE_START_DATE, VIGNETTE_END_DATE, VIGNETTE_VALIDITY, VIGNETTE_IS_VALID, car);
    }

    @Test
    public void testChangeAllExpiredVignettesIsValidReturnsFalseOnExpiredVignette() {
        Vignette expiredVignette = new Vignette(null, null, null, null, LocalDate.now().minusDays(1), null, true, null);
        Mockito
                .when(vignetteRepository.findAllByIsValidIsTrueAndEndDateIsBefore(Mockito.any(LocalDate.class)))
                .thenReturn(List.of(expiredVignette));

        vignetteService.changeAllExpiredVignettesIsValidStatus();

        assertFalse(expiredVignette.getIsValid());
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


        Mockito.when(carRepository.save(car)).thenReturn(car);

        this.vignetteService.doAdd(vignetteAdd, CAR_ID);

        assertEquals(vignette.getId(), car.getVignettes().getFirst().getId());
    }

    @Test
    public void testDoAddThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> vignetteService.doAdd(vignetteAdd, CAR_ID));
    }

}
