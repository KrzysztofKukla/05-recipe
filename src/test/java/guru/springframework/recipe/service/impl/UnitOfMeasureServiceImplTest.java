package guru.springframework.recipe.service.impl;

import guru.springframework.recipe.commands.UnitOfMeasureCommand;
import guru.springframework.recipe.converter.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.recipe.domain.UnitOfMeasure;
import guru.springframework.recipe.repository.UnitOfMeasureRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Krzysztof Kukla
 */
@ExtendWith(MockitoExtension.class)
class UnitOfMeasureServiceImplTest {

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @Mock
    private UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    @InjectMocks
    private UnitOfMeasureServiceImpl unitOfMeasureService;

    @Test
    void findByDescription() {
        String description = "description";
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder().id(1L).description(description).build();

        BDDMockito.when(unitOfMeasureRepository.findByDescription(description)).thenReturn(Optional.of(unitOfMeasure));

        Assertions.assertEquals(description, unitOfMeasureService.findByDescription(description).getDescription());
        BDDMockito.then(unitOfMeasureRepository).should().findByDescription(ArgumentMatchers.anyString());
    }

    @Test
    void findAll() {
        UnitOfMeasure unitOfMeasure1 = UnitOfMeasure.builder().id(1L).description("desciption1").build();
        UnitOfMeasure unitOfMeasure2 = UnitOfMeasure.builder().id(2L).description("desciption2").build();
        List<UnitOfMeasure> unitOfMeasuresList = Arrays.asList(unitOfMeasure1, unitOfMeasure2);

        UnitOfMeasureCommand measureCommand1 = UnitOfMeasureCommand.builder().id(1L).description("description1").build();
        UnitOfMeasureCommand measureCommand2 = UnitOfMeasureCommand.builder().id(2L).description("description2").build();

        BDDMockito.when(unitOfMeasureRepository.findAll()).thenReturn(unitOfMeasuresList);
        BDDMockito.when(unitOfMeasureToUnitOfMeasureCommand.convert(unitOfMeasure1)).thenReturn(measureCommand1);
        BDDMockito.when(unitOfMeasureToUnitOfMeasureCommand.convert(unitOfMeasure2)).thenReturn(measureCommand2);

        org.assertj.core.api.Assertions.assertThat(unitOfMeasureService.findAllUom()).hasSize(unitOfMeasuresList.size());
        BDDMockito.then(unitOfMeasureRepository).should().findAll();
        BDDMockito.then(unitOfMeasureToUnitOfMeasureCommand).should(BDDMockito.times(2)).convert(ArgumentMatchers.any(UnitOfMeasure.class));

    }

}