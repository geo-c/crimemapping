/*vars for heatmap*/
var CrimeLatLon = [];
var CrimeHeat = [];
/*vars for boroughs layer*/
var crimeIndexRateMap = new Object();
var highestRate = 0;
var lowestRate = 0;
/*Heatmap layer*/
var heat = new L.LayerGroup();
/*variable where the requestes JSON (data) will be stored in*/
var JSONtext;
/*vars for parliament query*/
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";

/*vocabulary/prefixes*/
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
PREFIX dc: <http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=elements#>\n\n";




/**
Sliders only
***/

var rangeMonths = {
    "1": "January",
        "2": "February",
        "3": "March",
        "4": "April",
		"5": "May",
		"6": "June",
		"7": "July",
		"8": "August",
		"9": "September",
		"10": "October",
		"11": "November",
		"12": "December"
};

var rangeYears = {
    "2013": "2013",
        "2014": "2014",
		"2015": "2015"
};

$(function () {

    $('#rangeTextMonth').text(rangeMonths[$('#rangeInputMonth').val()]);

    $('#rangeInputMonth').on('input change', function () {
        $('#rangeTextMonth').text(rangeMonths[$(this).val()]);
    });

});

$(function () {

    $('#rangeTextYear').text(rangeYears[$('#rangeInputYear').val()]);

    $('#rangeInputYear').on('input change', function () {
        $('#rangeTextYear').text(rangeYears[$(this).val()]);
    });

});


/**
next is for heatmap ONLY
**/

/*Function to build the SPARQL-query for the heatmap*/
function buildCrimeLocQuery(){
	var month = document.getElementById('rangeInputMonth').value < 10 ? "0"+document.getElementById('rangeInputMonth').value : document.getElementById('rangeInputMonth').value
	var query = sqlPrefixes + "\
		SELECT ?crime ?lat ?lon\n\
		WHERE { GRAPH <http://course.geoinfo2016.org/G3> {\n\
		?crime geo:lat ?lat.\n\
		?crime geo:long ?lon.\n\
	?crime lode:atTime ?t.\n\
		?t time:month \"--"+ month + "\"^^xsd:gMonth.\n\
		?t time:year \""+ rangeYears[$('#rangeInputYear').val()]+ "\"^^xsd:gYear.\n\
	}}LIMIT 150000";
	console.log(query)
	return query;
}

/*Function to build the SPARQL-query for the Crime Index Rate*/
function buildCrimeIndexRateQuery(){
	var query = sqlPrefixes + "\
	SELECT ?code ?borough ?incomeVal ?populationVal ?crime_count (((?crime_count/?populationVal)*1000) AS ?crimeIndexRate)\n\
	WHERE {\n\
		GRAPH <http://course.geoinfo2016.org/G3> {\n\
			?borough admingeo:gssCode ?code.\n\
			?borough dbpedia:income ?income.\n\
			?income owl:hasValue ?incomeVal.\n\
			?income dc:date \""+ rangeYears[$('#rangeInputYear').val()]+ "\"^^xsd:gYear.\n\
			?borough dbpedia:Population ?population.\n\
			?population owl:hasValue ?populationVal.\n\
			?population dc:date \""+ rangeYears[$('#rangeInputYear').val()]+ "\"^^xsd:gYear.\n\
			{\n\
				SELECT ?borough (COUNT(?crime) as ?crime_count)\n\
				WHERE\n\
				{\n\
					?crime lode:atPlace ?borough.\n\
					?crime lode:atTime ?t.\n\
					?t time:year \""+ rangeYears[$('#rangeInputYear').val()]+ "\"^^xsd:gYear.\n\
				}GROUP BY ?borough\n\
			}\n\
		}\n\
	}\n\n";
	console.log(query)
	return query;
}

/*function needed to to processing on the receives Crime coordinates*/
function coordinate(x, y) {
    this.x = parseFloat(x);
    this.y = y;
}

