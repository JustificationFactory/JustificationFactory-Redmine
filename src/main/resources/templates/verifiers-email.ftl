<p>Hello ${user.initials},</p>

<p>You are involved in these issues on the Redmine Wiki of the project ${projectName}:</p>

<ul>
<#list issues as issue>
  <li><a href="${redmineUrl}/projects/${projectName}/wiki/${issue.page.title}">${issue.page.title}</a>: ${(issue.topic?capitalize)?replace("_", " ")}</li>
</#list>
</ul>

<p>Please, fix these issues.</p>
