package org.owasp.webgoat.plugin.introduction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.webgoat.plugins.LessonTest;
import org.owasp.webgoat.session.WebgoatContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Benedikt Stuhrmann
 * @since 11/07/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlInjectionLesson10Test extends LessonTest {

    @Autowired
    private WebgoatContext context;

    private String completedError = "JSON path \"lessonCompleted\"";

    @Before
    public void setup() {
        SqlInjection sql = new SqlInjection();
        when(webSession.getCurrentLesson()).thenReturn(sql);
        when(webSession.getWebgoatContext()).thenReturn(context);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void tableExistsIsFailure() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/SqlInjection/attack10")
                    .param("action", ""))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("lessonCompleted", is(false)))
                    .andExpect(jsonPath("$.feedback", is(this.modifySpan(messages.getMessage("sql-injection.10.entries")))));
        } catch (AssertionError e) {
            if (!e.getMessage().contains(completedError)) throw e;

            mockMvc.perform(MockMvcRequestBuilders.post("/SqlInjection/attack10")
                    .param("action", ""))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("lessonCompleted", is(true)))
                    .andExpect(jsonPath("$.feedback", is(this.modifySpan(messages.getMessage("sql-injection.10.success")))));
        }
    }

    @Test
    public void tableMissingIsSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/SqlInjection/attack10")
                .param("action", "%'; DROP TABLE access_log;--"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("lessonCompleted", is(true)))
                .andExpect(jsonPath("$.feedback", is(this.modifySpan(messages.getMessage("sql-injection.10.success")))));
    }

    private String modifySpan(String message) {
        return message.replace("</span>", "<\\/span>");
    }
}