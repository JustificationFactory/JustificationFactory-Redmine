<p>Hello ${user.initials},</p>

<#if warnings?has_content || errors?has_content>
<h1>Your issues</h1>

<p>You are involved in these issues on the Redmine Wiki of the project ${projectName}:</p>

    <#if warnings?has_content>
<h2>Warnings</h2>

<table style="width: 100%; border: 1px solid black; border-collapse: collapse;">
    <tr style="border: 1px solid black;">
        <th style="padding: 5px; border: 1px solid black;">Wiki page</th>
        <th style="padding: 5px; border: 1px solid black;">Issue</th>
    </tr>

    <#list warnings as issue>
    <tr style="border: 1px solid black;">
        <td style="padding: 5px; border: 1px solid black;">
            <a href="${redmineUrl}/projects/${projectName}/wiki/${issue.approvalDocument.source.associatedPage.title}">${issue.approvalDocument.source.associatedPage.title}</a>
        </td>
        <td style="padding: 5px; border: 1px solid black;">${(issue.issueType?capitalize)?replace("_", " ")}</td>
    </tr>
    </#list>
</table>
    </#if>

    <#if errors?has_content>
<h2>Errors</h2>

<table style="width: 100%; border: 1px solid black; border-collapse: collapse;">
    <tr style="border: 1px solid black;">
        <th width="50%" style="padding: 5px; border: 1px solid black;">Wiki page</th>
        <th width="50%" style="padding: 5px; border: 1px solid black;">Issue</th>
    </tr>

    <#list errors as issue>
    <tr style="border: 1px solid black;">
        <td style="padding: 5px; border: 1px solid black;">
            <a href="${redmineUrl}/projects/${projectName}/wiki/${issue.approvalDocument.source.associatedPage.title}">${issue.approvalDocument.source.associatedPage.title}</a>
        </td>
        <td style="padding: 5px; border: 1px solid black;">${(issue.issueType?capitalize)?replace("_", " ")}</td>
    </tr>
    </#list>
</table>
    </#if>

<p>Please, fix these issues.</p>
</#if>

<#if validated?has_content>
<h1>Verified documents, need to be locked</h1>

<p>These justification documents are now validated and await to be locked.</p>

<ul>
    <#list validated as validatedDocument>
        <li>
            <a href="${redmineUrl}/projects/${projectName}/wiki/${validatedDocument.associatedPage.title}">${validatedDocument.associatedPage.title}</a>
        </li>
    </#list>
</ul>
</#if>

<#if others?has_content>
<h2>Issues of unknown users</h2>
<p>These issues do not have an associated user or the associated user is not known.</p>

<table style="width: 100%; border: 1px solid black; border-collapse: collapse;">
    <tr style="border: 1px solid black;">
        <th style="padding: 5px; border: 1px solid black;">Wiki page</th>
        <th style="padding: 5px; border: 1px solid black;">Issue</th>
    </tr>

    <#list others as issue>
    <tr style="border: 1px solid black;">
        <td style="padding: 5px; border: 1px solid black;">
            <a href="${redmineUrl}/projects/${projectName}/wiki/${issue.approvalDocument.source.associatedPage.title}">${issue.approvalDocument.source.associatedPage.title}</a>
        </td>
        <td style="padding: 5px; border: 1px solid black;">${(issue.issueType?capitalize)?replace("_", " ")}</td>
    </tr>
    </#list>
</table>
</#if>