/*function needed to to processing on the receives Borough information*/
function boroughObject(code, name, incomeVal, populationVal, crimeCountVal, crimeIndexRateVal) {
    this.boroughCode = code;
	this.boroughDBPediaName = name;
	this.income = incomeVal;
	this.population = populationVal;
	this.crimeCount = crimeCountVal;
	this.crimeIndexRate = crimeIndexRateVal;
}

/*function to create the heatmap layer*/
function createHeatMap(JSONtext){
	CrimeLatLon.length = 0;
	CrimeHeat.length = 0;
	heat.clearLayers();
		for (var key in JSONtext.results.bindings){
			var coords = new coordinate(JSONtext.results.bindings[key].lat.value,JSONtext.results.bindings[key].lon.value)
			CrimeLatLon.push(coords);
			CrimeHeat.push([coords.x, coords.y,0.5])
		}	
		L.heatLayer(CrimeHeat, {radius: 10})
			.addTo(heat);
}

/*Replace all ocurrencies*/
function replaceAll(str, find, replace) {
	return str.replace(new RegExp(find, 'g'), replace);
}

/*function to create the Crime Index Rate layer*/
function createCrimeIndexRateMap(JSONtext){
		crimeIndexRateMap = new Object();
		
		for (var key in JSONtext.results.bindings){
			var boroughCode = JSONtext.results.bindings[key].code.value;
			var boroughDBPediaName = JSONtext.results.bindings[key].borough.value;
			var income = parseFloat(JSONtext.results.bindings[key].incomeVal.value);
			var population = parseInt(JSONtext.results.bindings[key].populationVal.value);
			var crimeCount = parseInt(JSONtext.results.bindings[key].crime_count.value);
			var crimeIndexRate = parseFloat(JSONtext.results.bindings[key].crimeIndexRate.value); //calculating the index rate in parliament is missing the decimals 
			//var crimeIndexRate = (crimeCount / population) * 1000;
			crimeIndexRate = Math.round(crimeIndexRate * 100) / 100
			
			if(highestRate < crimeIndexRate ){
				highestRate = crimeIndexRate;
			}
			if(lowestRate > crimeIndexRate){
				lowestRate = crimeIndexRate;
			}
			
			var borough = new boroughObject(boroughCode, boroughDBPediaName, income, population, crimeCount, crimeIndexRate);
			
			var shortName = replaceAll(borough.boroughDBPediaName,"http://dbpedia.org/page/","");
			shortName = replaceAll(shortName,"London_Borough_of_","");
			shortName = replaceAll(shortName,"Royal_Borough_of_","");
			shortName = replaceAll(shortName,"_"," ");
			
			crimeIndexRateMap[shortName] = borough;
			//console.log("CIR of " + shortName + ": " + crimeIndexRate)
		}	
		
		map.removeControl(legend);
		legend.addTo(map);
		
		boroughLayer = getBoroughtsLayer();
		
		map.addLayer(boroughLayer);
}

/*If user presses Imprint Button with ID reqHeatmap, request is started)*/
document.getElementById('reqHeatmap').onclick = function(){
	console.log("Heat");
	var async = true;
	map.removeLayer(boroughLayer);
	map.removeControl(legend);
	map.addLayer(heat);
    askForData(buildCrimeLocQuery(), createHeatMap, async);
  };

document.getElementById('reqChoropleth').onclick = function(){
	console.log("Choropleth");
	var async = false;
	map.removeLayer(heat);
	map.removeLayer(boroughLayer);
	askForData(buildCrimeIndexRateQuery(), createCrimeIndexRateMap, async);
  };
  
  /**
Functions to build query for choropleth map (Nimrod)
**/

/*Function to build request*/
function buildChoroplethQuery(){
	var query = sqlPrefixes + ""; /*Put your query here,like in buildCrimeLocQuery() */
	console.log(query)
	return query;
}
/**
Function for receiving the data from parliament.
Function askForData needs a query and safes the received data is JSONtext variable.
On  .done   the function calls for other functions needing the JSONtext. Such as the heatmap
**/
  

