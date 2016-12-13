window.onload = function () {

    console.log("create diagram");

    var chart = c3.generate({
        bindto: '#barDiagram',
        data: {
            x: 'x',
            //xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
            columns: [
                ['x', '2013-01-01', '2013-02-02', '2013-03-03', '2013-04-04', '2013-05-05', '2013-06-06', '2013-07-01', '2013-08-01', '2013-09-01', '2013-10-01', '2013-11-01', '2013-12-01'],
              //  ['x', '201301', '201302', '201303', '201304', '201305', '201306'],
                ['BicycleTheft', 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250],
                ['Burglary', 130, 100, 100, 200, 150, 50, 30, 200, 200, 400, 150, 250],
                ['Other theft', 230, 200, 200, 300, 250, 250, 30, 200, 200, 400, 150, 250]
            ],
            type: 'bar',
            groups: [
                ['BicycleTheft', 'Burglary', 'Other theft']
            ]
        },
        grid: {
            y: {
                lines: [{value:0}]
            }
        },
        axis: {
            x: {
                type: 'timeseries',
                tick: {
                    format: '%Y-%m'
                }
            },
            y: {
                label: { // ADD
                    text: 'Number of crimes',
                    position: 'outer-middle'
                }
            }
        },
        size: {
            height: 480
        }
    });

};