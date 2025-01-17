package nextstep.subway.handler.validator;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.handler.exception.SectionException;

import static nextstep.subway.handler.exception.ErrorCode.*;

public class SectionValidator {
    private final static int MINIMUM_DISTANCE = 0;
    private final static int ONE_SECTION = 1;

    public static void validateDistance(int distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new SectionException(INVALID_DISTANCE);
        }
    }

    public static void validateOnlyOneStationExists(Line line, Station upStation, Station downStation) {
        // 두 역이 모두 노선에 존재하면 안된다.
        if (line.hasStation(upStation) && line.hasStation(downStation)) {
            throw new SectionException(STATIONS_ALL_EXISTS);
        }

        // 두 역이 모두 노선에 존재하지 않아서는 안된다.
        if (!line.hasStation(upStation) && !line.hasStation(downStation)) {
            throw new SectionException(STATIONS_NOT_FOUND_FROM_LINE);
        }
    }

    public static void validateOnlyOneSection(Line line) {
        if (line.getSectionSize() == ONE_SECTION) {
            throw new SectionException(SECTION_REMAINED_ONLY_ONE);
        }
    }
}
