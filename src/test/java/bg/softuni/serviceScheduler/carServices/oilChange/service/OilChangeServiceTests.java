package bg.softuni.serviceScheduler.carServices.oilChange.service;

import bg.softuni.serviceScheduler.carServices.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.carServices.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.vehicle.exception.EngineNotFoundException;
import bg.softuni.serviceScheduler.carServices.oilChange.service.impl.OilChangeServiceImpl;
import bg.softuni.serviceScheduler.vehicle.dao.EngineRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OilChangeServiceTests {

    private static final UUID CAR_ID = UUID.randomUUID();
    private static final Year CAR_YEAR = Year.of(2000);
    private static final String CAR_VIN = "CARVINNUMBER12345";
    private static final String CAR_REGISTRATION = "BB0000BB";
    private static final VehicleCategory CAR_CATEGORY = VehicleCategory.B;
    private static final UUID ENGINE_ID = UUID.randomUUID();
    public static final FuelType ENGINE_FUEL_TYPE = FuelType.PETROL;
    public static final int ENGINE_DISPLACEMENT = 2000;
    public static final double ENGINE_OIL_CAPACITY = 4.0;
    public static final int ENGINE_MILEAGE = 123000;
    public static final String ENGINE_OIL_FILTER_NUMBER = "Oil filter number";
    private static final UUID OIL_CHANGE_ID = UUID.randomUUID();
    private static final Integer OIL_CHANGE_MILEAGE = ENGINE_MILEAGE - 1000;
    private static final Integer OIL_CHANGE_INTERVAL = 10000;
    private static final LocalDate OIL_CHANGE_DATE = LocalDate.now().minusDays(6);
    private static final LocalDate OIL_CHANGE_ADDED_AT = LocalDate.now();
    private static final BigDecimal OIL_CHANGE_COST = BigDecimal.ONE;


    private final OilChangeRepository oilChangeRepository = Mockito.mock(OilChangeRepository.class);
    private final EngineRepository engineRepository = Mockito.mock(EngineRepository.class);
    private OilChangeService oilChangeService;
    private Engine engine;
    private OilChangeAddBindingModel oilChangeAdd;
    private OilChange oilChange;
    @Captor
    private ArgumentCaptor<OilChange> oilChangeCaptor;
    private Car car;
    @BeforeEach
    public void setup() {
        this.oilChangeService = new OilChangeServiceImpl(oilChangeRepository, engineRepository);
        this.engine = new Engine(ENGINE_ID, ENGINE_DISPLACEMENT, ENGINE_FUEL_TYPE, ENGINE_OIL_CAPACITY,
                ENGINE_MILEAGE, ENGINE_OIL_FILTER_NUMBER, null, new ArrayList<>());
        this.oilChange = new OilChange(OIL_CHANGE_ID, this.engine, OIL_CHANGE_COST, OIL_CHANGE_ADDED_AT, OIL_CHANGE_MILEAGE, OIL_CHANGE_INTERVAL, OIL_CHANGE_DATE);
        this.oilChangeAdd = new OilChangeAddBindingModel(OIL_CHANGE_DATE, OIL_CHANGE_MILEAGE, OIL_CHANGE_INTERVAL, OIL_CHANGE_COST);
        this.car = new Car(CAR_ID, null, this.engine, CAR_VIN, CAR_YEAR, CAR_CATEGORY, null, CAR_REGISTRATION, null, null);
        this.engine.setCar(car);
        oilChangeCaptor = ArgumentCaptor.forClass(OilChange.class);
    }

    @Test
    public void testGetOilChangesCountReturnsCorrectCount() {
        Mockito
                .when(this.oilChangeRepository.count())
                .thenReturn(1L);

        assertEquals(1L, this.oilChangeService.getOilChangesCount());
    }

    @Test
    public void testDoAddSavesOilChange() {
        Mockito
                .when(this.engineRepository.findById(ENGINE_ID))
                .thenReturn(Optional.of(this.engine));

        Mockito
                .when(this.oilChangeRepository.save(Mockito.any()))
                .thenReturn(this.oilChange);

        UUID carId = oilChangeService.doAdd(oilChangeAdd, ENGINE_ID);

        verify(oilChangeRepository).save(oilChangeCaptor.capture());

        OilChange saved = oilChangeCaptor.getValue();

        assertEquals(CAR_ID, carId, "wrong car id");
        assertEquals(engine, saved.getEngine(), "wrong engine");
        assertEquals(OIL_CHANGE_COST, saved.getCost(), "wrong cost");
        assertEquals(OIL_CHANGE_ADDED_AT, saved.getAddedAt(), "wrong added at");
        assertEquals(OIL_CHANGE_MILEAGE, saved.getMileage(), "wrong mileage");
        assertEquals(OIL_CHANGE_INTERVAL, saved.getChangeInterval(), "wrong change interval");
        assertEquals(OIL_CHANGE_DATE, saved.getChangeDate(), "wrong change date");
    }

    @Test
    public void testDoAddThrowsWhenEngineNotFound() {
        Mockito
                .when(this.engineRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        assertThrows(EngineNotFoundException.class, () -> this.oilChangeService.doAdd(this.oilChangeAdd, ENGINE_ID));
    }



}
