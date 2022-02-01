package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 서비스 단위 테스트")
@SpringBootTest
@Transactional
public class LineServiceTest {

    static final Distance 강남_판교_거리 = Distance.valueOf(10);
    static final int 판교_양재_거리 = 9;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        stationRepository.save(강남역);
        stationRepository.save(판교역);
        stationRepository.save(양재역);

        Line 신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 강남_판교_거리);
        lineRepository.save(신분당선);

        // when
        // lineService.addSection 호출
        lineService.addSection(
                신분당선.getId(),
                new SectionRequest(판교역.getId(), 양재역.getId(), 판교_양재_거리));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.getSectionSize()).isEqualTo(2);
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역, 양재역);
    }
}
