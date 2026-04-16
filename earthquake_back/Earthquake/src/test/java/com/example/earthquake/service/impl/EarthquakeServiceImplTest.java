package com.example.earthquake.service.impl;

import com.example.earthquake.exception.EarthquakeException;
import com.example.earthquake.model.Earthquake;
import com.example.earthquake.repository.EarthquakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EarthquakeServiceImplTest {

    @Mock
    private EarthquakeRepository earthquakeRepository;

    @InjectMocks
    private EarthquakeServiceImpl earthquakeService;

    private Earthquake eq1;
    private Earthquake eq2;

    @BeforeEach
    void setUp(){
        eq1 = new Earthquake(1L, 3.5,"Skopje", 1000L,1001L,"ml","Skopje Earthquake",-120.2, 36.7);
        eq2 = new Earthquake(2L, 1.5,"Ohrid", 3000L,3001L,"ml","Ohrid Earthquake", -115.2, 39.5);
    }

    @Test
    void testGetAllEarthquakes(){
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1,eq2));

        List<Earthquake> result = earthquakeService.getAllEarthquakes();

        assertThat(result).hasSize(2);
        assertThat(result).contains(eq1,eq2);
    }

    @Test
    void testFilterByBiggerThanMagnitude(){
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1,eq2));

        List<Earthquake> result = earthquakeService.filterByBiggerThanMagnitude(2.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMagnitude()).isGreaterThanOrEqualTo(2.0);
    }

    @Test
    void testFilterByTimeAfter(){
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1,eq2));

        List<Earthquake> result = earthquakeService.filterByTimeAfter(2000L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTime()).isGreaterThanOrEqualTo(2000L);
    }

    @Test
    void testDeleteById(){
        when(earthquakeRepository.existsById(1L)).thenReturn(true);

        earthquakeService.deleteById(1L);
        verify(earthquakeRepository,times(1)).deleteById(1L);

    }

    @Test
    void testDeleteByIdNotFound(){
        when(earthquakeRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(()-> earthquakeService.deleteById(5L))
                .isInstanceOf(EarthquakeException.class)
                .hasMessageContaining("5");
    }

    @Test
    void testFilterByBiggerThanMagnitudeWithNullMagnitude() {
        Earthquake nullMagEq = new Earthquake(3L, null, "Struga", 1000L, 2000L, "ml", "Struga - earthquake", -120.5, 36.7);
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1, nullMagEq));

        List<Earthquake> result = earthquakeService.filterByBiggerThanMagnitude(2.0);

        assertThat(result).hasSize(1);
        assertThat(result).doesNotContain(nullMagEq);
    }

    @Test
    void testFilterByTimeAfterWithNullTime() {
        Earthquake nullTimeEq = new Earthquake(3L, 3.5, "Texas", null, 2000L, "ml", "M - Texas", -120.5, 36.7);
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1, nullTimeEq));

        List<Earthquake> result = earthquakeService.filterByTimeAfter(500L);

        assertThat(result).hasSize(1);
        assertThat(result).doesNotContain(nullTimeEq);
    }

    @Test
    void testFilterByBiggerThanMagnitudeReturnsEmpty() {
        when(earthquakeRepository.findAll()).thenReturn(List.of(eq1, eq2));

        List<Earthquake> result = earthquakeService.filterByBiggerThanMagnitude(5.0);

        assertThat(result).isEmpty();
    }
}
