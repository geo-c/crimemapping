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
		/*var CrimeLocation= JSON.parse(JSONtext);*/
		console.log(JSONtext)
		
		for (var key in JSONtext){
		CrimeLatLon.push(new coordinate(JSONtext[key].lon.value,JSONtext[key].lat.value));
		}
		/*for (var key in CrimeLocation.results.bindings){
		CrimeLatLon.push(new coordinate(CrimeLocation.results.bindings[key].lat.value,CrimeLocation.results.bindings[key].lon.value));
		}*/
		
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



/**
 *  Leaflet map
 */
var map = L.map('map', {
	zoomControl : true
}).setView([51.509865, -0.118092], 10);

L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	maxZoom : 18,
	attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
	'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, '
	/* +
	'Imagery <a href="http://mapbox.com">Mapbox</a>',
	/*id : 'mapbox.streets'*/
}).addTo(map);

// add boroughs layer to map
$.getJSON("data/london_boroughs.geojson",function(boroughData){
    L.geoJson( boroughData, {
        style: function(){
            return { color: "#1c1499", weight: 2, fillOpacity: .0 };
        }/*,
		 // todo: show names of the boroughs?
		 onEachFeature: function( feature, layer ){
		 layer.bindPopup( "<strong>" + feature.properties.name + "</strong><br/>" )
		 }*/
    }).addTo(map);
});
