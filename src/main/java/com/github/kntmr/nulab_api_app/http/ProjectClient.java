package com.github.kntmr.nulab_api_app.http;

import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.Project;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.List;

@Component
public class ProjectClient extends BaseClient {

    /**
     * プロジェクト一覧を取得する
     *
     * @return
     * @throws MalformedURLException
     */
    public List<Project> getProjects() throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getProjects();
    }

    /**
     * プロジェクトIDに対応するプロジェクトを取得する
     *
     * @param projectId
     * @return
     * @throws MalformedURLException
     */
    public Project getProject(long projectId) throws MalformedURLException {
        BacklogClient client = super.getBacklogClient();
        return client.getProject(projectId);
    }

}
