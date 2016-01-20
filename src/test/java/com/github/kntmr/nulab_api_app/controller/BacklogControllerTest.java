package com.github.kntmr.nulab_api_app.controller;

import com.github.kntmr.nulab_api_app.Application;
import com.github.kntmr.nulab_api_app.http.IssueClient;
import com.github.kntmr.nulab_api_app.http.ProjectClient;
import com.nulabinc.backlog4j.*;
import com.nulabinc.backlog4j.internal.json.InternalFactoryJSONImpl;
import com.nulabinc.backlog4j.internal.json.ResponseListImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.ViewResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BacklogControllerTest {

    @Rule
    public final MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private BacklogController sut;

    @Mock
    private ProjectClient projectClient;
    @Mock
    private IssueClient issueClient;

    private MockMvc mockMvc;
    private InternalFactoryJSONImpl factory = new InternalFactoryJSONImpl();

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void getProjects_プロジェクトが存在する() throws Exception {
        // setup
        ResponseList<Project> projects = factory.createProjectList(getJSONString("/json/projects.json"));
        Mockito.when(projectClient.getProjects()).thenReturn(projects);

        // exercise
        ResultActions actual = mockMvc.perform(get("/"));

        // verify
        actual.andExpect(status().isOk())
                .andExpect(view().name("projects"))
                .andExpect(model().attribute("projects", projects));
    }

    @Test
    public void getProjects_プロジェクト0件() throws Exception {
        // setup
        ResponseList<Project> empty = new ResponseListImpl<>();
        Mockito.when(projectClient.getProjects()).thenReturn(empty);

        // exercise
        ResultActions actual = mockMvc.perform(get("/"));

        // verify
        actual.andExpect(status().isOk())
                .andExpect(view().name("projects"))
                .andExpect(model().attribute("projects", empty));
    }

    @Test
    public void getIssues_課題が存在する() throws Exception {
        // setup
        Project project = factory.createProject(getJSONString("/json/project.json"));
        ResponseList<Issue> issues = factory.createIssueList(getJSONString("/json/issues.json"));
        ResponseList<IssueType> issueTypes = factory.createIssueTypeList(getJSONString("/json/issue_types.json"));
        ResponseList<Priority> priorities = factory.createPriorityList(getJSONString("/json/priorities.json"));
        Mockito.when(projectClient.getProject(1)).thenReturn(project);
        Mockito.when(issueClient.getIssues(1)).thenReturn(issues);
        Mockito.when(issueClient.getIssueTypes(1)).thenReturn(issueTypes);
        Mockito.when(issueClient.getPriorities()).thenReturn(priorities);

        // exercise
        ResultActions actual = mockMvc.perform(get("/project/1"));

        // verify
        actual.andExpect(status().isOk())
                .andExpect(view().name("issues"))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("issues", issues))
                .andExpect(model().attribute("issueTypes", issueTypes))
                .andExpect(model().attribute("priorities", priorities));
    }

    @Test
    public void getIssues_課題0件() throws Exception {
        // setup
        Project project = factory.createProject(getJSONString("/json/project.json"));
        ResponseList<Issue> empty = new ResponseListImpl<>();
        ResponseList<IssueType> issueTypes = factory.createIssueTypeList(getJSONString("/json/issue_types.json"));
        ResponseList<Priority> priorities = factory.createPriorityList(getJSONString("/json/priorities.json"));
        Mockito.when(projectClient.getProject(1)).thenReturn(project);
        Mockito.when(issueClient.getIssues(1)).thenReturn(empty);
        Mockito.when(issueClient.getIssueTypes(1)).thenReturn(issueTypes);
        Mockito.when(issueClient.getPriorities()).thenReturn(priorities);

        // exercise
        ResultActions actual = mockMvc.perform(get("/project/1"));

        // verify
        actual.andExpect(status().isOk())
                .andExpect(view().name("issues"))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("issues", empty))
                .andExpect(model().attribute("issueTypes", issueTypes))
                .andExpect(model().attribute("priorities", priorities));
    }

    @Test
    public void addIssue_summaryが存在する() throws Exception {
        // setup
        String summary = "Test Summary";

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1"))
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("message", nullValue()));
    }

    @Test
    public void addIssue_summaryが空() throws Exception {
        // setup
        String summary = "";

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1"))
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("message", "summary is required."));
    }

    @Test
    public void addIssue_summaryがnull() throws Exception {
        // setup
        String summary = null;

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1"))
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("message", "summary is required."));
    }

    @Test
    public void getIssue_課題が存在する() throws Exception {
        // setup
        Project project = factory.createProject(getJSONString("/json/project.json"));
        Issue issue = factory.createIssue(getJSONString("/json/issue.json"));
        ResponseList<IssueType> issueTypes = factory.createIssueTypeList(getJSONString("/json/issue_types.json"));
        ResponseList<Priority> priorities = factory.createPriorityList(getJSONString("/json/priorities.json"));
        ResponseList<Status> statuses = factory.createStatusList(getJSONString("/json/statuses.json"));
        Mockito.when(projectClient.getProject(1)).thenReturn(project);
        Mockito.when(issueClient.getIssue(1)).thenReturn(issue);
        Mockito.when(issueClient.getIssueTypes(1)).thenReturn(issueTypes);
        Mockito.when(issueClient.getPriorities()).thenReturn(priorities);
        Mockito.when(issueClient.getStatuses()).thenReturn(statuses);

        // exercise
        ResultActions actual = mockMvc.perform(get("/project/1/issue/1"));

        // verify
        actual.andExpect(status().isOk())
                .andExpect(view().name("issue_detail"))
                .andExpect(model().attribute("project", project))
                .andExpect(model().attribute("issue", issue))
                .andExpect(model().attribute("issueTypes", issueTypes))
                .andExpect(model().attribute("priorities", priorities))
                .andExpect(model().attribute("statuses", statuses));
    }

    @Test
    public void updateIssue_summaryが存在する() throws Exception {
        // setup
        String summary = "Test Summary";

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue/1").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1"))
                .andExpect(view().name("redirect:/project/1"))
                .andExpect(flash().attribute("message", nullValue()));
    }

    @Test
    public void updateIssue_summaryが空() throws Exception {
        // setup
        String summary = "";

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue/1").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1/issue/1"))
                .andExpect(view().name("redirect:/project/1/issue/1"))
                .andExpect(flash().attribute("message", "summary is required."));
    }

    @Test
    public void updateIssue_summaryがnull() throws Exception {
        // setup
        String summary = null;

        // exercise
        ResultActions actual = mockMvc.perform(post("/project/1/issue/1").param("summary", summary));

        // verify
        actual.andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/1/issue/1"))
                .andExpect(view().name("redirect:/project/1/issue/1"))
                .andExpect(flash().attribute("message", "summary is required."));
    }

    private String getJSONString(String fileName) throws IOException {
        String fileUrl = BacklogControllerTest.class.getResource(fileName).getFile();
        String path = new File(fileUrl).getAbsolutePath();

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes);
    }

}
