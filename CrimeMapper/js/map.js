var CrimeLatLon = [];
var CrimeHeat = [];
/*vars for parliament query*/
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";
/*Libraries*/
var sqlPrefixes = "\
PREFIX crime: <http://course.geoinfo2016.org/G3/>\n\
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n";

function coordinate(x, y) {
    this.x = parseFloat(x);
    this.y = y;
}


function buildCrimeLocQuery(){
	var query = sqlPrefixes + "\
		SELECT ?crime ?lat ?lon\n\
		WHERE { GRAPH <http://course.geoinfo2016.org/G3> {\n\
		?crime geo:lat ?lat.\n\
		?crime geo:long ?lon.\n\
	}}LIMIT 80000";
	console.log(query)
	return query;
}

function askForCrimeLoc(query) {
	var url = sparqlUrl + encodeURIComponent(query); // encodeURI is not enough as it doesn't enocde # for example.
	console.log(url);
	 $.ajax({
		dataType: "jsonp",
		url: url,
		/*Success*/
		success: function(data){
		console.log(data)
		var JSONtext = data;
		console.log(JSONtext.results.bindings[1].lat.value);
		},
		/*error*/
		error: function (ajaxContext) {
		console.log(ajaxContext)
        alert(ajaxContext.responseText)
    }
	}).done(function(JSONtext) {
		console.log("done")		
		for (var key in JSONtext.results.bindings){
		CrimeLatLon.push(new coordinate(JSONtext.results.bindings[key].lat.value,JSONtext.results.bindings[key].lon.value));
		}	
		console.log(CrimeLatLon)
		for (var i = 1; i < CrimeLatLon.length; i++) {
			CrimeHeat.push([CrimeLatLon[i].x, CrimeLatLon[i].y,0.5])
		}
		var heat = L.heatLayer(CrimeHeat, {radius: 10})
			.addTo(map);	
	
	});
}

document.getElementById('clickMe').onclick = function(){
	console.log("here");
    askForCrimeLoc(buildCrimeLocQuery());
  };




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

/* Load geoJSON from file synchronously */

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

	var boroughs = LoadGeoJSON("data/london_boroughs.geojson");
	boroughLayer = L.geoJson(boroughs).addTo(map);



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
    "Boroughs": boroughLayer //todo: fix boroughs variable scope so this will work
}; 
 
L.control.layers(baseLayers, overlays).addTo(map); 