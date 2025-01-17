package nextstep.subway.domain;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.factory.EntityFactory.createStation;
import static nextstep.subway.domain.factory.EntityFactory.createSection;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 단위 테스트")
class LineTest {

    private Station 강남역;
    private Station 선릉역;
    private Line 이호선;

    @BeforeEach
    void init() {
        강남역 = createStation(1L, "강남역");
        선릉역 = createStation(2L, "선릉역");
        이호선 = Line.of("2호선", "green", 강남역, 선릉역, 10);
    }

    @Test
    @DisplayName("구간이 새로 등록되었을때 최상행, 최하행역을 갱신한다.")
    void updateStations() {
        // 새로운 구간이 등록되었을때,
        // (1) 노선의 최상행역과 새로운 구간의 하행역이 같은 경우
        // 노선의 최상행역을 갱신해야한다.
        // (2) 노선의 최하행역과 새로운 구간의 상행역이 같은 경우
        // 노선의 최하행역을 갱신해야한다.

        // given
        Station 역삼역 = createStation(3L, "역삼역");
        Station 교대역 = createStation(4L, "교대역");

        // (1) when/then
        이호선.addSection(createSection(이호선, 역삼역, 강남역, 7));
        assertThat(이호선.isUpStation(역삼역)).isTrue();

        // (2) when/then
        이호선.addSection(createSection(이호선, 선릉역, 교대역, 7));
        assertThat(이호선.isDownStation(교대역)).isTrue();
    }

    @Test
    @DisplayName("구간이 하행선에 이어서 생성된다.")
    void addSection() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");

        // when
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));

        // then
        assertThat(이호선.getSectionSize()).isEqualTo(2);
        assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 선릉역, 역삼역));
    }

    @Test
    @DisplayName("구간을 새롭게 등록한다. - 상행선이 같은 경우")
    void addSection2() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");

        // when
        이호선.addSection(createSection(이호선, 강남역, 역삼역, 7));

        // then
        assertThat(이호선.getSectionSize()).isEqualTo(2);
        assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 역삼역, 선릉역));
        assertThat(이호선.findSectionByDownStation(역삼역).getDistance()).isEqualTo(7);
        assertThat(이호선.findSectionByDownStation(선릉역).getDistance()).isEqualTo(3);
    }

    @Test
    @DisplayName("구간을 새롭게 등록한다. - 하행선이 같은 경우")
    void addSection3() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");

        // when
        이호선.addSection(createSection(이호선, 역삼역, 선릉역, 7));

        // then
        assertThat(이호선.getSectionSize()).isEqualTo(2);
        assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 역삼역, 선릉역));
        assertThat(이호선.findSectionByDownStation(역삼역).getDistance()).isEqualTo(3);
        assertThat(이호선.findSectionByDownStation(선릉역).getDistance()).isEqualTo(7);
    }

    @Test
    @DisplayName("구간을 삭제한다. - 종점을 삭제하는 경우")
    void removeSection() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));

        // when
        이호선.removeSectionByStation(역삼역);

        // then
        assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 선릉역));
        assertThat(이호선.getSections().findSectionByDownStation(선릉역).getDistance()).isEqualTo(10);
        assertThat(이호선.getDownStationName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("구간을 삭제한다. - 최상행역을 삭제하는 경우")
    void removeSection2() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));

        // when
        이호선.removeSectionByStation(강남역);

        // then
        assertThat(이호선.getStations()).containsExactly(Arrays.array(선릉역, 역삼역));
        assertThat(이호선.getSections().findSectionByDownStation(역삼역).getDistance()).isEqualTo(7);
        assertThat(이호선.getUpStationName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("구간을 삭제한다. - 중간역을 삭제하는 경우")
    void removeSection3() {
        // given
        Station 역삼역 = createStation(3L, "역삼역");
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));

        // when
        이호선.removeSectionByStation(선릉역);

        // then
        assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 역삼역));
        assertThat(이호선.getSections().findSectionByDownStation(역삼역).getDistance()).isEqualTo(17);
    }
}