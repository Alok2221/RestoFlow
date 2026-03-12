package pl.example.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.example.restaurant.entity.Category;
import pl.example.restaurant.entity.FoodType;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.service.MenuService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @Test
    void testGetMenu_AllDishes() throws Exception {
        // Given
        Dish dish1 = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        Dish dish2 = Dish.builder()
                .id(2L)
                .name("Schabowy")
                .price(new BigDecimal("45.00"))
                .category(Category.DANIE_GLOWNE)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        List<Dish> dishes = Arrays.asList(dish1, dish2);
        when(menuService.getAll()).thenReturn(dishes);

        // When & Then
        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Zupa pomidorowa"))
                .andExpect(jsonPath("$[1].name").value("Schabowy"));
    }

    @Test
    void testGetMenu_WithSearchQuery() throws Exception {
        // Given
        Dish dish = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        List<Dish> dishes = Arrays.asList(dish);
        when(menuService.searchByName("pomidorowa")).thenReturn(dishes);

        // When & Then
        mockMvc.perform(get("/api/menu").param("q", "pomidorowa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Zupa pomidorowa"));
    }

    @Test
    void testGetMenu_Available() throws Exception {
        // Given
        Dish dish = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        List<Dish> dishes = Arrays.asList(dish);
        when(menuService.getAvailable()).thenReturn(dishes);

        // When & Then
        mockMvc.perform(get("/api/menu/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void testGetMenu_ByCategory() throws Exception {
        // Given
        Dish dish = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        List<Dish> dishes = Arrays.asList(dish);
        when(menuService.getByCategory(Category.PRZYSTAWKA)).thenReturn(dishes);

        // When & Then
        mockMvc.perform(get("/api/menu/category/PRZYSTAWKA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("PRZYSTAWKA"));
    }

    @Test
    void testGetMenu_ById() throws Exception {
        // Given
        Dish dish = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .description("Domowa zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .category(Category.PRZYSTAWKA)
                .foodType(FoodType.NORMALNE)
                .available(true)
                .build();

        when(menuService.getById(1L)).thenReturn(dish);

        // When & Then
        mockMvc.perform(get("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Zupa pomidorowa"))
                .andExpect(jsonPath("$.price").value(18.50));
    }
}
