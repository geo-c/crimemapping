/**
 * This file contains javascript code concerning the diagram page.
 */

var selectedYear = "";
var selectedCrimeType = [];
var responseAll = []; // data contained in this array will be displayed in the diagram
var pos;

/* ============ Dropdown/Selects ==============*/
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

/**
 *  Invoked when submit button is pressed
 *
 *  Get the selected values for borough, crime types and years
 *  If the values are valid call functions to build diagram and table
 */
function changeSession() {
	document.getElementById("crimeTypeCheckboxes").style.display = "none";
	countUsageDiag();

	// get name of the selected borough
    var boroughName = document.getElementById('selectedBorough').value;

    //make the crimetype array and response array empty
    selectedCrimeType = [];
    pos = 0;
    responseAll.length = 0;
    responseAll=[0,0,0,0,0,0,0,0,0,0,0,0,0];
    //console.log(responseAll);

    // get the selected crime types and store them in an array
    var inputElements = document.getElementsByClassName('crimeVals');
    for(var i=0; inputElements[i]; ++i){
        if(inputElements[i].checked){
            selectedCrimeType.push(inputElements[i].value);
        }
    }

    // get selected year(s)
    var year = document.getElementById('selectedYear').value;

    // Ensure that the selection is valid
    if (boroughName == 'Select a borough') {
        document.getElementById("barDiagram").textContent = 'Please select a borough.';
        document.getElementById("barDiagram").style.color = 'Red';
    }else if(selectedCrimeType.length==0){
        document.getElementById("barDiagram").textContent = 'Please select atleast one crime type.';
        document.getElementById("barDiagram").style.color = 'Red';
    } else {

		//initialise and show the progress bar
        document.getElementById("pBar").max = selectedCrimeType.length*3;
		document.getElementById("pBar").value = "0";
		$("#loaderDiagram").show(1);
		$("#ProgressBar").show(1);
        document.getElementById("barDiagram").style.paddingLeft = '25px';
        document.getElementById("barDiagram").textContent = 'Please wait, diagram is loading....';
        document.getElementById("barDiagram").style.color = 'Green';
        selectedYear = year;

        // call functions to build diagrmm, data table and wikiLink
        buildDiagramAll(boroughName,year);
        askForTableData(buildTableQuery(boroughName));
        putWikiLink(boroughName);
    }
}


/* ================ Diagram query ==============*/
// vars for parliament query
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";

// Libraries
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

// Build SPARQL query for receiving crime data for the selected borough, crime types and year
function buildDiagramAll(boroughName, year){
    for(var i = 0; i < selectedCrimeType.length; i++) {
	//increment the progress bar
        document.getElementById("pBar").value = document.getElementById("pBar").value + 1;
        if ( year == "2013" || year == "2014") {
            var query = sqlPrefixes + '\
            SELECT ?m ?ct COUNT(?crime)\n\
            WHERE{ GRAPH<http://course.geoinfo2016.org/G3>{ \n\
                    ?crime lode:atTime ?t.\n\
                    ?t time:month ?m. \n\
                    ?t time:year "' + year + '"^^xsd:gYear. \n\
                    ?crime rdf:type ?ct. \n\
                    ?crime lode:atPlace dbpedia-page:' + boroughName + '. \n\
            }FILTER(?ct!=crime:Crime&&?ct!=rdfs:Class&&?ct=crime:' + selectedCrimeType[i] + ') \n\
            }GROUP BY ?m ?ct \n\
			ORDER BY ?m\n';
        } else {
            var query = sqlPrefixes + '\
            SELECT ?m ?ct ?y COUNT(?crime)\n\
            WHERE{ GRAPH<http://course.geoinfo2016.org/G3>{ \n\
                    ?crime lode:atTime ?t.\n\
                    ?t time:month ?m. \n\
                    ?t time:year ?y. \n\
                    ?crime rdf:type ?ct. \n\
                    ?crime lode:atPlace dbpedia-page:' + boroughName + '. \n\
            }FILTER(?ct!=crime:Crime&&?ct!=rdfs:Class&&?ct=crime:' + selectedCrimeType[i] + ') \n\
            }GROUP BY ?m ?y ?ct \n\
                ORDER BY ?m ?y\n';
        }
        askForDiagramData(query);
    }
}