function askForData(query, processData, asynchronous) {
	var url = sparqlUrl + encodeURIComponent(query); // parse the whole URL containing the query
	console.log(url);
	$("#loader").show(1);
	 $.ajax({
		async: asynchronous,
		dataType: "jsonp",
		url: url,
		/*On Success the data reveived from parialemt is stored in the variable JSONtext*/
		success: function(data){
		console.log(data)
		$("#loader").hide(1);
		var JSONtext = data;
		/*Used to show JSON crime points data and 33 borough results in a new window
		//var url = 'data:text/jsonp;charset=utf8,' + encodeURIComponent(JSON.stringify(data));
		//window.open(url, '_blank');
		//window.focus(); */
		},
		/*On error exceptions will be printed in dialog box*/
		error: function (ajaxContext) {
			$("#loader").hide(1);
		console.log(ajaxContext)
        alert(ajaxContext.responseText)
    }
	/*When request is done (.done) do something with it*/
	}).done(function(JSONtext) {
		console.log("done");	
		processData(JSONtext);
	});
}

  /**
  Leaflet Map properties and functions
  **/



var mbAttr = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
            '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
            'Imagery © <a href="http://mapbox.com">Mapbox</a>',
    mbUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibmdhdmlzaCIsImEiOiJjaXFheHJmc2YwMDdoaHNrcWM4Yjhsa2twIn0.8i1Xxwd1XifUU98dGE9nsQ';

var grayscale   = L.tileLayer(mbUrl, {id: 'mapbox.light', attribution: mbAttr}),
    streets  = L.tileLayer(mbUrl, {id: 'mapbox.streets',   attribution: mbAttr}),
    outdoors = L.tileLayer(mbUrl, {id: 'mapbox.outdoors', attribution: mbAttr}),
    satellite = L.tileLayer(mbUrl, {id: 'mapbox.satellite', attribution: mbAttr}),
    dark = L.tileLayer(mbUrl, {id: 'mapbox.dark', attribution: mbAttr}),
    light = L.tileLayer(mbUrl, {id: 'mapbox.light', attribution: mbAttr}),
    satellitestreets = L.tileLayer(mbUrl, {id: 'mapbox.streets-satellite', attribution: mbAttr})
    ;

var map = L.map('map', {
	zoomControl : true,
	layers: [streets] //default layer on startup of map
}).setView([51.528001, -0.130781], 10); //default zoom and position (london)

/* Load geoJSON from file synchronously (!)*/

function LoadGeoJSON(data) {
	var json = null;
	$.ajax({
		async: false,
		global: false,
		url: data,
		dataType: "json",
		success: function (data) {
			var async = false;	
			json = data;
		}
	});
	return json;
}

/*
Determine color of borough polygon according to crime rate, (C) color brewer 
*/
function getColor(crimeIndexRate) {
    return crimeIndexRate > 782 ? '#b10026' :
           crimeIndexRate > 105  ? '#e31a1c' :
           crimeIndexRate > 90  ? '#fc4e2a' :
           crimeIndexRate > 82  ? '#fd8d3c' :
           crimeIndexRate > 74   ? '#feb24c' :
           crimeIndexRate > 62   ? '#fed976' :
           crimeIndexRate > 52   ? '#ffffb2' :
                      '#FFEDA0';
}

function getColorCrimeRateIndex(crimeIndexRate) {
	
	grades = getChoroplethGrades();
	var colors = getChoroplethColors(grades.length);
	
	for(var i = 0; i < grades.length; i++){
		if((i+1) == grades.length){
			return colors[colors.length - 1];
		}
		if(crimeIndexRate >= grades[i] && crimeIndexRate < grades[i+1]){
			return colors[i];
		}
	}
	
	return "#FFEDA0";
}

/*
Set colors for borough layer
*/
function style(feature) {
    return {
        //fillColor: getColor(feature.properties.crime_rate),
		fillColor: getColorCrimeRateIndex(crimeIndexRateMap[feature.properties.name] ? crimeIndexRateMap[feature.properties.name].crimeIndexRate : 0),
        weight: 2,
        opacity: 1,
        color: 'black',
        dashArray: '0.1',
        fillOpacity: 0.6
    };
}

