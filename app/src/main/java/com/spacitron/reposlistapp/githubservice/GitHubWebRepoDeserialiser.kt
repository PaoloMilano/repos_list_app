package com.spacitron.reposlistapp.githubservice

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import org.jsoup.Jsoup

class GitHubWebRepoDeserialiser {

    companion object {

        fun map(reposHtmlString: String): List<Repository> {
            return Jsoup.parse(reposHtmlString)
                    .body()
                    .getElementsByClass("repo-list")
                    .select("li")
                    .map {

                        val listItemDivs = it.getElementsByTag("div")
                        val repoPrefix = listItemDivs.elementAt(0).getElementsByTag("span").text().replace(" ", "")
                        val repoName = listItemDivs.elementAt(0).getElementsByTag("a").text().replace(" ", "").replace(repoPrefix, "")

                        val repoDescription = listItemDivs.elementAt(2).text()
                        val repoUrl = "https://github.com/${repoPrefix + repoName}"

//            https@ //api.github.com/search/repositories?q=repo:google/tink

                        val repository = Repository()
                        repository.description = repoDescription
                        repository.name = repoName
                        repository.htmlUrl = repoUrl

                        val gitHubUser = GitHubUser()
                        gitHubUser.name = repoPrefix.replace("/", "");
                        repository.owner = gitHubUser
                        repository
                    }
        }
    }
}