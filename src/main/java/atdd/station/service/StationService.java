package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.StationCreateRequestDto;
import atdd.station.dto.StationDetailResponseDto;
import atdd.station.dto.StationListResponseDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service("stationService")
public class StationService {
    @Resource(name = "stationRepository")
    private StationRepository stationRepository;

    public Station create(StationCreateRequestDto station) {
        return stationRepository.save(station.toEntity());
    }

    public StationListResponseDto list() {
        return StationListResponseDto.toDtoEntity(stationRepository.findAll());
    }

    public StationDetailResponseDto findById(long id) {
        Station station = stationRepository.findById(id).orElseThrow(IllegalAccessError::new);
        return StationDetailResponseDto.toDtoEntity(station);
    }

    public void delete(long id) {
        Optional<Station> station = stationRepository.findById(id);
        station.orElseThrow(IllegalAccessError::new).deleteStation();
    }
}