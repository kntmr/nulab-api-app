package com.github.kntmr.nulab_api_app.controller;

import com.github.kntmr.nulab_api_app.http.IssueClient;
import com.github.kntmr.nulab_api_app.http.IssueForm;
import com.github.kntmr.nulab_api_app.http.ProjectClient;
import com.nulabinc.backlog4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping
public class BacklogController {

    @Autowired
    private ProjectClient projectClient;
    @Autowired
    private IssueClient issueClient;

    /**
     * プロジェクト一覧を表示する
     *
     * @param model
     * @return
     * @throws MalformedURLException
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getProjects(Model model) throws MalformedURLException {
        List<Project> projects = projectClient.getProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }

    /**
     * 課題一覧を表示する
     *
     * @param projectId
     * @param issueForm
     * @param model
     * @return
     * @throws MalformedURLException
     */
    @RequestMapping(path = "/project/{projectId}", method = RequestMethod.GET)
    public String getIssues(@PathVariable long projectId,
                            IssueForm issueForm, Model model) throws MalformedURLException {
        Project project = projectClient.getProject(projectId);

        List<Issue> issues = issueClient.getIssues(projectId);
        List<IssueType> issueTypes = issueClient.getIssueTypes(projectId);
        List<Priority> priorities = issueClient.getPriorities();

        model.addAttribute("project", project);
        model.addAttribute("issues", issues);
        model.addAttribute("issueTypes", issueTypes);
        model.addAttribute("priorities", priorities);

        return "issues";
    }

    /**
     * 課題を追加する
     * summary未入力の場合はエラーメッセージを設定する
     *
     * @param projectId
     * @param issueForm
     * @param result
     * @param redirectAttributes
     * @return
     * @throws MalformedURLException
     */
    @RequestMapping(path = "/project/{projectId}/issue", method = RequestMethod.POST)
    public String addIssue(@PathVariable long projectId, @Validated IssueForm issueForm,
                           BindingResult result, RedirectAttributes redirectAttributes) throws MalformedURLException {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "summary is required.");
        } else {
            issueForm.setProjectId(projectId);
            issueClient.addIssue(issueForm);
        }
        return "redirect:/project/" + projectId;
    }

    /**
     * 課題の詳細を表示する
     *
     * @param projectId
     * @param issueId
     * @param issueForm
     * @param model
     * @return
     * @throws MalformedURLException
     */
    @RequestMapping(path = "/project/{projectId}/issue/{issueId}", method = RequestMethod.GET)
    public String getIssue(@PathVariable long projectId, @PathVariable long issueId,
                           IssueForm issueForm, Model model) throws MalformedURLException {
        Project project = projectClient.getProject(projectId);

        Issue issue = issueClient.getIssue(issueId);
        List<IssueType> issueTypes = issueClient.getIssueTypes(projectId);
        List<Priority> priorities = issueClient.getPriorities();
        List<Status> statuses = issueClient.getStatuses();

        model.addAttribute("project", project);
        model.addAttribute("issue", issue);
        model.addAttribute("issueTypes", issueTypes);
        model.addAttribute("priorities", priorities);
        model.addAttribute("statuses", statuses);

        return "issue_detail";
    }

    /**
     * 課題を更新する
     * summary未入力の場合はエラーメッセージを設定して自画面に遷移する
     *
     * @param projectId
     * @param issueId
     * @param issueForm
     * @param result
     * @param redirectAttributes
     * @return
     * @throws MalformedURLException
     */
    @RequestMapping(path = "/project/{projectId}/issue/{issueId}", method = RequestMethod.POST)
    public String updateIssue(@PathVariable long projectId, @PathVariable long issueId, @Validated IssueForm issueForm,
                              BindingResult result, RedirectAttributes redirectAttributes) throws MalformedURLException {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "summary is required.");
            return "redirect:/project/" + projectId + "/issue/" + issueId;
        }

        issueClient.updateIssue(issueId, issueForm);
        return "redirect:/project/" + projectId;
    }

}