// Send request containing the SPARQL query to parliament
function askForDiagramData(query) {
    var url = sparqlUrl + encodeURIComponent(query); // encodeURI is not enough as it doesn't enocde # for example.
    $.ajax({
        dataType: "jsonp",
        url: url,
        /*Success*/
        success: function(data){
            //console.log(data);
	    //increment the progress bar
	    document.getElementById("pBar").value = document.getElementById("pBar").value + 1;
            document.getElementById("barDiagram").style.removeProperty('padding-left');
            document.getElementById("barDiagram").style.removeProperty('color');

            // call function to generate the diagram using the received data
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

/* ========= Parse received data and generate the diagram =========== */
function generateDiagram(data) {

    // generating a string containing the dates that will be displayed on the x-axis of the diagram
    var yearArray;
    if (selectedYear == "2013") {
        yearArray = ['x', '2013-01-01', '2013-02-02', '2013-03-03', '2013-04-04', '2013-05-05', '2013-06-06', '2013-07-01', '2013-08-01', '2013-09-01', '2013-10-01', '2013-11-01', '2013-12-01'];
    } else if (selectedYear == "2014") {
        yearArray = ['x', '2014-01-01', '2014-02-02', '2014-03-03', '2014-04-04', '2014-05-05', '2014-06-06', '2014-07-01', '2014-08-01', '2014-09-01', '2014-10-01', '2014-11-01', '2014-12-01'];
    } else {
        yearArray = ['x', '2013-01-01', '2013-02-02', '2013-03-03', '2013-04-04', '2013-05-05', '2013-06-06', '2013-07-01', '2013-08-01', '2013-09-01', '2013-10-01', '2013-11-01', '2013-12-01', '2014-01-01', '2014-02-02', '2014-03-03', '2014-04-04', '2014-05-05', '2014-06-06', '2014-07-01', '2014-08-01', '2014-09-01', '2014-10-01', '2014-11-01', '2014-12-01'];
    }

    // Parse the data and write them into dataArray
    // one year selected
    if( selectedYear == "2013" || selectedYear == "2014") {
        var dataArray = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]; // to be used to generate the diagram
        dataArray[0] = data.results.bindings[0].ct.value;
        dataArray[0] = dataArray[0].substr(dataArray[0].lastIndexOf('/') + 1);
        for (var key = 0; key < data.results.bindings.length; key++) {
            var number = data.results.bindings[key][".1"].value;
            var month = data.results.bindings[key]["m"].value;
            month = month.slice(2, 4); // delete "--" at the beginning of the string
            if (month.slice(0, 1) == "0") { // delete "0" at the beginning if needed
                month = month.slice(1);
            }
            dataArray[month] = number; // add the number of crimes for the specific month in the array
        }
    // two years selected
    } else {
        var dataArray = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]; // to be used to generate the diagram
        dataArray[0] = data.results.bindings[0].ct.value;
        dataArray[0] = dataArray[0].substr(dataArray[0].lastIndexOf('/') + 1);
        for (var key = 0; key < data.results.bindings.length; key++) {
            var number = data.results.bindings[key][".1"].value;
            var month = data.results.bindings[key]["m"].value;
            var year = data.results.bindings[key]["y"].value;
            month = month.slice(2, 4); // delete "--" at the beginning of the string
            if (month.slice(0, 1) == "0") { // delete "0" at the beginning if needed
                month = month.slice(1);
            }
            if (year == "2013")
                dataArray[month] = number; // add the number of crimes for the specific month in the array
            if (year == "2014")
                dataArray[11 + parseInt(month)] = number;
        }
    }

    // insert data into responseAll array (containing the data that is displayed in the diagram)
    responseAll[pos]=dataArray;
    pos = pos + 1;


    /**** generate c3 bar Diagram ***/
    var chart = c3.generate({
        bindto: '#barDiagram',
        data: {
            x: 'x',
            columns: [
                yearArray,
                responseAll[0],
                responseAll[1],
                responseAll[2],
                responseAll[3],
                responseAll[4],
                responseAll[5],
                responseAll[6],
                responseAll[7],
                responseAll[8],
                responseAll[9],
                responseAll[10],
                responseAll[11],
                responseAll[12]
            ],
            type: 'bar',
            groups: [
                ['BicycleTheft','Burglary','TheftFromThePerson','Anti-socialBehaviour','PublicOrder','Shoplifting','Drugs','VehicleCrime','ViolenceAndSexualOffences','CriminalDamageAndArson','PossessionOfWeapons','Robbery','OtherTheft','OtherCrime']
            ]
        },
        axis: {
            x: {
                type: 'timeseries',
                tick: {
                    format: '%Y-%m'
                }
            },
            y: {
                label: {
                    text: 'Number of crimes',
                    position: 'outer-middle'
                }
            }
        },
        size: {
            height: 400
        },
        // change tooltip to show the total number of crimes (crimes of all crime types) instead of date
        tooltip: {
            contents: function (d, defaultTitleFormat, defaultValueFormat, color) {
                var sum = 0;
                d.forEach(function (e) {
                    sum += e.value
                });
                defaultTitleFormat = function () {
                    return sum
                };
                return c3.chart.internal.fn.getTooltipContent.apply(this, arguments);
            }
        }
    });

	//increment the progress bar
	document.getElementById("pBar").value = document.getElementById("pBar").value + 1;

    //hide loader icon and progress bar once the diagram is completely loaded
    if(responseAll[selectedCrimeType.length-1]!=0){
        $("#loaderDiagram").hide(1);
		$("#ProgressBar").hide(1);
        console.log(responseAll);
    }
}


/* ========= Table below the diagram and Wiki-link =========*/

// build SPARQL query to recive income and population data for the selected borough
function buildTableQuery(boroughName, crimeType, year){
    var query = sqlPrefixes + '\
    SELECT ?y ?iVal ?pVal\n\
    WHERE{ GRAPH<http://course.geoinfo2016.org/G3>{ \n\
            dbpedia-page:'+boroughName+' dbpedia:income ?income. \n\
			?income owl:hasValue ?iVal. \n\
			?income dc:date ?y. \n\
			dbpedia-page:'+boroughName+' dbpedia:Population ?popu. \n\
			?popu owl:hasValue ?pVal. \n\
			?popu dc:date ?y. \n\
    }\n\
    }';
    // console.log(query);
    return query;
}

// send request to parliament to receive data that will be displayed in the table
function askForTableData(query) {
    var url = sparqlUrl + encodeURIComponent(query); // encodeURI is not enough as it doesn't enocde # for example.
    //console.log(url);
    console.log('start request');
    $.ajax({
        dataType: "jsonp",
        url: url,
        /*Success*/
        success: function(data){
            console.log(data);
            // call function to generate table with the received data
            createAndFillTable(data);
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

// Parse received data and generate the table
function createAndFillTable(data) {
    var result ="<br><center><table class='BoroughInfoTable'><tr><th>Year</th><th>Income</th><th>Population</th></tr>";
    for (var key = 0; key < data.results.bindings.length-1; key++) {	//-1 to skip the last row (values of 2012)
        var year = data.results.bindings[key]["y"].value;
        var iVal = data.results.bindings[key]["iVal"].value;
        var pVal = data.results.bindings[key]["pVal"].value;
        result = result + "<tr><td>"+year+"</td><td>"+iVal+"</td><td>"+pVal+"</td></tr>";
    }
    result = result + "</table></center>";
    document.getElementById('TableContainer').innerHTML=result;
}

// add Wiki-link for the selected borough
function putWikiLink(boroughName){
    var boroughNameNew = boroughName.replace(/_/g, ' ');
    var wikiURL = "<br><center><a id='wikilink' href='https://en.wikipedia.org/wiki/"+boroughName+"' target='_blank'>Wikipedia page of "+boroughNameNew+"</a></center>";
    document.getElementById('WikiLinkHolder').innerHTML=wikiURL;
}

// show/hide the crimetype checkboxes list
var expanded = false;
function showCheckboxes() {
    var checkboxes = document.getElementById("crimeTypeCheckboxes");
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }
}
