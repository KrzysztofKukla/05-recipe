package guru.springframework.recipe.service.impl;

import guru.springframework.recipe.commands.UnitOfMeasureCommand;
import guru.springframework.recipe.converter.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.recipe.domain.UnitOfMeasure;
import guru.springframework.recipe.repository.UnitOfMeasureRepository;
import guru.springframework.recipe.service.UnitOfMeasureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Kukla
 */
@Service
@RequiredArgsConstructor
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    @Override
    public UnitOfMeasure findByDescription(String description) {
        Optional<UnitOfMeasure> byDescription = unitOfMeasureRepository.findByDescription(description);
        return byDescription.orElseThrow(()->new RuntimeException("Unit of measure does not exist-> "+description));
    }

    @Override
    public Set<UnitOfMeasureCommand> findAllUom() {
        return unitOfMeasureRepository.findAll().stream()
            .map(unitOfMeasureToUnitOfMeasureCommand::convert)
            .collect(Collectors.toSet());
    }
}
