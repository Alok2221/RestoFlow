package pl.example.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.example.restaurant.entity.Category;
import pl.example.restaurant.entity.FoodType;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.repository.DishRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private MenuService menuService;

    private Dish testDish1;
    private Dish testDish2;
    private Dish testDish3;

    @BeforeEach
    void setUp() {
        testDish1 = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .description("Domowa zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        testDish2 = Dish.builder()
                .id(2L)
                .name("Schabowy")
                .description("Tradycyjny schabowy")
                .price(new BigDecimal("45.00"))
                .category(Category.DANIE_GLOWNE)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        testDish3 = Dish.builder()
                .id(3L)
                .name("Sernik")
                .description("Tradycyjny sernik")
                .price(new BigDecimal("22.00"))
                .category(Category.DESER)
                .foodType(FoodType.NORMALNE)
                .available(false)
                .build();
    }

    @Test
    void testGetAll() {
        // Given
        List<Dish> allDishes = Arrays.asList(testDish1, testDish2, testDish3);
        when(dishRepository.findAll()).thenReturn(allDishes);

        // When
        List<Dish> result = menuService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(dishRepository).findAll();
    }

    @Test
    void testGetByCategory() {
        // Given
        List<Dish> przystawki = Arrays.asList(testDish1);
        when(dishRepository.findByCategory(Category.PRZYSTAWKA)).thenReturn(przystawki);

        // When
        List<Dish> result = menuService.getByCategory(Category.PRZYSTAWKA);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Category.PRZYSTAWKA, result.get(0).getCategory());
        verify(dishRepository).findByCategory(Category.PRZYSTAWKA);
    }

    @Test
    void testGetAvailable() {
        // Given
        List<Dish> availableDishes = Arrays.asList(testDish1, testDish2);
        when(dishRepository.findByAvailableTrue()).thenReturn(availableDishes);

        // When
        List<Dish> result = menuService.getAvailable();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Dish::isAvailable));
        verify(dishRepository).findByAvailableTrue();
    }

    @Test
    void testGetById_Success() {
        // Given
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish1));

        // When
        Dish result = menuService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Zupa pomidorowa", result.getName());
        verify(dishRepository).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(dishRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> menuService.getById(999L));
        verify(dishRepository).findById(999L);
    }

    @Test
    void testSearchByName() {
        // Given
        List<Dish> searchResults = Arrays.asList(testDish1);
        when(dishRepository.findByNameContainingIgnoreCase("pomidorowa")).thenReturn(searchResults);

        // When
        List<Dish> result = menuService.searchByName("pomidorowa");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("pomidorowa"));
        verify(dishRepository).findByNameContainingIgnoreCase("pomidorowa");
    }

    @Test
    void testSearchByName_CaseInsensitive() {
        // Given
        List<Dish> searchResults = Arrays.asList(testDish1);
        when(dishRepository.findByNameContainingIgnoreCase("POMIDOROWA")).thenReturn(searchResults);

        // When
        List<Dish> result = menuService.searchByName("POMIDOROWA");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dishRepository).findByNameContainingIgnoreCase("POMIDOROWA");
    }
}
