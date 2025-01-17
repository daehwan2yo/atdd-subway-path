package nextstep.subway.domain;

import nextstep.subway.handler.exception.ErrorCode;
import nextstep.subway.handler.exception.SectionException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public int size() {
        return sections.size();
    }

    public List<Station> getStations(Station upStation) {
        List<Station> stations = new ArrayList<>();
        List<Section> tmpSections = new ArrayList<>(sections);
        Station station = upStation;
        Section section;

        while (!tmpSections.isEmpty()) {
            section = findSectionByUpStation(tmpSections, station);
            stations.add(section.getUpStation());
            station = section.getDownStation();
            tmpSections.remove(section);
        }

        stations.add(station);
        return stations;
    }

    private Section findSectionByUpStation(List<Section> tmpSections, Station targetStation) {
        return tmpSections.stream()
                .filter(section -> section.hasUpStation(targetStation))
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NO_CORRECT_SECTION));
    }

    public void remove(Section section) {
        if (!sections.contains(section)) {
            throw new SectionException(ErrorCode.SECTION_NOT_FOUND);
        }
        sections.remove(section);
    }

    public Section findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.hasDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NO_CORRECT_SECTION));
    }

    public boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Optional<Section> findSameUpDifferentDown(Section section) {
        return sections.stream()
                .filter(oldSection -> oldSection.hasUpStation(section.getUpStation())
                        && !oldSection.hasDownStation(section.getDownStation()))
                .findFirst();
    }

    public Optional<Section> findSameDownDifferentUp(Section section) {
        return sections.stream()
                .filter(oldSection -> oldSection.hasDownStation(section.getDownStation())
                        && !oldSection.hasUpStation(section.getUpStation()))
                .findFirst();
    }

    public Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElseThrow(() -> new SectionException(ErrorCode.NO_CORRECT_SECTION));
    }

    public List<Section> findSectionByStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasStation(station))
                .collect(Collectors.toList());
    }

    public void addStationsInGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.forEach(section -> {
            int distance = section.getDistance();
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
        });
    }
}
