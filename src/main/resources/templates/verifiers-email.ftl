<p>Hello ${user.initials},</p>

<p>You are involved in these issues on the Redmine Wiki of the project ${projectName}:</p>

<#if warnings?has_content>
<h1>Warnings</h1>
<ul>
<#list warnings as issue>
    <li><a href="${redmineUrl}/projects/${projectName}/wiki/${issue.page.title}">${issue.page.title}</a>: ${(issue.type?capitalize)?replace("_", " ")}</li>
</#list>
</ul>
</#if>

<#if errors?has_content>
<h1>Errors</h1>
<ul>
<#list errors as issue>
  <li><a href="${redmineUrl}/projects/${projectName}/wiki/${issue.page.title}">${issue.page.title}</a>: ${(issue.type?capitalize)?replace("_", " ")}</li>
</#list>
</ul>
</#if>

<p>Please, fix these issues.</p>
