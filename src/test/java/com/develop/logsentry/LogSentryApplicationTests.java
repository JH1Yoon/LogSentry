package com.develop.logsentry;

import com.develop.logsentry.common.filter.AuthFilter;
import com.develop.logsentry.domain.log.service.LogProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@SpringBootTest
class LogSentryApplicationTests {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private LogProducer logProducer;

    @MockBean
    private AuthFilter authFilter;

    @Test
    void contextLoads() {
    }

}
