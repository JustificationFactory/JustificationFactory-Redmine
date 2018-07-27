h1. Approbations des pages Redmine : bilan du ${date} pour le projet ${projectName}

h2. État du Wiki

|*"404 Not Found"*|*Sans approbation*|*Structure NOK*|*Contenu NOK*|*Contenu OK*|*Total*|
|${notFoundPages}|${withoutApprovalPages}|${nokStructurePages}|${nokContentPages}|${okContentPages}|${totalPages}|

h2. Classement des utilisateurs

|*Initiales*|*Nombre de documents non signés*|*Nombre de dates mal formatées*|*Nombre de pages bien signées*|
<#list ranking.orderedUsersWithLessFailures as contribution>
|${contribution.user.initials}|${contribution.numberOfUnsigned}|${contribution.numberOfWrongDates}|${contribution.numberOfWellSigned}|
</#list>
