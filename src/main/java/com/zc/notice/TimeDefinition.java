package com.zc.notice;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明 . <br>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2017/12/08 下午10:03
 * <p>
 * Company: xxx
 * <p>
 *
 * @author zhongcheng_m@yeah.net
 * @version 1.0.0
 */
public class TimeDefinition {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long[] intervals;

    private static final String DEFAULT_INTERVALS = "3/5/5/5/5/5/5/5/5";

    private static final String DELIMITER = "/";

    public TimeDefinition() {
        this(DEFAULT_INTERVALS);
    }

    public TimeDefinition(String timeDefinition) {

        if (StringUtils.isEmpty(timeDefinition)) {
            timeDefinition = DEFAULT_INTERVALS;
        }

        String[] timeDefinitionArr = timeDefinition.split(DELIMITER);

        int length = timeDefinitionArr.length;
        intervals = new Long[length];

        for (int i = 0; i < length; i++) {
            Long temp = Long.valueOf(timeDefinitionArr[i]);
            intervals[i] = temp;
        }

    }

    public Long[] getIntervals() {
        return intervals;
    }


}
