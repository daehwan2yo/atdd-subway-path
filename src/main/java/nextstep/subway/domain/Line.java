package nextstep.subway.domain;

import nextstep.subway.handler.validator.SectionValidator;
import nextstep.subway.handler.validator.StationValidator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    public Line() {
    }

    private Line(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        sections.add(initSection(upStation, downStation, distance));
    }

    public static Line of(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(id, name, color, upStation, downStation, distance);
    }

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        sections.add(initSection(upStation, downStation, distance));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    private Section initSection(Station upStation, Station downStation, int distance) {
        return Section.initialize(this, upStation, downStation, distance);
    }

    /* 구간 추가 */
    public void addSection(Section section) {
        if (isUpStation(section.getDownStation())) {
            this.upStation = section.getUpStation();
        }

        if (isDownStation(section.getUpStation())) {
            this.downStation = section.getDownStation();
        }

        insertSection(section);
    }

    private void insertSection(Section insertedSection) {
        bothUpStationSame(insertedSection);
        bothDownStationSame(insertedSection);

        sections.add(insertedSection);
    }

    private void bothUpStationSame(Section insertedSection) {
        sections.findSameUpDifferentDown(insertedSection)
                .ifPresent(oldSection -> {
                    pushSection(insertedSection.getDownStation(), oldSection.getDownStation(),
                            extractDistance(insertedSection, oldSection));
                    removeSection(oldSection);
                });
    }

    private void bothDownStationSame(Section insertedSection) {
        sections.findSameDownDifferentUp(insertedSection)
                .ifPresent(oldSection -> {
                    pushSection(oldSection.getUpStation(), insertedSection.getUpStation(),
                            extractDistance(insertedSection, oldSection));
                    removeSection(oldSection);
                });
    }

    private int extractDistance(Section insertedSection, Section oldSection) {
        return oldSection.getDistance() - insertedSection.getDistance();
    }

    private void pushSection(Station upStation, Station downStation, int newDistance) {
        sections.add(Section.of(this, upStation, downStation, newDistance));
    }

    /* 갖고있는 지하철역 리스트 반환 */
    public List<Station> getStations() {
        return sections.getStations(upStation);
    }

    /* 노선 정보 변경 */
    public void update(String name, String color) {
        if (name != null && !this.name.equals(name)) {
            this.name = name;
        }
        if (color != null && !this.color.equals(color)) {
            this.color = color;
        }
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean hasStation(Station station) {
        return sections.hasStation(station);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Section findSectionByDownStation(Station station) {
        return sections.findSectionByDownStation(station);
    }

    public Sections getSections() {
        return sections;
    }

    public int getSectionSize() {
        return sections.size();
    }

    public void removeSectionByStation(Station station) {
        SectionValidator.validateOnlyOneSection(this);
        StationValidator.validateStationRemove(this, station);

        if (isDownStation(station)) {
            removeDownSection(station);
            return;
        }

        if (isUpStation(station)) {
            removeUpSection(station);
            return;
        }

        removeCentralSection(station);
    }

    private void removeUpSection(Station station) {
        Section targetSection = sections.findSectionByUpStation(station);
        removeSection(targetSection);
        updateUpStation(targetSection);
    }

    private void removeDownSection(Station station) {
        Section targetSection = sections.findSectionByDownStation(station);
        removeSection(targetSection);
        updateDownStation(targetSection);
    }

    private void removeCentralSection(Station station) {
        Station newUpStation = null;
        Station newDownStation = null;
        int newDistance = 0;

        for (Section section : sections.findSectionByStation(station)) {
            if (section.hasDownStation(station)) {
                newUpStation = section.getUpStation();
                newDistance += section.getDistance();
                removeSection(section);
            }
            if (section.hasUpStation(station)) {
                newDownStation = section.getDownStation();
                newDistance += section.getDistance();
                removeSection(section);
            }
        }

        pushInitSection(newUpStation, newDownStation, newDistance);
    }

    private void pushInitSection(Station upStation, Station downStation, int distance) {
        sections.add(Section.initialize(this, upStation, downStation, distance));
    }

    private void updateUpStation(Section targetSection) {
        upStation = targetSection.getDownStation();
    }

    private void updateDownStation(Section targetSection) {
        downStation = targetSection.getUpStation();
    }

    private void removeSection(Section targetSection) {
        sections.remove(targetSection);
    }

    public void addStationsInGraphForExplore(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.sections.addStationsInGraph(graph);
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    /* getter */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getUpStationName() {
        return upStation.getName();
    }

    public String getDownStationName() {
        return downStation.getName();
    }
}
