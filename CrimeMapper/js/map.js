var boroughs;
/*vars for parliament query*/
var sparqlUrl = "http://giv-lodumdata.uni-muenster.de:8282/parliament/sparql?output=JSON&query=";
/*Libraries*/
var sqlPrefixes = "\
PREFIX crime: <http://course.geoinfo2016.org/G3/>\n\
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n";


function buildCrimeLocQuery(){
	var query = sqlPrefixes + "\
		SELECT ?crime ?lat ?lon\n\
		WHERE { GRAPH <http://course.geoinfo2016.org/G3> {\n\
		?crime geo:lat ?lat.\n\
		?crime geo:long ?lon.\n\
	}}";
	console.log(query)
	return query;
}

function askForCrimeLoc(query) {
	var url = sparqlUrl + encodeURIComponent(query); // encodeURI is not enough as it doesn't enocde # for example.
	 $.ajax({
		dataType: "jsonp",
		url: url,
		success: function(data){
		var JSONtext = data.responseJSON;
		console.log(data.results.bindings)
		},
		error: function (ajaxContext) {
        alert(ajaxContext.responseText)
    }
	});
}

  $(document).ready(function(){
    $('#clickMe').click(function(){
    askForCrimeLoc(buildCrimeLocQuery());
    });
  });

/*vars for heatMap*/
/**
var CrimeLatLon = [];
var CrimeHeat = [];

function coordinate(x, y) {
    this.x = parseFloat(x);
    this.y = y;
}
var url= "http://giv-oct.uni-muenster.de:8080/api/dataset/crimeLoc?authorization=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBfbmFtZSI6IkNyaW1lIE1hcHBlciIsImlhdCI6MTQ4MDY3ODM5Nn0.G8E9xAK7OoXhjpmc2RPd4ZMzqbA-6P38rONMc4H6_Ng"
var jqxhr = $.getJSON( url, function() {
  	console.log( "success" );
	}).done(function() {
		console.log("second success");
		var JSONtext = jqxhr.responseJSON;
		console.log(JSONtext)
		
		for (var key in JSONtext){
		CrimeLatLon.push(new coordinate(JSONtext[key].lat.value,JSONtext[key].lon.value));
		}
		
		for (var i = 1; i < CrimeLatLon.length; i++) {
			CrimeHeat.push([CrimeLatLon[i].x, CrimeLatLon[i].y,1])
		}
		console.log(CrimeHeat);
		var heat = L.heatLayer(CrimeHeat, {radius: 20})
			.addTo(map);
			
			
	}).fail(function() {
			console.log( "error" );
	}).always(function() {
});

*/

/**
 *  Leaflet map
 */
// add boroughs layer to map
$.getJSON("data/london_boroughs.geojson",function(boroughData){
    boroughs = L.geoJson( boroughData, {
        style: function(){
            return { color: "#1c1499", weight: 2, fillOpacity: .0 };
        }/*,
		 // todo: show names of the boroughs?
		 onEachFeature: function( feature, layer ){
		 layer.bindPopup( "<strong>" + feature.properties.name + "</strong><br/>" )
		 }*/
    }).addTo(map);
});


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
}).setView([51.509865, -0.118092], 10); //default zoom and position (london)

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
    "Boroughs": boroughs //todo: fix boroughs variable scope so this will work
};


L.control.layers(baseLayers).addTo(map); 