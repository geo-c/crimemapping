/*vars for heatmap*/
var CrimeLatLon = [];
var CrimeHeat = [];
/*Heatmap layer*/
var heat = new L.LayerGroup();
/*variable where the requestes JSON (data) will be stored in*/
var JSONtext;
/*vars for parliament query*/
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";
/*Libraries*/
var sqlPrefixes = "\
PREFIX crime: <http://course.geoinfo2016.org/G3/>\n\
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n";

/**
next is for heatmap ONLY
**/

/*Function to build the SPARQL-query for the heatmap*/
function buildCrimeLocQuery(){
	var query = sqlPrefixes + "\
		SELECT ?crime ?lat ?lon\n\
		WHERE { GRAPH <http://course.geoinfo2016.org/G3> {\n\
		?crime geo:lat ?lat.\n\
		?crime geo:long ?lon.\n\
	}}LIMIT 1000";
	console.log(query)
	return query;
}

/*function needed to to processing on the receives Crime coordinates*/
function coordinate(x, y) {
    this.x = parseFloat(x);
    this.y = y;
}

/*function to create the heatmap layer*/
function createHeatMap(JSONtext){
		for (var key in JSONtext.results.bindings){
		CrimeLatLon.push(new coordinate(JSONtext.results.bindings[key].lat.value,JSONtext.results.bindings[key].lon.value));
		}	
		for (var i = 1; i < CrimeLatLon.length; i++) {
			CrimeHeat.push([CrimeLatLon[i].x, CrimeLatLon[i].y,0.5])
		}
		L.heatLayer(CrimeHeat, {radius: 10})
			.addTo(heat);
}

/*If user presses Imprint Button with ID ClickMe, request is started)*/
document.getElementById('clickMe').onclick = function(){
	console.log("here");
    askForData(buildCrimeLocQuery());
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
  

function askForData(query) {
	var url = sparqlUrl + encodeURIComponent(query); // parse the whole URL containing the query
	console.log(url);
	 $.ajax({
		dataType: "jsonp",
		url: url,
		/*On Success the data reveived from parialemt is stored in the variable JSONtext*/
		success: function(data){
		console.log(data)
		var JSONtext = data;
		},
		/*On error exceptions will be printed in dialog box*/
		error: function (ajaxContext) {
		console.log(ajaxContext)
        alert(ajaxContext.responseText)
    }
	/*When request is done (.done) do something with it*/
	}).done(function(JSONtext) {
console.log("done");	
	createHeatMap(JSONtext);
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
}).setView([51.300465, -0.118092], 11); //default zoom and position (london)

/* Load geoJSON from file synchronously (!)*/

function LoadGeoJSON(data) {
        var json = null;
        $.ajax({
            async: false,
            global: false,
            url: data,
            dataType: "json",
            success: function (data) {
                json = data;
            }
        });
        return json;
    }

/*
Determine color of borough polygon according to crime rate 
*/

function getColor(d) {
    return d > 1000 ? '#800026' :
           d > 500  ? '#BD0026' :
           d > 200  ? '#E31A1C' :
           d > 100  ? '#FC4E2A' :
           d > 50   ? '#FD8D3C' :
           d > 20   ? '#FEB24C' :
           d > 10   ? '#FED976' :
                      '#FFEDA0';
}

/*
Set colors for borough layer
*/
function style(feature) {
    return {
        //fillColor: getColor(feature.properties.density),
        weight: 2,
        opacity: 1,
        color: 'blue',
        dashArray: '3',
        fillOpacity: 0.2
    };
}

var boroughs = LoadGeoJSON("data/london_boroughs.geojson"); //Do not move

/*
Interactivity functions when hovering
*/
function highlightFeature(e) {
    var layer = e.target;

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

boroughLayer = L.geoJson(boroughs, {
    style: style,
    onEachFeature: onEachFeature
}).addTo(map);


var baseLayers = {
    "Grayscale": grayscale,
    "Streets": streets,
    "Outdoors": outdoors,
    "Satellite": satellite,
    "Satellite Streets": satellitestreets,
    "Dark Map": dark,
    "Light Map": light
    };

var overlays = {
    "Boroughs": boroughLayer, //todo: fix boroughs variable scope so this will work
	"Heatmap": heat
}; 
 
L.control.layers(baseLayers, overlays).addTo(map); 