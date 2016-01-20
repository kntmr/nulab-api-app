package com.github.kntmr.nulab_api_app.http;

import com.nulabinc.backlog4j.*;
import com.nulabinc.backlog4j.api.option.CreateIssueParams;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.api.option.UpdateIssueParams;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

@Component
public class IssueClient extends BaseClient {

    /**
     * プロジェクトに紐付く課題一覧を取得する
     *
     * @param projectId
     * @return
     * @throws MalformedURLException
     */
    public List<Issue> getIssues(long projectId) throws MalformedURLException {
        GetIssuesParams params = new GetIssuesParams(Arrays.asList(projectId));

        BacklogClient client = super.getBacklogClient();
        return client.getIssues(params);
    }

    /**
     * 課題IDに対応する課題を取得する
     *
     * @param issueId
     * @return
     * @throws MalformedURLException
     */
    public Issue getIssue(long issueId) throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getIssue(issueId);
    }

    /**
     * 課題を追加する
     *
     * @param issueForm
     * @return
     * @throws MalformedURLException
     */
    public Issue addIssue(IssueForm issueForm) throws MalformedURLException {
        CreateIssueParams params = new CreateIssueParams(
                issueForm.getProjectId(),
                issueForm.getSummary(),
                issueForm.getIssueTypeId(),
                Issue.PriorityType.valueOf(issueForm.getPriorityId()));

        BacklogClient client = super.getBacklogClient();
        return client.createIssue(params);
    }

    /**
     * 課題を更新する
     * statusは更新された場合のみ保存する
     *
     * @param issueId
     * @param issueForm
     * @return
     * @throws MalformedURLException
     */
    public Issue updateIssue(long issueId, IssueForm issueForm) throws MalformedURLException {
        UpdateIssueParams params = new UpdateIssueParams(issueId)
                .summary(issueForm.getSummary())
                .description(issueForm.getDescription())
                .issueTypeId(issueForm.getIssueTypeId())
                .priority(Issue.PriorityType.valueOf(issueForm.getPriorityId()));

        Issue.StatusType after = Issue.StatusType.valueOf(issueForm.getStatusId());

        Issue.StatusType before = getIssue(issueId).getStatus().getStatusType();
        if (before.getIntValue() < after.getIntValue()) {
            params.status(after);
        }

        BacklogClient client = super.getBacklogClient();
        return client.updateIssue(params);
    }

    /**
     * プロジェクトIDに紐付く課題の種別一覧を取得する
     *
     * @param projectId
     * @return
     * @throws MalformedURLException
     */
    public List<IssueType> getIssueTypes(long projectId) throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getIssueTypes(projectId);
    }

    /**
     * 優先度一覧を取得する
     *
     * @return
     * @throws MalformedURLException
     */
    public List<Priority> getPriorities() throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getPriorities();
    }

    /**
     * 状態一覧を取得する
     *
     * @return
     * @throws MalformedURLException
     */
    public List<Status> getStatuses() throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getStatuses();
    }

}
