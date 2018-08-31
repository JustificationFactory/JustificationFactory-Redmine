|{background:lightblue}.*Phase projet*|{background:lightblue}.*Type*|{background:lightblue}.*Référence*|{background:lightblue}.*Lien*|{background:lightblue}.*Auteur*|{background:lightblue}.*Date*|
|{background:lightgrey}.*INITIALISATION*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|
<#list initializationDocuments as document>|Initialisation|${document.documentType}|${document.reference}|[[${projectName}:${document.reference}]]|${document.author}|${document.releaseDate?date("yyyy-MM-dd")?string["dd/MM/yy"]}|
</#list>
|{background:lightgrey}.*DONNÉES D'ENTRÉE*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|
<#list entryDataDocuments as document>|Données d'entrée|${document.documentType}|${document.reference}|[[${projectName}:${document.reference}]]|${document.author}|${document.releaseDate?date("yyyy-MM-dd")?string["dd/MM/yy"]}|
</#list>
|{background:lightgrey}.*FAISABILITÉ*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|
<#list feasibilityDocuments as document>|Faisabilité|${document.documentType}|${document.reference}|[[${projectName}:${document.reference}]]|${document.author}|${document.releaseDate?date("yyyy-MM-dd")?string["dd/MM/yy"]}|
</#list>
|{background:lightgrey}.*DÉVELOPPEMENT*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|
<#list developmentDocuments as document>|Développement|${document.documentType}|${document.reference}|[[${projectName}:${document.reference}]]|${document.author}|${document.releaseDate?date("yyyy-MM-dd")?string["dd/MM/yy"]}|
</#list>