var boroughs = LoadGeoJSON("data/london_boroughs_crime.geojson"); //Do not move*/

/*
Interactivity functions when hovering
*/
function highlightFeature(e) {
    var layer = e.target;
    info.update(layer.feature.properties);

    layer.setStyle({
        weight: 5,
        color: '#666',
        dashArray: '',
        fillOpacity: 0.7
    });

    if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) {
        layer.bringToFront();
    }
}
function resetHighlight(e) {
    boroughLayer.resetStyle(e.target);
    info.update();
}

function zoomToFeature(e) {
    map.fitBounds(e.target.getBounds());
}

function onEachFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: zoomToFeature
    });
}

boroughLayer = getBoroughtsLayer();

function getBoroughtsLayer(){
	return L.geoJson(boroughs, {
		style: style,
		onEachFeature: onEachFeature
	});
}

/* Control & Legend functions */

var info = L.control();

info.onAdd = function (map) {
    this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
    this.update();
    return this._div;
};

/*Create Legend for choropleth */
var legend = L.control({position: 'bottomright'});

/* legend.onAdd = function (map) {
	var div = L.DomUtil.create('Choropleth', 'Choropleth legend');

	div.innerHTML= '<b>Crime Rate Per 1,000: </b>' +'<br>';
	
	var grades = [0, 55, 65, 75, 85, 95, 110, 785];

	for (var i = 0; i < grades.length; i++) {
		div.innerHTML +=
		'<i style="background:' + getColor(grades[i] + 1) + '"></i> ' +
		grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');
	}
	
    return div;
}; */

function getChoroplethGrades(){
	var amountOfBoroughs = 33;
	var amountOfGrades = 8;
	var grades = [];
	
	var range = parseInt(highestRate / amountOfGrades) + 1;
	for(var i = 0; i < amountOfGrades; i++){
		grades.push(range * i);
	}
	
	return grades;
}
function getChoroplethColors(length){
	/* return d3.scaleThreshold()
		.domain(d3.range(0, length))
		.range(d3.schemeYlOrRd[9]); */
		
	colors = ["#FFEDA0","#ffffb2","#fed976","#feb24c","#fd8d3c","#fc4e2a","#e31a1c","#b10026"];
	
	return colors;
}

legend.onAdd = function () {
	var div = L.DomUtil.create('Choropleth', 'Choropleth legend');

	div.innerHTML= '<b>Crime Rate Per 1,000: </b>' +'<br>';
	
	grades = getChoroplethGrades();
	
	var colors = getChoroplethColors(grades.length);
	
	for(var i = 0; i < grades.length; i++){
		div.innerHTML +=
		'<i style="background:' + colors[i] + '"></i> ' +
		grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');
	}

    return div;
};


// method used to update the control based on feature properties passed
info.update = function (props) {
    this._div.innerHTML = '<h4>Borough Information</h4>' +  (props ?
        '<b>' + props.name + '</b><br />' +
		(crimeIndexRateMap[props.name]? 
			'Year: 2014<br />' +
			'Population: ' + crimeIndexRateMap[props.name].population.toLocaleString() + '<br />' +
			'Avg. Annual Income: ' + crimeIndexRateMap[props.name].income.toLocaleString() + ' &pound;<br />' +
			'Crime Rate Per 1,000 Inhabitants: ' + crimeIndexRateMap[props.name].crimeIndexRate.toLocaleString() + '<br />'
		: '')
        : 'Hover over a borough');
};

info.addTo(map);

/*Finalizing Map */

var baseLayers = {
    "Grayscale": grayscale,
    "Streets": streets,
    "Outdoors": outdoors,
    "Dark Map": dark
    };

var overlays = {
    "Boroughs": boroughLayer, 
	"Heat Map": heat
}; 
 
L.control.layers(baseLayers, overlays,{collapsed:false}).addTo(map); 

/** Borough boundary only by default **/

var defaultBorough = LoadGeoJSON("data/london_boroughs.geojson")

L.geoJson(defaultBorough, {
	style: {
		opacity: 1,
		fillOpacity: 0
	}
}).addTo(map);