function showDiagram (boroughName) {

    console.log(boroughName);

    // todo: query to parliament/OCT depending on selected borough name
    // todo: parse the received json

    // show selected borough name on button
    document.getElementById("dropbtn").textContent=boroughName;

    var chart = c3.generate({
        bindto: '#barDiagram',
        data: {
            x: 'x',
            columns: [
                ['x', '2013-01-01', '2013-02-02', '2013-03-03', '2013-04-04', '2013-05-05', '2013-06-06', '2013-07-01', '2013-08-01', '2013-09-01', '2013-10-01', '2013-11-01', '2013-12-01', '2014-01-01', '2014-02-02', '2014-03-03', '2014-04-04', '2014-05-05', '2014-06-06', '2014-07-01', '2014-08-01', '2014-09-01', '2014-10-01', '2014-11-01', '2014-12-01'],
                ['BicycleTheft', 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250],
                ['Burglary', 130, 100, 100, 200, 150, 50, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250],
                ['Other theft', 230, 200, 200, 300, 250, 250, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250, 30, 200, 200, 400, 150, 250]
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
            // todo: fixed height for y-axis??
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

/* ============ Dropdown ==============*/
/* Dropdown Button */
/* When the user clicks on the button,
 toggle between hiding and showing the dropdown content */
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown menu if the user clicks outside of it
window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {

        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
};