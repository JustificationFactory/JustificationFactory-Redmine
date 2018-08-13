<!DOCTYPE html>
<html>
    <head>
        <title>Redmine Ranking</title>

        <!--Import Google Icon Font-->
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <!--Import materialize.css-->
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>

        <!--Let browser know website is optimized for mobile-->
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta charset="UTF-8">
    </head>

    <body>
        <div class="chart-container" style="margin: 0 auto; width:80vw;">
            <canvas id="myChart"></canvas>
        </div>

        <div style="margin: 0 auto; width:80vw; text-align: center;">
            <p class="flow-text"><b>${approvalsInError}</b>/<b>${wikiPagesWithApproval}</b> pages en erreur.</p>
        </div>

        <script>
            if (${approvalsInError} * 100 / ${wikiPagesWithApproval} > 20) {
                document.body.style.backgroundColor = 'rgb(252, 232, 232)'
            }

            const users = [<#list performances as p>['${p.user.initials}', ${p.numberOfTotalContributions}, ${p.numberOfFaults}]<#sep>, </#list>];

            var names = [];
            var contributions = [];
            var errorsInApproval = [];
            for (i in users) {
                names.push(users[i][0]);
                contributions.push(users[i][1]);
                errorsInApproval.push(users[i][2]);
            }
        </script>

        <script type="text/javascript" src="js/Chart.bundle.min.js"></script>
        <script>
            var ctx = document.getElementById("myChart").getContext('2d');

            var myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: names,

                    datasets: [{
                        label: 'Nombre de contributions à des pages Wiki',
                        data: contributions,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    },
                        {
                            label: 'Nombre d\'erreurs dans l\'encadré d\'approbation',
                            data: errorsInApproval,
                            backgroundColor: 'rgba(255, 99, 132, 0.2)',
                            borderColor: 'rgba(255,99,132,1)',
                            borderWidth: 1
                        }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero:true
                            }
                        }]
                    }
                }
            });
        </script>

        <!--JavaScript at end of body for optimized loading-->
        <script type="text/javascript" src="js/materialize.min.js"></script>
    </body>
</html>