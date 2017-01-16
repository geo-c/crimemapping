var selectedBorough = "";

/* ============ Dropdown ==============*/
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Called when a change is detected in one of the dropdowns/select
function changeSession() {
    var boroughName = document.getElementById('selectedBorough').value;
    var crimeType = document.getElementById('selectedCrimeType').value;
    var year = document.getElementById('selectedYear').value;
    // Ensure that a borough name has been selected
    if (boroughName == 'Select a borough') {
        document.getElementById("barDiagram").textContent = 'Please select a borough.';
        document.getElementById("barDiagram").style.color = 'Red';
    } else {
        document.getElementById("barDiagram").style.paddingLeft = '25px';
        document.getElementById("barDiagram").textContent = 'Please wait, diagram is loading.';
        document.getElementById("barDiagram").style.color = 'Green';
        askForDiagramData(buildDiagramQuery(boroughName, crimeType, year));
    }
}


/* ================ Diagram query ==============*/
/*vars for parliament query*/
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";

/*Libraries*/
var sqlPrefixes = "\
PREFIX crime: <http://course.geoinfo2016.org/G3/>\n\
PREFIX dbpedia-page: <http://dbpedia.org/page/>\n\
PREFIX dbpedia: <http://dbpedia.org/ontology/>\n\
PREFIX time: <http://www.w3.org/2006/time#>\n\
PREFIX owl: <https://www.w3.org/2002/07/owl#>\n\
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\
PREFIX lode: <http://linkedevents.org/ontology/>\n\
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n\
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n\
PREFIX gpowl: <http://aims.fao.org/aos/geopolitical.owl#>\n\
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n\
PREFIX admingeo: <http://data.ordnancesurvey.co.uk/ontology/admingeo/>\n\
PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n\
PREFIX dc: <http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=elements#>\n";

/*function buildDiagramQuery(boroughName, year, crimeType){
    var query = sqlPrefixes + '\
    SELECT ?m ?y ?ct COUNT(?crime)\n\
    WHERE{ GRAPH<http://course.geoinfo2016.org/G3>{ \n\
        ?crime lode:atTime ?t.\n\
            ?t time:month ?m. \n\
            ?t time:year "2013"^^xsd:gYear. \n\
            ?crime rdf:type ?ct. \n\
            ?crime lode:atPlace dbpedia-page:'+boroughName+'. \n\
    }FILTER(?ct!=crime:Crime&&?ct!=rdfs:Class) \n\
    }GROUP BY ?m ?y ?ct \n\
        ORDER BY ?m ?y \n';
    console.log(query);
    return query;
}*/

// builds a string containing the query which will be send to parliament
function buildDiagramQuery(boroughName, crimeType, year){
    var query = sqlPrefixes + '\
    SELECT ?m ?y ?ct COUNT(?crime)\n\
    WHERE{ GRAPH<http://course.geoinfo2016.org/G3>{ \n\
        ?crime lode:atTime ?t.\n\
            ?t time:month ?m. \n\
            ?t time:year "'+year+'"^^xsd:gYear. \n\
            ?crime rdf:type crime:'+crimeType+'. \n\
            ?crime lode:atPlace dbpedia-page:'+boroughName+'. \n\
    }\n\
    }GROUP BY ?m ?y \n\
        ORDER BY ?m ?y \n';
    console.log(query);
    return query;
}

// send request to parliament
function askForDiagramData(query) {
    var url = sparqlUrl + encodeURIComponent(query); // encodeURI is not enough as it doesn't enocde # for example.
    console.log(url);
    $.ajax({
        dataType: "jsonp",
        url: url,
        /*Success*/
        success: function(data){
            console.log(data);
            document.getElementById("barDiagram").style.removeProperty('padding-left');
            document.getElementById("barDiagram").style.removeProperty('color');
            generateDiagram(data);
        },
        /*error*/
        error: function (ajaxContext) {
            console.log("error"+ ajaxContext);
            alert(ajaxContext.responseText);
        }
    }).done(function(JSONtext) {
        console.log("done")
    });
}

// Parse received data and generate the diagram
function generateDiagram(data) {
    // parse data
    // todo

    // generate c3 bar Diagram
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
                lines: [{value: 0}]
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
}